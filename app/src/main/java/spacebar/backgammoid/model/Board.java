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

import java.util.Arrays;
import java.util.List;

/**
 * A game board
 */
public class Board {

    /**
     * The total number of points in the board
     */
    public final static int N_POINTS = 24;
    /**
     * Indexes of the first and last point
     */
    public final static int FIRST_POINT = 0;
    public final static int LAST_POINT = 23;
    /**
     * Arbitrary index given to the central bar
     */
    public final static int BAR = -100;
    /**
     * Goal index for each player
     */
    public final static int GOAL_P1 = LAST_POINT + 1;
    public final static int GOAL_P2 = FIRST_POINT - 1;
    /**
     * Board's central bar
     */
    public Bar bar = new Bar();
    /**
     * Board's goals
     */
    public Goal goalP1 = new Goal();
    public Goal goalP2 = new Goal();
    /**
     * points Board points
     */
    private BPoint points[] = new BPoint[N_POINTS];
    /**
     * Current value of game dice
     */
    private Dice dice[] = new Dice[2];
    /**
     * Whether dice have the same value, and thus can be used twice
     */
    private boolean doubles = false;
    /**
     * Check if some chip was sent to the bar on the last move
     */
    private boolean hasEaten = false;

    /**
     * Constructor
     */
    public Board() {
        // Initialize dice
        dice = new Dice[]{new Dice(), new Dice()};

        // Initialize points
        for (int ii = 0; ii < 24; ii++) {
            points[ii] = new BPoint();
        }

        // Initialize the central bar
        bar = new Bar();

        /*
         * Test goal
         *
        points[LAST_POINT -1].pushChip(Player.P1);
        points[LAST_POINT -1].pushChip(Player.P1);
        points[LAST_POINT -2].pushChip(Player.P1);
        points[LAST_POINT -5].pushChip(Player.P1);
        points[LAST_POINT -6].pushChip(Player.P1);
        points[FIRST_POINT].pushChip(Player.P2);
        points[FIRST_POINT].pushChip(Player.P2);
        points[FIRST_POINT + 5].pushChip(Player.P2);
        points[FIRST_POINT + 6].pushChip(Player.P2);/**/

        /**
         * Chips are distributed for each player
         * 	2 in the first point
         * 	5 in the twelfth
         * 	3 in the seventeenth
         * 	5 in the nineteenth
         */
        for (int i = 0; i < 2; i++) {
            points[FIRST_POINT].pushChip(Player.P1);
            points[LAST_POINT].pushChip(Player.P2);
        }

        for (int i = 0; i < 5; i++) {
            points[11].pushChip(Player.P1);
            points[12].pushChip(Player.P2);
        }

        for (int i = 0; i < 3; i++) {
            points[16].pushChip(Player.P1);
            points[7].pushChip(Player.P2);
        }

        for (int i = 0; i < 5; i++) {
            points[18].pushChip(Player.P1);
            points[5].pushChip(Player.P2);
        }/**/
    }

    /**
     * @return List of points in the board
     */
    public List<BPoint> getPoints() {
        return Arrays.asList(points);
    }

    public int indexOf(BPoint point) {
        if (point.getClass().equals(Bar.class)) return BAR;

        if (point.equals(goalP1)) return GOAL_P1;
        if (point.equals(goalP2)) return GOAL_P2;

        return (Arrays.asList(points).indexOf(point));
    }

    public BPoint getPoint(int index) {
        if (index == BAR) return bar;
        if (index == GOAL_P1) return goalP1;
        if (index == GOAL_P2) return goalP2;

        if ((index >= FIRST_POINT) && (index <= LAST_POINT)) return points[index];

        return null;
    }

    /**
     * Returns a copy of the dice array
     *
     * @return dice
     */
    public Dice[] getDice() {
        return dice.clone();
    }

    /**
     * Rolls the dice, and checks if it's a double roll, updates
     * doubles in consequence
     */
    public void rollDice() {
        doubles = (dice[0].roll() == dice[1].roll());
    }

    /**
     * @return true if a chip was eating during the last move
     */
    public boolean hasEatenOnLastMove() {
        return hasEaten;
    }

    /**
     * Checks if a given player has won
     *
     * @param player presumed winner
     * @return true if the player doesn't have any chip left in the board
     */
    public boolean hasWon(Player player) {
        if (hasInBar(player))
            return false;

        for (BPoint point : points) {
            if (point.hasChipsOf(player)) return false;
        }
        return true;
    }

    /**
     * TODO Following two methods could belong to Backgammon class, looks like it should be part of the controller instead of the model I hate when it's not clear
     */

    /**
     * Checks if a given player has any chip left in the bar
     *
     * @param player Player for whose chips will be sought within the central bar
     * @return True if there are any player's chips in the bar
     */
    public boolean hasInBar(Player player) {
        return bar.hasChipsOf(player);
    }

