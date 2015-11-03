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

package spacebar.backgammoid;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spacebar.backgammoid.controller.Backgammon;
import spacebar.backgammoid.model.AI;
import spacebar.backgammoid.model.BPoint;
import spacebar.backgammoid.model.Board;
import spacebar.backgammoid.model.Dice;
import spacebar.backgammoid.model.Move;

import static spacebar.backgammoid.controller.Backgammon.BState.INITIAL;

//TODO Keep AI running continuously
public class GameActivity extends ActionBarActivity {
    private PointMap pointMap;

    private BPoint selectedOrig;
    private BPoint selectedDest;
    private Backgammon backgammon;

    private boolean useAI = false;
    private boolean isAITurn = false;

    public void setIsAITurn(boolean isAITurn) {
        this.isAITurn = isAITurn;
    }

    public Backgammon getBackgammon() {
        return backgammon;
    }

    public void setSelectedOrig(BPoint point) {
        selectedOrig = point;
    }

    public void setSelectedDest(BPoint point) {
        selectedDest = point;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        backgammon = new Backgammon();
        pointMap = initHashMap();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("spacebar.backgammoid.AI")) {
            useAI = getIntent().getExtras().getBoolean("spacebar.backgammoid.AI");
        }
        //TODO keep board proportions
/*        LinearLayout board = (LinearLayout) findViewById(R.id.board);

        board.getLayoutParams().height = 16/9 * board.getWidth();*/

        populatePoints();
        reDraw();

