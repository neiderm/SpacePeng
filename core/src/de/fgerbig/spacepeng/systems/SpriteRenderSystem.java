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
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import de.fgerbig.spacepeng.components.AnimationParameters;
import de.fgerbig.spacepeng.components.Invisible;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Sprite;
import de.fgerbig.spacepeng.global.Const;

import java.util.*;

public class SpriteRenderSystem extends EntitySystem {
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Sprite> spr_cm;
    @Wire
    ComponentMapper<Invisible> inv_cm;
    @Wire
    ComponentMapper<AnimationParameters> anim_cm;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private BitmapFont font;

    private HashMap<String, AtlasRegion> regions;
    private HashMap<String, Array<? extends TextureRegion>> animations;
    private List<Entity> sortedEntities;

    public SpriteRenderSystem(OrthographicCamera camera, SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
        super(Aspect.getAspectForAll(Position.class, Sprite.class));
        this.camera = camera;
        this.batch = batch;
        this.textureAtlas = atlas;
        this.font = font;
    }

    @Override
    protected void initialize() {
        regions = new HashMap<String, AtlasRegion>();
        animations = new HashMap<String, Array<? extends TextureRegion>>();

        for (AtlasRegion region : textureAtlas.getRegions()) {

            // no texture region with this name
            if (!regions.containsKey(region.name)) {
                // add region to list of regions
                Gdx.app.log(Const.NAME, "added texture atlas region '" + region.name + "'");
                regions.put(region.name, region);
            }

            // no animation with this name
            if (!animations.containsKey(region.name)) {
                // check length of animation
                Array<AtlasRegion> animationRegions = textureAtlas.findRegions(region.name);
                if (animationRegions.size > 1) {
                    // add regions to list of animations
                    Gdx.app.log(Const.NAME, "added animation '" + region.name + "' with " + animationRegions.size + " texture atlas regions");
                    animations.put(region.name, animationRegions);
                }
            }

        }

        sortedEntities = new ArrayList<Entity>();
        font.setUseIntegerPositions(false);
    }

    @Override
    protected void processEntities(IntBag entities) {
        for (int i = 0; sortedEntities.size() > i; i++) {
            process(sortedEntities.get(i));
        }
    }

    protected void process(Entity e) {
        if (inv_cm.has(e)) {
            return; // entity is invisible => don't draw it :-)
        }

        Position position = pos_cm.get(e);
        Sprite sprite = spr_cm.get(e);

        TextureRegion spriteRegion = regions.get(sprite.name);
        batch.setColor(sprite.r, sprite.g, sprite.b, sprite.a);

        // animation
        if (anim_cm.has(e) && animations.containsKey(sprite.name)) {
            AnimationParameters ap = anim_cm.get(e);
            Animation animation = new Animation(ap.frameDuration, animations.get(sprite.name), ap.playMode);
            spriteRegion = animation.getKeyFrame(ap.stateTime);
            ap.stateTime += world.getDelta();
        }

        float posX = position.x - (spriteRegion.getRegionWidth() / 2 * sprite.scaleX);
        float posY = position.y - (spriteRegion.getRegionHeight() / 2 * sprite.scaleX);
        batch.draw(spriteRegion, posX, posY, 0, 0, spriteRegion.getRegionWidth(), spriteRegion.getRegionHeight(), sprite.scaleX, sprite.scaleY, sprite.rotation);
    }

    @Override
    protected void inserted(Entity e) {
        sortedEntities.add(e);

        Collections.sort(sortedEntities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                Sprite s1 = spr_cm.getSafe(e1);
                Sprite s2 = spr_cm.getSafe(e2);
                if (s1 != null && s2 != null) {
                    return s1.layer.compareTo(s2.layer);
                }
                return -1;
            }
        });
    }

    @Override
    protected void removed(Entity e) {
        sortedEntities.remove(e);
    }
}
