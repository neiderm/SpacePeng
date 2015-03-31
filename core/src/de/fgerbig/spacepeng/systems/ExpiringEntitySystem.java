/*
 * From "Artemis-odb" entity component system
 * by Adrian Papari (junkdog)
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

package de.fgerbig.spacepeng.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedEntityProcessingSystem;
import de.fgerbig.spacepeng.components.ExpiringEntity;

public class ExpiringEntitySystem extends DelayedEntityProcessingSystem {
    @Wire
    ComponentMapper<ExpiringEntity> em;

    @SuppressWarnings("unchecked")
    public ExpiringEntitySystem() {
        super(Aspect.getAspectForAll(ExpiringEntity.class));
    }

    @Override
    protected void processDelta(Entity e, float accumulatedDelta) {
        ExpiringEntity expiringEntity = em.get(e);
        expiringEntity.delay -= accumulatedDelta;
    }

    @Override
    protected void processExpired(Entity e) {
        ExpiringEntity expiringEntity = em.get(e);
        if (expiringEntity.onExpiry != null) {
            expiringEntity.onExpiry.run();
        }
        e.deleteFromWorld();
    }

    @Override
    protected float getRemainingDelay(Entity e) {
        ExpiringEntity expiringEntity = em.get(e);
        return expiringEntity.delay;
    }
}
