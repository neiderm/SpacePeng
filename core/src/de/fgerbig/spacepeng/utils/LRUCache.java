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

package de.fgerbig.spacepeng.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple implementation of an LRU cache.
 * <p/>
 * Retrieved from <a href=
 * "http://stackoverflow.com/questions/224868/easy-simple-to-use-lru-cache-in-java"
 * >Stackoverflow</a>.
 */
public class LRUCache<K, V> {
    private Map<K, V> cache;
    private CacheEntryRemovedListener<K, V> entryRemovedListener;

    /**
     * Creates the cache with the specified max entries.
     */
    public LRUCache(
            final int maxEntries) {
        cache = new LinkedHashMap<K, V>(maxEntries + 1, .75F, true) {
            public boolean removeEldestEntry(
                    Map.Entry<K, V> eldest) {
                if (size() > maxEntries) {
                    if (entryRemovedListener != null) {
                        entryRemovedListener.notifyEntryRemoved(eldest.getKey(), eldest.getValue());
                    }
                    return true;
                }
                return false;
            }
        };
    }

    public void add(
            K key,
            V value) {
        cache.put(key, value);
    }

    public V get(
            K key) {
        return cache.get(key);
    }

    public Collection<V> retrieveAll() {
        return cache.values();
    }

    public void setEntryRemovedListener(
            CacheEntryRemovedListener<K, V> entryRemovedListener) {
        this.entryRemovedListener = entryRemovedListener;
    }

    /**
     * Called when a cached element is about to be removed.
     */
    public interface CacheEntryRemovedListener<K, V> {
        void notifyEntryRemoved(
                K key,
                V value);
    }
}
