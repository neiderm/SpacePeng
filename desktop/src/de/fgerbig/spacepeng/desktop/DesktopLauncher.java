/*
 * From libGDX "Android/iOS/HTML5/desktop game development framework"
 * by by Badlogic Games
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

package de.fgerbig.spacepeng.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.services.PreferencesManager;

public class DesktopLauncher {
	public static void main (String[] args) {
        boolean forceWindowMode = false;

        // handle command line options
        if (args.length == 1) {
            forceWindowMode = args[0].equalsIgnoreCase("-window");
        }

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.forceExit = true;

        // read full screen mode from preferences
        config.preferencesDirectory = PreferencesManager.PREFS_DIRNAME;
        LwjglPreferences preferences = new LwjglPreferences(PreferencesManager.PREFS_FILENAME, config.preferencesDirectory);
        config.fullscreen = preferences.getBoolean(PreferencesManager.PREF_FULLSCREEN_ENABLED);

        if (forceWindowMode) {
            System.out.println("Disabling full screen mode.");
            config.fullscreen = false;
            preferences.putBoolean(PreferencesManager.PREF_FULLSCREEN_ENABLED, false);
        }

        if (config.fullscreen) {
            System.out.println("Trying to set display mode to full screen.");
            System.out.println("(Start with command line option '-window') if this fails.)");

            // get desktop display mode
            Graphics.DisplayMode displayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();

            // set screen size to desktop size
            config.width = displayMode.width;
            config.height = displayMode.height;

        } else {

            // set window size to default size
            config.width = Const.WIDTH;
            config.height = Const.HEIGHT;
        }

        new LwjglApplication(new SpacePeng(), config);
    }
}
