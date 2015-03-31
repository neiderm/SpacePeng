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
import com.badlogic.gdx.Preferences;
import de.fgerbig.spacepeng.global.Const;

/**
 * Handles the game preferences.
 */
public class PreferencesManager {
    // constants
    public static final String PREFS_DIRNAME = (Const.IS_OS_WINDOWS?"":".") + Const.NAME.toLowerCase();
    public static final String PREFS_FILENAME = Const.NAME.toLowerCase() + ".cfg";
    public static final String PREF_FULLSCREEN_ENABLED = "fullscreen_enabled";
    private static final String PREF_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music_enabled";
    private static final String PREF_SOUND_ENABLED = "sound_enabled";

    private Preferences prefs;

    public PreferencesManager() {
    }

    protected synchronized Preferences getPrefs() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences(PREFS_FILENAME);
        }
        return prefs;
    }

    public boolean isFullscreenEnabled() {
        return getPrefs().getBoolean(PREF_FULLSCREEN_ENABLED, false);
    }

    public void setFullscreenEnabled(boolean fullscreenEnabled) {
        getPrefs().putBoolean(PREF_FULLSCREEN_ENABLED, fullscreenEnabled);
        getPrefs().flush();
    }

    public boolean isSoundEnabled() {
        return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean soundEffectsEnabled) {
        getPrefs().putBoolean(PREF_SOUND_ENABLED, soundEffectsEnabled);
        getPrefs().flush();
    }

    public boolean isMusicEnabled() {
        return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean musicEnabled) {
        getPrefs().putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        getPrefs().flush();
    }

    public float getVolume() {
        return getPrefs().getFloat(PREF_VOLUME, 0.5f);
    }

    public void setVolume(float volume) {
        getPrefs().putFloat(PREF_VOLUME, volume);
        getPrefs().flush();
    }
}