    /**
     * Checks if a player is allowed to remove chips from the board
     *
     * @param player Player who's to check
     * @return true if all the player's chips remaining in the board are in his internal field
     */
    public boolean canRemove(Player player) {
        // If there is any player's chip in the bar, he is not allowed to remove
        if (hasInBar(player)) return false;

        int nonHomeBoardMin;
        int nonHomeBoardMax;

        if (player == Player.P1) {
            nonHomeBoardMin = FIRST_POINT;
            nonHomeBoardMax = LAST_POINT - 5;
        } else {
            nonHomeBoardMin = FIRST_POINT + 6;
            nonHomeBoardMax = LAST_POINT + 1;
        }

        // If there is any chip out of the home board, the player is not allowed
        for (BPoint bPoint : getPoints().subList(nonHomeBoardMin, nonHomeBoardMax))
            if (bPoint.hasChipsOf(player))
                return false;

        return true;
    }

    /**
     * Checks if a given movement is valid
     *
     * @param move makeMove whose validity is to check
     * @return true if it's a valid movement
     */
    public boolean isValidMove(Move move) {
        int origin = move.getOrig();
        int destination = move.getDest();
        Dice dice = move.getDice();
        Player player = move.getPlayer();

/*		if (!(!dice.isUsed())) {}
        // Move is performed only if origin is an existing point
		else if (!((origin >= FIRST_POINT && origin <= LAST_POINT) || origin == BAR)) { Log.i("a","uno");}
		// Move is performed only if origin point contains player's chips
		else if (!((origin == BAR && bar.hasChipsOf(player)) || (origin != BAR && points[origin].hasChipsOf(player)))){Log.i("a","dos");}
		// Move is performed only if only if destination is an existing point, or destination is out of the board but the player can remove his chips
		else if (!(((destination >= FIRST_POINT && destination <= LAST_POINT) || destination == BAR) || canRemove(player))){Log.i("a","tres");}
		//Move is valid only if the player doesn't have chips in the bar,or the origin is the bar itself
		else if (!(origin == BAR || !bar.hasChipsOf(player))){Log.i("a","cuatro");}
		// Move is performed only if destination point is accessible to player (the bar always is)
		else if (!(destination == BAR ||((destination>LAST_POINT || destination<FIRST_POINT) && canRemove(player))|| points[destination].isAccessibleToPlayer(player))){Log.i("a","cinco");}
	*/

		/*
        WARNING!! this statement uses lazy evaluation, changing the order ot the conditions may result in apocalypse
		 */
        return !dice.isUsed()
                // Move is performed only if origin is an existing point
                && ((origin >= FIRST_POINT && origin <= LAST_POINT) || origin == BAR)
                // Move is performed only if origin point contains player's chips
                && ((origin == BAR && bar.hasChipsOf(player)) || (origin != BAR && points[origin].hasChipsOf(player)))
                // Move is performed only if only if destination is an existing point, or destination is out of the board but the player can remove his chips
                && (((destination >= FIRST_POINT && destination <= LAST_POINT) || destination == BAR) || canRemove(player))
                //Move is valid only if the player doesn't have chips in the bar,or the origin is the bar itself
                && (origin == BAR || !bar.hasChipsOf(player))
                // Move is performed only if destination point is accessible to player (the bar always is)
                && (destination == BAR || ((destination > LAST_POINT || destination < FIRST_POINT) && canRemove(player)) || points[destination].isAccessibleToPlayer(player));
    }

    /**
     * Moves a chip from one point to another if this movement is possible
     *
     * @param move
     * @return true if a chip was sent to the bar
     */
    // TODO scoring
    public void moveChip(Move move) throws Exception {
        int origin = move.getOrig();
        int destination = move.getDest();
        Dice dice = move.getDice();
        Player player = move.getPlayer();

        // If the makeMove is not valid
        if (!isValidMove(move)) {
            throw new Exception("Not a valid move");
        }

        this.hasEaten = false;

        // The origin chip is popped from the corresponding point
        if (origin != BAR) {
            points[origin].popChip(player);
        } else {
            bar.popChip(player);
        }

        // And pushed in the destination point
        if (destination == GOAL_P1) {
            goalP1.pushChip(player);
        }

        if (destination == GOAL_P2) {
            goalP2.pushChip(player);
        }

        if (destination != BAR && destination != GOAL_P1 && destination != GOAL_P2) {
            // If destination point is a blot belonging to the opponent, it's last chip is popped to the bar
            if (points[destination].isBlot() && points[destination].hasChipsOf(player.opponent())) {
                bar.pushChip(points[destination].popChip(player.opponent()));
                this.hasEaten = true;
            }

            points[destination].pushChip(player);
        }

        // If it's a double roll, only half a dice is consumed for the movement
        if (doubles) {
            if (dice.isHalfused()) {
                dice.use();
            } else {
                dice.setHalfUsed(true);
            }
        } else {
            dice.use();
        }
    }

    /**
     * A board is to be used by two opposing players
     */
    public enum Player {
        P1, P2;

        /**
         * Returns the opponent of this player
         *
         * @return the opponent of this player
         */
        public Player opponent() {
            if (this == P1)
                return P2;
            else
                return P1;
        }
    }
}