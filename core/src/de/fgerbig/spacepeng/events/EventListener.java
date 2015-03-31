/*
 * From "commons-gdx"
 * by Gemserk (http://blog.gemserk.com)
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

package de.fgerbig.spacepeng.events;

/**
 * Base class to use when registering a listener for an event.
 */
public class EventListener {

    /**
     * Called when to handle the event.
     *
     * @param event The event to handle.
     */
    public void onEvent(Event event) {
    }

}