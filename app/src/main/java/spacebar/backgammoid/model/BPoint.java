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

import java.util.ArrayList;
import java.util.Iterator;

import spacebar.backgammoid.model.Board.Player;

/**
 * Every point in the board game is a BPoint,
 * containing (or not) up to MAX_POINT_CAPACITY
 * belonging to any of the players.
 * <p>
 * It's up to the controller to decide whether a point is
 * allowed to contain both player's chips (which is the case for the central bar)
 */
public class BPoint {
    /**
     * MAX_POINT_CAPACITY
     */
    private static int MAX_POINT_CAPACITY = 5;
    protected ArrayList<Player> chips = new ArrayList<>();

    public void pushChip(Player player) {
        chips.add(player);
    }

    /* Remove last belonging to a player from the point */
    public Player popChip(Player player) {
        return chips.remove(chips.lastIndexOf(player));
    }

    /**
     * The point is full whenever there's at least MAX_POINT_CAPACITY chips on it
     *
     * @return true if it's full
     */
    public boolean isFull() {
        return (chips.size() >= MAX_POINT_CAPACITY);
    }

    /**
     * A point if empty if it contains no chips
     *
     * @return true if it's empty
     */
    public boolean isEmpty() {
        return chips.isEmpty();
    }

    /**
     * A point is a blot if it contains a single chip
     *
     * @return true if it's a blot
     */
    public boolean isBlot() {
        return (chips.size() == 1);
    }

    /**
     * @return the number of chips in the point
     */
    public int countChips() {
        return chips.size();
    }

    /**
     * Checks if a point contains chips of a given player
     *
     * @param player player whose chips are being seeked
     * @return true if the point contains chips belonging to player
     */
    public boolean hasChipsOf(Player player) {
        return (chips.contains(player));
    }

    /**
     * Checks if a player is allowed to put chips on this point
     *
     * @return true if the player is allowed to put chips on the point
     */
    public boolean isAccessibleToPlayer(Player player) {
        return !this.isFull() && !(this.hasChipsOf(player.opponent()) && !this.isBlot());
    }

    /**
     * Get an iterator for the point
     *
     * @return A player iterator for the point contents
     */
    public Iterator<Player> iterator() {
        return chips.iterator();
    }
}

