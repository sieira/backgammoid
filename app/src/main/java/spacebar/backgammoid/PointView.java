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
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import spacebar.backgammoid.model.Board;


/**
 * Custom View representing a backgammon point
 */
public class PointView extends LinearLayout {
    private boolean mUpsideDown = false;
    private boolean mBar = false;

    public PointView(Context context) {
        super(context);
    }

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PointView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Init the point's attributes
     *
     * @param context Activity context
     * @param attrs   Attributes for the view (upsideDown has to be specified)
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PointView);

        mUpsideDown = a.getBoolean(R.styleable.PointView_upsideDown, false);
        mBar = a.getBoolean(R.styleable.PointView_bar, false);

        int gravity = mUpsideDown ? Gravity.TOP : Gravity.BOTTOM;
        // If the point is a bar, chips gravitate around its center
        gravity = mBar ? Gravity.CENTER : gravity;

        setGravity(gravity);

        a.recycle();
    }

    /**
     * Get the last chip belonging to a player (useful for the bar, where chips can be mixed)
     *
     * @param player The player whose chip is to be gotten
     * @return The outermost chip belonging to a player in this point
     */
    public ChipView getLastChip(Board.Player player) {
        ChipView lastChip = null;

        if (mUpsideDown) {
            for (int ii = super.getChildCount() - 1; ii >= 0; ii--) {
                lastChip = (ChipView) super.getChildAt(ii);
                Board.Player owner = lastChip.getOwner();
                if (owner.equals(player)) break;
            }
        } else {
            for (int ii = 0; ii < super.getChildCount(); ii++) {
                lastChip = (ChipView) super.getChildAt(ii);
                Board.Player owner = lastChip.getOwner();
                if (owner.equals(player)) break;
            }
        }

        return lastChip;
    }

    /**
     * Gets the last chip from the point regardless of its owner
     *
     * @return The outermost chip of this point
     */
    public ChipView getLastChip() {
        return mUpsideDown ? (ChipView) super.getChildAt(super.getChildCount() - 1) : (ChipView) super.getChildAt(0);
    }

    /**
     * Pops the last chip belonging to a player (useful for the bar, where chips can be mixed)
     *
     * @param player The player whose outermost chip is to be popped
     * @return The outermost chip belonging to a player in the point
     */
    public ChipView pop(Board.Player player) {
        ChipView last = getLastChip(player);
        this.removeView(last);
        return last;
    }

    public ChipView pop() {
        ChipView last = getLastChip();
        this.removeView(last);
        return last;
    }

    /**
     * Pushes a chip into the point at the proper position
     *
     * @param chip The chip that's to be pushed to this point (it should not have a parent)
     */
    public void push(View chip) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        chip.setLayoutParams(params);

        if (mUpsideDown) {
            super.addView(chip);
        } else {
            super.addView(chip, 0);
        }
    }
}