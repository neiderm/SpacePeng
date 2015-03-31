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

package de.fgerbig.spacepeng.services;

/**
 * The player's profile.
 * <p/>
 * This class is used to store the game progress, and is persisted to the file
 * system when the game exists.
 *
 * @see ProfileManager
 */
public class Profile {
    private int highScore = 0;
    private int lastPlayedLevel = 1;

    /**
     * Gets the current high score.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Sets the high score.
     */
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    /**
     * Sets the high score.
     */
    public boolean setIfNewHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            return true;
        }
        return false;
    }

    /**
     * Gets the last played level.
     */
    public int getLastPlayedLevel() {
        return lastPlayedLevel;
    }

    /**
     * Sets the last played level.
     */
    public void setLastPlayedLevel(int lastPlayedLevel) {
        this.lastPlayedLevel = lastPlayedLevel;
    }
}
