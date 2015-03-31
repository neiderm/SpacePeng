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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import de.fgerbig.spacepeng.events.Event;
import de.fgerbig.spacepeng.events.EventManager;
import de.fgerbig.spacepeng.global.Const;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Provides reflection utilities to register event listeners for the methods of a class, maybe should be used in an instance way, to avoid problems when forgetting to call unregister().
 */
public class EventListenerReflectionRegistrator {

    // private static final Class<Handles> handlesClass = Handles.class;
    private static final Class<Event> eventClass = Event.class;
    private static final Map<Class<?>, Method[]> cachedClassMethods = new HashMap<Class<?>, Method[]>();

    private static final Map<Class<?>, Map<String, Method>> cachedMethodsPerClass = new HashMap<Class<?>, Map<String, Method>>();

    private static final Map<Method, Annotation[]> cachedAnnotationsPerMethod = new IdentityHashMap<Method, Annotation[]>();

    private final Pool<InvokeMethodEventListener> invokeMethodEventListenerPool = new Pool<InvokeMethodEventListener>(64) {
        @Override
        protected InvokeMethodEventListener newObject() {
            return new InvokeMethodEventListener();
        }
    };

    // this doesn't allows multiple event listeners per method
    private final Map<Object, Map<Method, InvokeMethodEventListener>> createdMethodEventListeners = new HashMap<Object, Map<Method, InvokeMethodEventListener>>();

    private final EventManager eventManager;

    public EventListenerReflectionRegistrator(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private Map<String, Method> getClassCachedMethods(Class<?> clazz) {
        if (!cachedMethodsPerClass.containsKey(clazz))
            cachedMethodsPerClass.put(clazz, new HashMap<String, Method>());
        return cachedMethodsPerClass.get(clazz);
    }

    private Method getMethod(String name, Class<?> clazz) {
        Map<String, Method> cachedMethods = getClassCachedMethods(clazz);
        if (cachedMethods.containsKey(name))
            return cachedMethods.get(name);
        try {
            Method method = clazz.getMethod(name, eventClass);
            cachedMethods.put(name, method);
            return method;
        } catch (SecurityException e) {
            Gdx.app.log(Const.NAME, "Failed to get method " + name);
        } catch (NoSuchMethodException e) {
            Gdx.app.log(Const.NAME, "Failed to get method" + name);
        }
        return null;
    }

    public void registerEventListener(String eventId, final Object o) {
        // On ComponentsEngine Component methods were cached to improve performance when registering for events and more.
        final Method method = getMethod(eventId, o.getClass());
        if (method == null) {
            Gdx.app.log(Const.NAME, "Failed to register EventListener for event " + eventId);
            return;
        }
        registerEventListenerForMethod(eventId, o, method);
    }

    private void registerEventListenerForMethod(String eventId, final Object o, final Method method) {
        method.setAccessible(true);

        InvokeMethodEventListener invokeMethodEventListener = invokeMethodEventListenerPool.obtain();
        invokeMethodEventListener.setOwner(o);
        invokeMethodEventListener.setMethod(method);

        eventManager.register(eventId, invokeMethodEventListener);

        // used to be returned to the pool later.
        Map<Method, InvokeMethodEventListener> cachedMethodEventListeners = getCachedMethodEventListenersForObject(o);
        cachedMethodEventListeners.put(method, invokeMethodEventListener);
    }

    private Map<Method, InvokeMethodEventListener> getCachedMethodEventListenersForObject(final Object o) {
        if (!createdMethodEventListeners.containsKey(o))
            createdMethodEventListeners.put(o, new HashMap<Method, InvokeMethodEventListener>());
        return createdMethodEventListeners.get(o);
    }

    private void unregisterEventListenerForMethod(String eventId, final Object o, final Method method) {
        Map<Method, InvokeMethodEventListener> cachedMethodEventListeners = getCachedMethodEventListenersForObject(o);
        InvokeMethodEventListener invokeMethodEventListener = cachedMethodEventListeners.get(method);
        eventManager.unregister(eventId, invokeMethodEventListener);
        invokeMethodEventListenerPool.free(invokeMethodEventListener);
        cachedMethodEventListeners.remove(method);
    }

    /**
     * Registers all methods from object o with @Handles annotation.
     *
     * @param o The object to register the methods as event listeners.
     */
    public void registerEventListeners(Object o) {
        Class<?> clazz = o.getClass();
        Method[] methods = getCachedClassMethods(clazz);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Handles handlesAnnotation = getHandlesAnnotation(method);
            if (handlesAnnotation == null)
                continue;
            String[] eventIds = handlesAnnotation.ids();
            if (eventIds.length == 0) {
                registerEventListenerForMethod(method.getName(), o, method);
                continue;
            }
            for (int j = 0; j < eventIds.length; j++) {
                String eventId = eventIds[j];
                registerEventListenerForMethod(eventId, o, method);
            }
        }
    }

    /**
     * Unregisters all registered methods from object with @Handles annotation. If not called correctly, then the cached event listeners per method will be stored forever.
     *
     * @param o The object to unregister the methods as event listeners from.
     */
    public void unregisterEventListeners(Object o) {
        Class<?> clazz = o.getClass();
        Method[] methods = getCachedClassMethods(clazz);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Handles handlesAnnotation = getHandlesAnnotation(method);
            if (handlesAnnotation == null)
                continue;
            String[] eventIds = handlesAnnotation.ids();
            if (eventIds.length == 0) {
                unregisterEventListenerForMethod(method.getName(), o, method);
                continue;
            }
            for (int j = 0; j < eventIds.length; j++) {
                String eventId = eventIds[j];
                unregisterEventListenerForMethod(eventId, o, method);
            }
        }
        createdMethodEventListeners.remove(o);
    }

    private Handles getHandlesAnnotation(Method method) {
        Annotation[] annotations = getAnnotations(method);
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof Handles)
                return (Handles) annotations[i];
        }
        return null;
    }

    private Annotation[] getAnnotations(Method method) {
        if (!cachedAnnotationsPerMethod.containsKey(method))
            cachedAnnotationsPerMethod.put(method, method.getAnnotations());
        return cachedAnnotationsPerMethod.get(method);
    }

    private static Method[] getCachedClassMethods(Class<?> clazz) {
        if (!cachedClassMethods.containsKey(clazz))
            cachedClassMethods.put(clazz, clazz.getMethods());
        return cachedClassMethods.get(clazz);
    }

}