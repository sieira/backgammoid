/*
Copyright © 2015 Luis Sieira Garcia

 This file is part of Backgammoid.

    Backgammoid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Backgammoid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Backgammoid.  If not, see <http://www.gnu.org/licenses/>.
 */


package spacebar.backgammoid.controller;

import java.util.ArrayList;
import java.util.List;

import spacebar.backgammoid.model.BPoint;
import spacebar.backgammoid.model.Board;
import spacebar.backgammoid.model.Dice;
import spacebar.backgammoid.model.Move;

public class Backgammon {
    /**
     * board Game board
     */
    private Board board = new Board();
    /**
     * state current game state
     */
    private BState state;
    /**
     * turn current player
     */
    private Board.Player turn;
    /**
     * nextMove Move to perform during the change of state
     */
    private Move nextMove;
    private boolean hasEaten = false;

    public Backgammon() {
        state = BState.INITIAL;
        turn = Board.Player.P1;
    }

    public Dice[] getRoll() {
        return board.getDice();
    }

    /*
        public Board getBoard() {
            return board;
        }
    */
    public List<BPoint> getPoints() {
        return board.getPoints();
    }

    public BPoint getPoint(int index) {
        return board.getPoint(index);
    }

    public boolean canRemove(Board.Player player) {
        return board.canRemove(player);
    }

    public boolean isValidMove(Move move) {
        return board.isValidMove(move);
    }

    public BState getState() {
        return state;
    }

    public Board.Player currentTurn() {
        return turn;
    }

    public void setNextMove(BPoint orig, BPoint dest) throws Exception {
        setNextMove(board.indexOf(orig), board.indexOf(dest));
    }

    /**
     * Allows the view to store the next makeMove to perform
     * throws an exception if the movement is not a valid one
     *
     * @param orig origin point
     * @param dest destination point
     */
    public void setNextMove(int orig, int dest) throws Exception {
        // Choose the first available dice making the makeMove
        //TODO choose the smallest die when exiting the board
        for (Dice die : board.getDice()) {
            Move move = new Move(orig, die, turn);

            if (move.getDest() == dest && board.isValidMove(move)) {
                setNextMove(move);
                return;
            }
        }
        throw new Exception("Impossible move");
    }

    public void setNextMove(Move move) {
        this.nextMove = move;
    }

    /**
     * @return true if the las move ate an opponents chip
     */
    public boolean isHasEaten() {
        return hasEaten;
    }

    /**
     * Iterates over every point to decide if there is any possible movement for
     * the current player with the current dice roll
     *
     * @return List of possible moves for the current player with the current
     * dice values
     */
    public ArrayList<Move> getPossibleMoves() {
        int dest = 0;

        ArrayList<Move> moves = new ArrayList<Move>();

        // If there are chips of the current player in the bar, these are the
        // only possible moves
        if (board.hasInBar(turn)) {
            for (Dice die : board.getDice()) {
                Move move = new Move(Board.BAR, die, turn);
                if (board.isValidMove(move)) moves.add(move);
            }
        } else {
            for (int source = 0; source < Board.N_POINTS; source++) {
                for (Dice die : board.getDice()) {
                    Move move = new Move(source, die, turn);
                    if (board.isValidMove(move)) moves.add(move);
                }
            }
        }
        return moves;
    }

    /**
     * <p>
     * Transit the automaton to the next state
     * </p>
     */
    public void nextState() {
        switch (state) {
            case INITIAL:
            case ROLLING_DICE:
                board.rollDice();
                if (getPossibleMoves().isEmpty())
                    state = BState.LOOSES_TURN;
                else
                    state = BState.CHOOSING_ORIGIN;
                break;
            case LOOSES_TURN:
                state = BState.INITIAL;
                turn = turn.opponent();
                break;
            case CHOOSING_ORIGIN:
                state = BState.CHOOSING_DESTINATION;
                break;
            case CHOOSING_DESTINATION:
                try {
                    board.moveChip(nextMove);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hasEaten = board.hasEatenOnLastMove();

                if (board.hasWon(turn)) {
                    state = BState.VICTORY;
                    break;
                }
                if (board.getDice()[0].isUsed() && board.getDice()[1].isUsed()) {
                    turn = turn.opponent();
                    state = BState.INITIAL;
                } else if (getPossibleMoves().isEmpty()) state = BState.LOOSES_TURN;
                else state = BState.CHOOSING_ORIGIN;
                break;
            default:
                break;
        }
    }

    /**
     * <p>
     * Transit the automaton to the previous state
     * </p>
     */
    public void previousState() {
        switch (state) {
            case ROLLING_DICE:
                state = BState.INITIAL;
                break;
            case LOOSES_TURN:
            case CHOOSING_ORIGIN:
                state = BState.ROLLING_DICE;
                break;
            case CHOOSING_DESTINATION:
                state = BState.CHOOSING_ORIGIN;
                break;
            default:
                break;
        }
    }

    /**
     * <p>
     * Possible game states
     * </p>
     */
    public enum BState {
        INITIAL, ROLLING_DICE, CHOOSING_ORIGIN, CHOOSING_DESTINATION, LOOSES_TURN, VICTORY, SAVING;

        @Override
        public String toString() {
            switch (this) {
                case INITIAL:
                    return "initial";
                case ROLLING_DICE:
                    return "rolling dice";
                case CHOOSING_ORIGIN:
                    return "choosing origin";
                case CHOOSING_DESTINATION:
                    return "choosing destination";
                case LOOSES_TURN:
                    return "looses turn";
                case VICTORY:
                    return "victory";
                default:
                    return "unknown state";
            }
        }
    }
}