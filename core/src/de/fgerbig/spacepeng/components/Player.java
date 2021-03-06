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

package de.fgerbig.spacepeng.components;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class Player extends Component {

    public enum State {
        ALIVE,
        RESPAWNING,
        DEAD;
    }

    public static final String SPRITE_NAME = "player";
    public static final String SPRITE_NAME_SHIELD = "playershield";

    public static final int DEFAULT_LIVES = 5;

    private State state = State.ALIVE;

    public int lives = DEFAULT_LIVES;
    public int score;

    public void setState(State state) {
        this.state = state;
    }

    public boolean isState(State state) {
        return this.state.equals(state);
    }

}
