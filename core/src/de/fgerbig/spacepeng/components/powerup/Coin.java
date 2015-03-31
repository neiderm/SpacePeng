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

package de.fgerbig.spacepeng.components.powerup;

import com.artemis.Component;

public class Coin extends Component {

    public static final String SPRITE_NAME = "coin";

    public enum Type {
        EXTRALIFE(Coin.SPRITE_NAME),
        SHIELD(Coin.SPRITE_NAME, 0.5f, 1.0f, 0.5f), // green
        DOUBLESHOT(Coin.SPRITE_NAME, 1.0f, 0.5f, 0.5f); //red

        public final String spriteName;
        public final float r, g, b;

        private Type(String spriteName) {
            this(spriteName, 1.0f, 1.0f, 1.0f);
        }

        private Type(String spriteName, float r, float g, float b) {
            this.spriteName = spriteName;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public Type type;

    public Coin(Type type) {
        this.type = type;
    }
}
