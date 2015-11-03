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

/**
 * Bar is the central game board bar, which conceptually is
 * no more than a point allowed to contain any number of both player's chips
 */
public class Bar extends BPoint {
    /**
     * The point is never full
     *
     * @return false
     */
    public boolean isFull() {
        return false;
    }

    /**
     * The bar is always happy to welcome any player's chips
     * she is kinda nice.
     *
     * @param player the player that will put some chip in the bar
     * @return always true
     */
    public boolean isAccessibleToPlayer(Player player) {
        return true;
    }
}
