/*
 * From libGDX tutorial "Tyrian"
 * by Gustavo Steigert (https://code.google.com/p/steigert-libgdx)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fgerbig.spacepeng.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import de.fgerbig.spacepeng.assets.MusicKey;
import de.fgerbig.spacepeng.global.Const;

/**
 * A service that manages the background music.
 * Only one music may be playing at a given time.
 */
public class MusicManager implements Disposable {
    /**
     * Holds the music currently being played, if any.
     */
    private MusicKey musicKeyBeingPlayed;
    /**
     * Holds the last music being played, if any.
     */
    private MusicKey lastMusicKeyBeingPlayed;
    /**
     * The volume to be set on the music.
     */
    private float volume = 1f;
    /**
     * Whether the music is enabled.
     */
    private boolean enabled = true;

    /**
     * Creates the music manager.
     */
    public MusicManager() {
    }

    /**
     * Plays the given music (starts the streaming).
     * <p/>
     * If there is already a music being played it is stopped automatically.
     */
    public void play(MusicKey musicKey) {
        // check if the music is enabled
        if (!enabled) return;

        // check if the given music is already being played
        if (musicKeyBeingPlayed == musicKey) return;

        // do some logging
        Gdx.app.log(Const.NAME, "Playing music: " + musicKey.name());

        // stop any music being played
        stop();

        // start streaming the new music
        Music music = musicKey.getMusic();
        music.setVolume(volume);
        music.setLooping(true);
        music.play();

        // set the music being played
        musicKeyBeingPlayed = musicKey;
    }

    /**
     * Stops and disposes the current music being played, if any.
     */
    public void stop() {
        if (musicKeyBeingPlayed != null) {
            Gdx.app.log(Const.NAME, "Stopping current music");
            Music music = musicKeyBeingPlayed.getMusic();
            music.stop();
            lastMusicKeyBeingPlayed = musicKeyBeingPlayed;
            musicKeyBeingPlayed = null;
        }
    }

    /**
     * Sets the music volume which must be inside the range [0,1].
     */
    public void setVolume(float volume) {
        Gdx.app.log(Const.NAME, "Adjusting music volume to: " + volume);

        // check and set the new volume
        if (volume < 0 || volume > 1f) {
            throw new IllegalArgumentException("The volume must be inside the range: [0,1]");
        }
        this.volume = volume;

        // if there is a music being played, change its volume
        if (musicKeyBeingPlayed != null) {
            musicKeyBeingPlayed.getMusic().setVolume(volume);
        }
    }

    /**
     * Enables or disabled the music.
     */
    public void setEnabled(boolean enabled) {
        // no change, so nothing to do
        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;

        // if the music is being deactivated, stop any music being played
        if (!enabled) {
            stop();
            return;
        }

        // if the music has just been enabled, start the music
        if (enabled && lastMusicKeyBeingPlayed != null) {
            this.play(lastMusicKeyBeingPlayed);
        }

    }

    /**
     * Disposes the music manager.
     */
    public void dispose() {
        Gdx.app.log(Const.NAME, "Disposing music manager");
        stop();
    }
}
