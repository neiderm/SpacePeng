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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import de.fgerbig.spacepeng.global.Const;

import java.io.File;

/**
 * Profile operations.
 */
public class ProfileManager {
    // the location of the profile data file
    private static final String PROFILE_FILENAME = "profile-v1.json";

    // the loaded profile (may be null)
    private Profile profile;

    /**
     * Creates the profile manager.
     */
    public ProfileManager() {
    }

    /**
     * Retrieves the player's profile, creating one if needed.
     */
    public Profile retrieveProfile() {
        // if the profile is already loaded, just return it
        if (profile != null) {
            return profile;
        }

        // create the handle for the profile data file
        FileHandle profileDataFile = getProfileFileHandle();
        Gdx.app.log(Const.NAME, "Retrieving profile from: " + profileDataFile.path());

        // create the JSON utility object
        Json json = new Json();

        // check if the profile data file exists
        if (profileDataFile.exists()) {

            // load the profile from the data file
            try {

                // read the file as text
                String profileAsText = profileDataFile.readString().trim();

                // decode the contents (if it's base64 encoded)
                if (profileAsText.matches("^[A-Za-z0-9/+=]+$")) {
                    Gdx.app.log(Const.NAME, "Persisted profile is base64 encoded");
                    profileAsText = Base64Coder.decodeString(profileAsText);
                }

                // restore the state
                profile = json.fromJson(Profile.class, profileAsText);

            } catch (Exception e) {

                // log the exception
                Gdx.app.error(Const.NAME, "Unable to parse existing profile data file", e);

                // recover by creating a fresh new profile data file;
                // note that the player will lose all game progress
                profile = new Profile();
                persist(profile);

            }

        } else {
            // create a new profile data file
            profile = new Profile();
            persist(profile);
        }

        // return the result
        return profile;
    }

    /**
     * Persists the given profile.
     */
    protected void persist(Profile profile) {
        // create the handle for the profile data file
        FileHandle profileDataFile = getProfileFileHandle();
        Gdx.app.log(Const.NAME, "Persisting profile in: " + profileDataFile.path());

        // create the JSON utility object
        Json json = new Json();

        // convert the given profile to text
        String profileAsText = json.toJson(profile);

        // encode the text
        if (!Const.DEV_MODE) {
            profileAsText = Base64Coder.encodeString(profileAsText);
        }

        // write the profile data file
        profileDataFile.writeString(profileAsText, false);
    }

    /**
     * Persists the player's profile.
     * <p/>
     * If no profile is available, this method does nothing.
     */
    public void persist() {
        if (profile != null) {
            persist(profile);
        }
    }

    private FileHandle getProfileFileHandle() {
        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            String filename = PreferencesManager.PREFS_DIRNAME + File.separator + PROFILE_FILENAME;
            return Gdx.files.external(filename);
        } else {
            String filename = "data" + File.separator + PROFILE_FILENAME;
            return Gdx.files.local(filename);
        }
    }
}
