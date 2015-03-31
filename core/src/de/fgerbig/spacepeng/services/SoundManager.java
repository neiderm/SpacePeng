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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.utils.LRUCache;

/**
 * A service that manages the sound effects.
 */
public class SoundManager implements LRUCache.CacheEntryRemovedListener<SoundKey, Sound>, Disposable {
    /**
     * The sound cache.
     */
    private final LRUCache<SoundKey, Sound> soundCache;
    /**
     * The volume to be set on the sound.
     */
    private float volume = 1f;

    /**
     * Whether the sound is enabled.
     */
    private boolean enabled = true;

    /**
     * Creates the sound manager.
     */
    public SoundManager() {
        soundCache = new LRUCache<SoundKey, Sound>(10);
        soundCache.setEntryRemovedListener(this);
    }

    /**
     * Plays the specified sound.
     */
    public void play(SoundKey sound) {
        // check if the sound is enabled
        if (!enabled) return;

        // try and get the sound from the cache
        Sound soundToPlay = soundCache.get(sound);
        if (soundToPlay == null) {
            FileHandle soundFile = Gdx.files.internal(sound.getKey());
            soundToPlay = Gdx.audio.newSound(soundFile);
            soundCache.add(sound, soundToPlay);
        }

        // play the sound
        //Gdx.app.log(Const.NAME, "Playing sound: " + sound.id());
        soundToPlay.play(volume);
    }

    /**
     * Sets the sound volume which must be inside the range [0,1].
     */
    public void setVolume(
            float volume) {
        Gdx.app.log(Const.NAME, "Adjusting sound volume to: " + volume);

        // check and set the new volume
        if (volume < 0 || volume > 1f) {
            throw new IllegalArgumentException("The volume must be inside the range: [0,1]");
        }
        this.volume = volume;
    }

    /**
     * Enables or disabled the sound.
     */
    public void setEnabled(
            boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void notifyEntryRemoved(SoundKey key, Sound value) {
        Gdx.app.log(Const.NAME, "Disposing sound: " + key.name());
        value.dispose();
    }

    // EntryRemovedListener implementation

    /**
     * Disposes the sound manager.
     */
    public void dispose() {
        Gdx.app.log(Const.NAME, "Disposing sound manager");
        for (Sound sound : soundCache.retrieveAll()) {
            sound.stop();
            sound.dispose();
        }
    }
}
