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

package de.fgerbig.spacepeng.assets;

public enum SoundKey {
    BOING("sound/boing.ogg"),
    CLICK("sound/click.ogg"),
    ALIEN_SHOT("sound/alienshot.ogg"),
    ALIEN_EXPLOSION("sound/alienexplosion.ogg"),
    ALIEN_BOSS_HIT("sound/alienbosshit.ogg"),
    ALIEN_BOSS_EXPLOSION("sound/alienbossexplosion.ogg"),
    PLAYER_SHOT("sound/playershot.ogg"),
    PLAYER_EXPLOSION("sound/playerexplosion.ogg");

    private final String key;

    private SoundKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        return key;
    }
}
