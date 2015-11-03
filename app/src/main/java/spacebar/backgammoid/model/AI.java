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

package spacebar.backgammoid.model;

import android.util.Log;

import spacebar.backgammoid.GameActivity;
import spacebar.backgammoid.controller.Backgammon;

public class AI extends Thread {
    private GameActivity match;
    private Backgammon backgammon;
    private Move chosenMove;
    private Board.Player impersonate;
    private Object sync = new Object();


    public AI(GameActivity match, Board.Player impersonate) {
        this.match = match;
        this.backgammon = match.getBackgammon();
        this.impersonate = impersonate;
    }

    /**
     * <p>Check all possible moves and choose the most appropriate one.</p>
     *
     * @return Best possible makeMove
     */
    //TODO consider two moves, you'll need to add some "predicted possible moves" to the board class
    public Move decideMove() {
        int aux = 0;
        Move bestMove = null;

        for (Move move : backgammon.getPossibleMoves()) {
            int moveWeight = evaluateMove(move);
            if (moveWeight >= aux) {
                aux = moveWeight;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * <p>Heuristics for each makeMove</p>
     *
     * @return Calculated weight for evaluated move
     */
    private int evaluateMove(Move move) {
        BPoint dest = backgammon.getPoint(move.getDest());
        BPoint orig = backgammon.getPoint(move.getDest());

        int weight = 0;

        if (!backgammon.isValidMove(move)) return weight; //non valid moves get no points

        // Get points if the move will not create a blot
        if (orig.countChips() > 2) weight += 3;
        // Get 1 point if the move will move a chip that's a blot
        if (orig.isBlot()) {
            weight++;
            // And 2 extra points if the destination is not empty (will not remain a blot)
            if (!dest.isEmpty()) weight += 2;
        }

        // Avoiding leaving an isolated chip, get 2 points
        // Sending an opponents chip to the bar, get 3 extra points
        if (dest.isBlot()) {
            weight += 2;
            if (dest.hasChipsOf(move.getPlayer().opponent())) weight += 3;
        }

        // Get 5 points for sending a chip out of the board
        if (dest.getClass().equals(Goal.class) && backgammon.canRemove(move.getPlayer())) {
            weight += 8; // Sending a chip out of the board gives 5 points
        }

        return weight;
    }

    public void think() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeMove() {
        match.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (sync) {
                    match.makeMove();
                    sync.notify();
                }
            }
        });
    }

    private void rollDice() {
        Log.d("Stack : ", "AI.rollDice");
        match.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Stack : ", "AI.rollDice.runnableRun");
                synchronized (sync) {
                    match.rollDice();
                    sync.notify();
                }
            }
        });
    }

    /**
     * <p>
     * AI plays next automaton transition
     * </p>
     */
    public void play() {
        Log.d("TURN: ", "play :" + backgammon.getState().toString());

        switch (backgammon.getState()) {
            case INITIAL:
            case ROLLING_DICE:
                backgammon.nextState();
                Log.d("Stack: ", "Rolling dice");

                synchronized (sync) {
                    rollDice();
                    Log.d("Stack: ", "Rolling dice.sync");
                    try {
                        Log.d("Stack: ", "Waiting");
                        sync.wait();
                        Log.d("Stack: ", "Waited");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("AI Error", "Error waiting for UI update completion");
                    }
                }
                break;
            case LOOSES_TURN:
                backgammon.nextState();
                break;
            case CHOOSING_ORIGIN:
                chosenMove = decideMove();
                BPoint selectedOrig = backgammon.getPoint(chosenMove.getOrig());
                match.setSelectedOrig(selectedOrig);
                backgammon.nextState();
                break;
            case CHOOSING_DESTINATION:
                BPoint selectedDest = backgammon.getPoint(chosenMove.getDest());
                match.setSelectedDest(selectedDest);
                synchronized (sync) {
                    makeMove();
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("AI Error", "Error waiting for UI update completion");
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * AI will be running until it's turn is over, and then
     * be lost in time like tears in rain
     */
    public void run() {
        while (backgammon.currentTurn().equals(impersonate)) {
            think();
            play();
            match.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    match.reDraw();
                }
            });
        }
        // Notify the end of the turn
        match.setIsAITurn(false);
    }
}