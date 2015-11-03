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

import spacebar.backgammoid.model.Board.Player;

public class Move {
    private Board.Player player;
    private int orig;
    private int dest;
    private Dice dice;

    /*
        Movements keep track of the dice used to perform them
     */
    //TODO think about a way to get rid of the dice
    public Move(int source, Dice dice, Player player) {
        this.orig = source;
        this.dice = dice;
        this.player = player;

        switch (player) {
            case P2:
                // When coming from the bar, the chip is considered to come from one point further than the last one
                if (this.orig == Board.BAR) this.dest = Board.LAST_POINT - dice.getValue() + 1;
                    //When going out of the board, destination is the goal
                else if ((source - dice.getValue()) < Board.FIRST_POINT) this.dest = Board.GOAL_P2;
                else this.dest = source - dice.getValue();
                break;
            case P1:
                // When coming from the bar, the chip is considered to come from one point fewer than the first one
                if (orig == Board.BAR) this.dest = Board.FIRST_POINT + dice.getValue() - 1;
                    //When going out of the board, destination is the goal
                else if ((source + dice.getValue()) > Board.LAST_POINT) this.dest = Board.GOAL_P1;
                else this.dest = source + dice.getValue();
                break;
            default:
                break;
        }
    }

    public int getOrig() {
        return orig;
    }

    public int getDest() {
        return dest;
    }

    public Dice getDice() {
        return dice;
    }

    public Board.Player getPlayer() {
        return player;
    }

}

