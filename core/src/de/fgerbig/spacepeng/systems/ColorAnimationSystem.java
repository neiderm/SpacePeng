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
import de.fgerbig.spacepeng.components.ColorAnimation;
import de.fgerbig.spacepeng.components.Sprite;

public class ColorAnimationSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<ColorAnimation> cam;
    @Wire
    ComponentMapper<Sprite> sm;

    public ColorAnimationSystem() {
        super(Aspect.getAspectForAll(ColorAnimation.class, Sprite.class));
    }

    @Override
    protected void process(Entity e) {
        ColorAnimation c = cam.get(e);
        Sprite sprite = sm.get(e);

        if (c.alphaAnimate) {
            sprite.a += c.alphaSpeed * world.delta;

            if (sprite.a > c.alphaMax || sprite.a < c.alphaMin) {
                if (c.repeat) {
                    c.alphaSpeed = -c.alphaSpeed;
                } else {
                    c.alphaAnimate = false;
                }
            }
        }
    }

}
