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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import de.fgerbig.spacepeng.components.Health;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Sprite;

public class HealthRenderSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Health> hlth_cm;
    @Wire
    ComponentMapper<Sprite> spr_cm;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private BitmapFont font;

    @SuppressWarnings("unchecked")
    public HealthRenderSystem(OrthographicCamera camera, SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
        super(Aspect.getAspectForAll(Position.class, Health.class));
        this.camera = camera;
        this.batch = batch;
        this.textureAtlas = atlas;
        this.font = font;
    }

    @Override
    protected void initialize() {
        font.setUseIntegerPositions(false);
    }

    @Override
    protected void process(Entity e) {
        Position position = pos_cm.get(e);
        Health health = hlth_cm.get(e);

        int percentage = MathUtils.round(health.health / health.maximumHealth * 100f);

        float sy = 0;

        if (spr_cm.has(e)) {
            Sprite sprite = spr_cm.get(e);
            TextureAtlas.AtlasRegion region = textureAtlas.findRegion(sprite.name);
            sy = region.getRegionHeight();
        }

        String s = percentage + "%";
        float x = position.x - font.getBounds(s).width / 2;
        float y = position.y + sy * 2 / 3;
        font.draw(batch, s, x, y);
    }
}