        //TODO score
//        ((TextView) findViewById(R.id.score_label)).setText("Hola Mundo");
    }

    //TODO Make sure the game continues where it was when suspending the phone http://stackoverflow.com/questions/151777/saving-activity-state-in-android

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @return a map of the equivalence between model and view points
     */
    private PointMap initHashMap() {
        PointMap map = new PointMap();

        List<BPoint> points = backgammon.getPoints();
        Iterator<BPoint> pointsIterator = points.iterator();

        try {
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point1));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point2));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point3));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point4));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point5));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point6));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point7));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point8));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point9));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point10));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point11));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point12));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point13));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point14));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point15));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point16));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point17));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point18));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point19));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point20));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point21));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point22));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point23));
            map.put(pointsIterator.next(), (PointView) findViewById(R.id.point24));
            map.put(backgammon.getPoint(Board.BAR), (PointView) findViewById(R.id.bar));
            map.put(backgammon.getPoint(Board.GOAL_P1), (PointView) findViewById(R.id.goalP1));
            map.put(backgammon.getPoint(Board.GOAL_P2), (PointView) findViewById(R.id.goalP2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void flushSelectablePoints() {
        Iterator<PointView> pointViewIterator = pointMap.pointsLayoutIterator();

        while (pointViewIterator.hasNext()) {
            PointView pointView = pointViewIterator.next();

            pointView.setClickable(false);
            pointView.getBackground().clearColorFilter();
        }
    }

    private void flushPoints() {
        Iterator<PointView> pointViewIterator = pointMap.pointsLayoutIterator();

        while (pointViewIterator.hasNext()) {
            PointView pointView = pointViewIterator.next();

            pointView.removeAllViews();
            pointView.setClickable(false);
            pointView.getBackground().clearColorFilter();
        }
    }

    private void populatePoints() {
        Iterator<PointView> pointViewIterator = pointMap.pointsLayoutIterator();

        while (pointViewIterator.hasNext()) {
            PointView pointView = pointViewIterator.next();

            // Make sure that the background behaves differently for each point
            pointView.getBackground().mutate();
            //Make sure the chip animation will not be clipped by parent view
            pointView.setClipToPadding(false);
            pointView.setClipChildren(false);


            BPoint point = pointMap.bPointOf(pointView);

            Iterator<Board.Player> chipsIterator = point.iterator();

            while (chipsIterator.hasNext()) {
                Board.Player chip = chipsIterator.next();

                ChipView chipView = new ChipView(this.getApplicationContext());
                chipView.setOwner(chip);

                pointView.push(chipView);
            }
        }
    }

    private void activatePoint(final View point, int highlightColor) {
        point.getBackground().setColorFilter(highlightColor, PorterDuff.Mode.SRC_ATOP);

        if (!isAITurn) {
            point.setClickable(true);

            point.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPointTouch(point);
                }
            });
        }
    }

    private void drawSelectablePoints() {
        flushSelectablePoints();
        Iterator<Move> movesIterator = backgammon.getPossibleMoves().iterator();

        switch (backgammon.getState()) {
            case CHOOSING_ORIGIN:
                while (movesIterator.hasNext()) {
                    Move move = movesIterator.next();

                    int p = move.getOrig();

                    PointView pointView = pointMap.layoutOf(backgammon.getPoint(p));
                    activatePoint(pointView, 0xff7ba6ff);
                }
                break;
            case CHOOSING_DESTINATION:
                while (movesIterator.hasNext()) {
                    Move move = movesIterator.next();

                    int p = move.getOrig();

                    BPoint moveOriginPoint = backgammon.getPoint(p);

                    if (moveOriginPoint.equals(selectedOrig)) {
                        p = move.getDest();

                        PointView destPointView = pointMap.layoutOf(backgammon.getPoint(p));
                        activatePoint(destPointView, 0xff7ba6ff);
                    }
                }
                activatePoint(pointMap.layoutOf(selectedOrig), 0xffffa600);
                break;
            default:
                //Nothing to do in any other case
                break;
        }
    }

    private void drawDice() {
        Dice[] roll = backgammon.getRoll();

        DieView dice1view = (DieView) findViewById(R.id.dice1);
        DieView dice2view = (DieView) findViewById(R.id.dice2);

        float alpha = roll[0].isHalfused() ? (float) 0.5 : (float) 1;
        alpha = roll[0].isUsed() ? (float) 0 : alpha;
        dice1view.setAlpha(alpha);

        alpha = roll[1].isHalfused() ? (float) 0.5 : (float) 1;
        alpha = roll[1].isUsed() ? (float) 0 : alpha;
        dice2view.setAlpha(alpha);
    }

    private void drawToast() {
        String message = backgammon.currentTurn().toString() + " : ";

        switch (backgammon.getState()) {
            case INITIAL:
                message = message + "Roll dice";
                break;
            case LOOSES_TURN:
                message = message + "No possible moves";
                backgammon.nextState();
                break;
            default:
                return;
        }
        Toast toast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void makeMove() {
        if (selectedOrig != null && selectedDest != null) {
            final Board.Player turn = backgammon.currentTurn();

            // Get the layouts of the points involved on the move
            PointView originPointView = pointMap.layoutOf(selectedOrig);
            PointView destPointView = pointMap.layoutOf(selectedDest);

            // Get the moving chip before the turn can change
            // to avoid getting the wrong chip
            ChipView movingChip = originPointView.pop(turn);

            // Don't make the backgammon transit if we are reacting to
            // a move that ate a chip belonging to the opponent
            if (selectedDest != backgammon.getPoint(Board.BAR)) {
                // Perform the movement
                try {
                    backgammon.setNextMove(selectedOrig, selectedDest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                backgammon.nextState();

                // Check if a chip has to be sent to the bar, and send it
                // before putting the new chip if so
                if (backgammon.isHasEaten()) {
                    selectedOrig = selectedDest;
                    selectedDest = backgammon.getPoint(Board.BAR);
                    makeMove();
                }
            }
            // Put the chipView on it's final position, causing
            // the animation to trigger
            destPointView.push(movingChip);

            // Flush origin and destination selection
            selectedOrig = null;
            selectedDest = null;
        }
    }

    public void reDraw() {
        drawDice();
        drawSelectablePoints();
        drawToast();
    }

    /**
     * Decides if it's AI turn, instantiates it and makes it play
     */
    // TODO this should not be done, AI is supposed to detect it's own turn and block the UI (avoid user interacting with the points and dice)
    private void victorsTurn() {
        if (useAI && backgammon.currentTurn().equals(Board.Player.P2)) {
            // Flag used to avoid the user interacting with the UI while the IA plays
            isAITurn = true;
            AI victor = new AI(this, Board.Player.P2);
            victor.start();
        }
    }

    public void onPointTouch(View touchedPoint) {
        switch (backgammon.getState()) {
            case CHOOSING_ORIGIN:
                backgammon.nextState();
                selectedOrig = pointMap.bPointOf(touchedPoint);
                reDraw();
                break;
            case CHOOSING_DESTINATION:
                selectedDest = pointMap.bPointOf(touchedPoint);

                /* If the selected point is the one that was already selected, backgammon transits to previous state */
                if (selectedDest.equals(selectedOrig)) {
                    backgammon.previousState();
                    selectedDest = null;
                    selectedOrig = null;
                    reDraw();
                    break;
                }

                makeMove();
                reDraw();
                victorsTurn();
                break;
            default:
                break;
        }
    }

    public void rollDice() {
        Dice[] roll = backgammon.getRoll();

        switch (backgammon.getState()) {
            case INITIAL:
            case ROLLING_DICE:
                backgammon.nextState(); // Make backgammon transit to next state (and roll the dice)
                break;
            default:
        }

        DieView dice1view = (DieView) findViewById(R.id.dice1);
        DieView dice2view = (DieView) findViewById(R.id.dice2);

        dice1view.roll(roll[0].getValue());
        dice2view.roll(roll[1].getValue());

        reDraw();
        Log.d("rollDice : ", "diceRoll made. Values : (" + roll[0].getValue() + "," + roll[1].getValue() + ")");
    }

    public void onDiceTouch(View view) {
        // Avoid responding to user input during AI turn
        if (!isAITurn) {
            rollDice();
            victorsTurn();
        }
    }

    /**
     * Class of the 1:1 map
     */
    private static class PointMap {
        private HashMap<BPoint, PointView> map = new HashMap<>();
        private HashMap<PointView, BPoint> inverseMap = new HashMap<>();

        /**
         * Add a new item to the map
         *
         * @param point
         * @param layout
         * @throws Exception
         */
        public void put(BPoint point, PointView layout) throws Exception {
            if (map.get(point) != null || inverseMap.get(layout) != null)
                throw new Exception("Item is already mapped");

            this.map.put(point, layout);
            this.inverseMap.put(layout, point);
        }

        /**
         * Add a new item to the map
         *
         * @param layout
         * @param point
         * @throws Exception
         */
        public void put(PointView layout, BPoint point) throws Exception {
            this.put(point, layout);
        }

        /**
         * Get the model point associated to the layout point
         *
         * @param layoutPoint layout Point whose equivalent model point is to be found
         * @return mapped BPoint
         */
        public BPoint bPointOf(View layoutPoint) {
            return this.inverseMap.get(layoutPoint);
        }

        /**
         * Get the layout point associated to the model point
         *
         * @param point model Point whose equivalent layout point is to be found
         * @return mapped PointView
         */
        public PointView layoutOf(BPoint point) {
            return this.map.get(point);
        }

        public Iterator<BPoint> pointsIterator() {
            return this.map.keySet().iterator();

        }

        public Iterator<PointView> pointsLayoutIterator() {
            return this.map.values().iterator();
        }
    }
}
