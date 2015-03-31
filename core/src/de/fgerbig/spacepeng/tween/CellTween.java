/*
 * Copyright (C) 2015 F. Gerbig (fgerbig@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.fgerbig.spacepeng.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

public class CellTween implements TweenAccessor<Cell> {
    public static final int POS_X = 1;
    public static final int POS_Y = 2;
    public static final int POS_XY = 3;
    public static final int SCALE_X = 4;
    public static final int SCALE_Y = 5;
    public static final int SCALE_XY = 6;
    public static final int COLOR = 7;

    @Override
    public int getValues(Cell target, int tweenType, float[] returnValues) {
        switch (tweenType) {

            case POS_X:
                //returnValues[0] = target.getX();
                return 1;
            case POS_Y:
                //returnValues[0] = target.getY();
                return 1;
            case POS_XY:
                //returnValues[0] = target.getX();
                //returnValues[1] = target.getY();
                return 2;

            case SCALE_X:
                //returnValues[0] = target.getScaleX();
                return 1;
            case SCALE_Y:
                //returnValues[0] = target.getScaleY();
                return 1;
            case SCALE_XY:
                returnValues[0] = target.getMaxWidth();
                returnValues[1] = target.getMaxHeight();
                return 2;

            case COLOR:
//                returnValues[0] = target.getColor().r;
//                returnValues[1] = target.getColor().g;
//                returnValues[2] = target.getColor().b;
//                returnValues[3] = target.getColor().a;
                return 4;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Cell target, int tweenType, float[] newValues) {
        switch (tweenType) {

            case POS_X:
                //target.setX(newValues[0]);
                break;
            case POS_Y:
                //target.setY(newValues[0]);
                break;
            case POS_XY:
                //target.setPosition(newValues[0], newValues[1]);
                break;

            case SCALE_X:
                //target.setScale(newValues[0], target.getScaleY());
                break;
            case SCALE_Y:
                //target.setScale(target.getScaleX(), newValues[0]);
                break;
            case SCALE_XY:
                target.maxSize(newValues[0], newValues[1]);
                break;

            case COLOR:
                //Color c = target.getColor();
                //c.set(newValues[0], newValues[1], newValues[2], newValues[3]);
                //target.setColor(c);
                break;

            default:
                assert false;
                break;
        }
    }


}
