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
import com.artemis.systems.EntityProcessingSystem;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Velocity;

public class VelocityMovementSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Velocity> vlc_cm;

    public VelocityMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Velocity.class));
    }

    @Override
    protected void process(Entity e) {
        Position position = pos_cm.get(e);
        Velocity velocity = vlc_cm.get(e);

        if (velocity == null) {
            return;
        }

        position.x += velocity.vectorX * world.delta;
        position.y += velocity.vectorY * world.delta;
    }
}
