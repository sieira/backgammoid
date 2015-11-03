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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import spacebar.backgammoid.model.Board;

/**
 * Represents a backgammon chip
 */
public class ChipView extends ImageView {
    private Board.Player owner = null;
    private int[] currentLocation = null;


    /**
     * Clone constructor
     * Creates a dumb chip intended to be used for
     * animation.
     */
    public ChipView(ChipView orig) {
        super(orig.getContext());

        int[] origLocation = new int[2];
        orig.getLocationOnScreen(origLocation);

        Log.d("Position : ", "(" + String.valueOf(origLocation[0]) + "," + String.valueOf(origLocation[1]) + ")");

        // Set dimensions and position
        this.setTop(origLocation[1]);
        this.setLeft(origLocation[0]);
        this.setRight(origLocation[0] + orig.getWidth());
        this.setBottom(origLocation[1] + orig.getHeight());

        // Scale the drawable
        int dHeight = orig.getDrawable().getIntrinsicHeight();
        int dWidth = orig.getDrawable().getIntrinsicWidth();
        int vHeight = orig.getHeight();
        int vWidth = orig.getWidth();

        float aspectRatio = dWidth / (float) dHeight;

        dWidth = vWidth;
        dHeight = (int) (vWidth * aspectRatio);

        if (dHeight > vHeight) {
            dWidth = (int) (vHeight * aspectRatio);
            dHeight = vHeight;
        }

        int dLeft = (int) ((float) vWidth / 2f - (float) dWidth / 2f);
        int dTop = (int) ((float) vHeight / 2f - (float) dHeight / 2f);

        // Center the drawable
        this.setImageDrawable(orig.getDrawable().getConstantState().newDrawable().mutate());
        this.getDrawable().setBounds(
                dLeft,
                dTop,
                dWidth + dLeft,
                dHeight + dTop
        );
    }

    public ChipView(Context context) {
        super(context);
        init(context, null);
    }

    public ChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * @return The player who owns the chip
     */
    public Board.Player getOwner() {
        return owner;
    }

    /**
     * Assigns an owner to the chip
     *
     * @param owner The player who will control the chip
     */
    public void setOwner(Board.Player owner) {
        Drawable chip;

        if (owner == Board.Player.P1) chip = getResources().getDrawable(R.drawable.red_chip);
        else chip = getResources().getDrawable(R.drawable.blue_chip);

        this.setImageDrawable(chip);
        this.owner = owner;
    }

    /**
     * Gets the attributes and processes them. Also, adds an onLayoutChangeListener
     * that will trigger a translate animation whenever the chip layout changes
     * (except for the first time)
     *
     * @param context Context of this View (it could be gotten form the parent as well)
     * @param attrs   Attributes of this view (they could not have been passed to the constructor,
     *                so it is a nullable parameter.
     */
    // TODO prevent the animation to trigger when turning the phone
    public void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.ChipView);
            int player = a.getInt(R.styleable.ChipView_chip_owner, 0);

            if (player == 0) this.setOwner(Board.Player.P1);
            else this.setOwner(Board.Player.P2);

            a.recycle();
        }

        // Add a layout listener in order to perform the animation
        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(final View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //Get the destination location for the chip
                int[] newLocation = new int[2];
                v.getLocationOnScreen(newLocation);
                /* Animation is not performed when populating the points,
                 * nor when calling layout for any other reason,
                 * just when they makeMove from their original position */
                if ((currentLocation != null) && (currentLocation[0] != newLocation[0] || currentLocation[1] != newLocation[1])) {
                    // Hide the animating chip
                    v.setVisibility(INVISIBLE);

                    // Clone the chip for animation, remember it's already in the destination point
                    final ChipView ghostChip = new ChipView((ChipView) v);

                    // Get the overlay of the root element
                    final ViewGroupOverlay overlay = (ViewGroupOverlay) v.getRootView().getOverlay();
                    overlay.add(ghostChip);


                    // Configure and start animation
                    TranslateAnimation chipMove = new TranslateAnimation(
                            currentLocation[0] - newLocation[0],
                            0,
                            currentLocation[1] - newLocation[1],
                            0);
                    chipMove.setDuration(1000);

                    ghostChip.startAnimation(chipMove);

                    ghostChip.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            overlay.remove(ghostChip);
                            v.setVisibility(VISIBLE);
                        }
                    }, 1000);

                }
                //Save new location as current one
                currentLocation = newLocation;
            }
        });

    }
}
