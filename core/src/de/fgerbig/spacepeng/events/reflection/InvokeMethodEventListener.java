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

package de.fgerbig.spacepeng.events.reflection;

import de.fgerbig.spacepeng.events.Event;
import de.fgerbig.spacepeng.events.EventListener;

import java.lang.reflect.Method;

public class InvokeMethodEventListener extends EventListener {

    protected Object owner;
    protected Method method;

    private Object[] args = new Object[1];

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    @Override
    public void onEvent(Event event) {
        try {
            args[0] = event;
            method.invoke(owner, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method " + method.getName() + " for event " + event.getId(), e);
        }
    }

}