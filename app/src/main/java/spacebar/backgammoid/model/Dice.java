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

import java.util.Random;

public class Dice {
    private static Random random = new Random(System.currentTimeMillis());
    private boolean halfused;
    private boolean used;
    private int value;

    public Dice() {
        halfused = false;
        used = true;
        value = 1;
    }

    public void use() {
        used = true;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isHalfused() {
        return halfused;
    }

    public void setHalfUsed(boolean halfused) {
        this.halfused = halfused;
    }

    public int getValue() {
        return value;
    }

    public int roll() {
        used = false;
        halfused = false;

        value = Math.abs(random.nextInt() % 6) + 1;
        return value;
    }
}
