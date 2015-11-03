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
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A dice able to rock and roll
 * Based on Tek Eye's Android Dice Roller : http://tekeye.biz/2013/android-dice-code
 */
public class DieView extends View {
    Random rng = new Random();    //generate random numbers
    SoundPool dice_sound;
    int value = 0; //Current dice value
    int sound_id;        //Used to control sound stream return by SoundPool
    Handler handler;    //Post message to start roll
    Timer timer = new Timer();    //Used to implement feedback to user
    boolean rolling = false;        //Is die rolling?
    Drawable dieDrawable;
    //Receives message from timer to start dice roll
    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            //Get roll result
            //Remember nextInt returns 0 to 5 for argument of 6
            //hence + 1
            show(value);
            rolling = false;    //user can press again
            invalidate();
            return true;
        }
    };

    public DieView(Context context) {
        super(context);
        init();
    }

    public DieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DieView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //load dice sound
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            dice_sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            sound_id = dice_sound.load(getContext(), R.raw.shake_dice, 1);
        }
        dieDrawable = getResources().getDrawable(R.drawable.roll_dice);
        dieDrawable.setCallback(this);

        //link handler to callback
        handler = new Handler(callback);
    }

    //User pressed dice, lets start
    public void roll() {
        roll(rng.nextInt(6) + 1);
    }

    public void roll(int value) {
        this.value = value;
        if (!rolling) {
            rolling = true;
            //Show rolling image

            dieDrawable = getResources().getDrawable(R.drawable.roll_dice);
            invalidate();


            //Start rolling sound
            dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);
            //Pause to allow image to update
            timer.schedule(new Roll(), 400);
        }
    }

    public void show(int value) {
        switch (value) {
            case 1:
                dieDrawable = getResources().getDrawable(R.drawable.dice1);
                break;
            case 2:
                dieDrawable = getResources().getDrawable(R.drawable.dice2);
                break;
            case 3:
                dieDrawable = getResources().getDrawable(R.drawable.dice3);
                break;
            case 4:
                dieDrawable = getResources().getDrawable(R.drawable.dice4);
                break;
            case 5:
                dieDrawable = getResources().getDrawable(R.drawable.dice5);
                break;
            case 6:
                dieDrawable = getResources().getDrawable(R.drawable.dice6);
                break;
            default:
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int w = getWidth() - paddingLeft - paddingRight;
        int h = getHeight() - paddingTop - paddingBottom;

        w = w < h ? w : h;
        h = w;

        if (dieDrawable != null) {
            dieDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + w, paddingTop + h);
            dieDrawable.draw(canvas);
        }
    }

    //When pause completed message sent to callback
    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }
}