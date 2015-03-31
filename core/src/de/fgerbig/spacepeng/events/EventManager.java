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
 * Provides a way to register/unregister events and listeners to handle the those events.
 */
public interface EventManager {

    /**
     * Submits a new event specifying the id and the object which generated the event.
     *
     * @param eventId     The identifier of the event to be registered.
     * @param eventSource The object which generated the event (or another data to send with the event).
     */
    void submit(String eventId, Object eventSource);

    /**
     * Registers a new EventListener to listen the specified eventId.
     *
     * @param eventId       The event identifier.
     * @param eventListener The EventListener to register for the specified event.
     */
    void register(String eventId, EventListener eventListener);

    /**
     * Unregisters the specified EventListener from listening events with the specified eventId.
     *
     * @param eventId       The Event identifier.
     * @param eventListener The EventListener to unregister for the specified event.
     */
    void unregister(String eventId, EventListener eventListener);

    /**
     * Unregisters the specified EventListener from listening all events it was registered for.
     *
     * @param listener The EventListener to unregister from all the events it was registered to listen.
     */
    void unregister(EventListener listener);

    /**
     * Process all events from registered using the EventManager interface.
     */
    void process();

}