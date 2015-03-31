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

package de.fgerbig.spacepeng.path;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.fgerbig.spacepeng.global.Const;

public class TwoAlienBossesAttackPathFunction implements PathFunction {
    public static final String id = "twoBossesAttack";

    @Override
    public Vector2 getXY(float rad) {
        float x = MathUtils.sin(rad) * Const.WIDTH * 0.18f;
        float y = MathUtils.cos(3 * rad) * Const.HEIGHT / 3f - Const.HEIGHT / 4;

        return new Vector2(x, y);
    }
}
