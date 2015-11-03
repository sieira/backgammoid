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

public class Goal extends BPoint {
    /*
    When called without a board context, the goal is not accessible
    @deprecated You're supposed to give me a board to check, you fool
     */
    @Deprecated
    public boolean isAccessibleToPlayer(Player player) {
        return false;
    }

    public boolean isAccessibleToPlayer(Board board, Player player) {
        return board.canRemove(player);
    }
}
