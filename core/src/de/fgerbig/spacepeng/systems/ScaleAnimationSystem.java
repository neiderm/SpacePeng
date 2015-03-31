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
import de.fgerbig.spacepeng.components.ScaleAnimation;
import de.fgerbig.spacepeng.components.Sprite;

public class ScaleAnimationSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<ScaleAnimation> sa;
    @Wire
    ComponentMapper<Sprite> sm;

    @SuppressWarnings("unchecked")
    public ScaleAnimationSystem() {
        super(Aspect.getAspectForAll(ScaleAnimation.class));
    }

    @Override
    protected void process(Entity e) {
        ScaleAnimation scaleAnimation = sa.get(e);
        if (scaleAnimation.active) {
            Sprite sprite = sm.get(e);

            sprite.scaleX += scaleAnimation.speed * world.delta;
            sprite.scaleY = sprite.scaleX;

            if (sprite.scaleX > scaleAnimation.max) {
                sprite.scaleX = scaleAnimation.max;
                scaleAnimation.active = false;
            } else if (sprite.scaleX < scaleAnimation.min) {
                sprite.scaleX = scaleAnimation.min;
                scaleAnimation.active = false;
            }
        }
    }

}
