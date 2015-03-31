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

import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;

class InternalEventManager {

    private final Pool<Event> eventPool = new Pool<Event>(50) {
        @Override
        protected Event newObject() {
            return new Event();
        }
    };

    private ArrayList<Event> eventList = new ArrayList<Event>();

    public void registerEvent(String id, Object source) {
        Event event = eventPool.obtain();
        event.setSource(source);
        event.setId(id);
        eventList.add(event);
    }

    public void clear() {
        for (int i = 0; i < getEventCount(); i++)
            eventPool.free(eventList.get(i));
        eventList.clear();
    }

    public int getEventCount() {
        return eventList.size();
    }

    public Event getEvent(int index) {
        if (index < 0 || index >= eventList.size())
            return null;
        return eventList.get(index);
    }

}