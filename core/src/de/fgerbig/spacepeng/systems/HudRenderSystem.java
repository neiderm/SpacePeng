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

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.components.Player;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Sprite;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.global.Tags;
import de.fgerbig.spacepeng.services.Profile;

import java.util.HashMap;

public class HudRenderSystem extends VoidEntitySystem {

    public enum Overlay {
        NONE,
        LEVEL,
        NEW_HIGHSCORE,
        READY,
        LEVEL_DONE,
        GAME_OVER;
    }

    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Sprite> spr_cm;
    @Wire
    ComponentMapper<Player> ply_cm;

    protected DirectorSystem ds;

    private Profile profile;
    private HashMap<String, AtlasRegion> regions;
    private TextureAtlas textureAtlas;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private BitmapFont levelFont;

    public Overlay overlay = Overlay.NONE;

    com.badlogic.gdx.graphics.g2d.Sprite overlay_ready;
    com.badlogic.gdx.graphics.g2d.Sprite overlay_level_done;
    com.badlogic.gdx.graphics.g2d.Sprite overlay_game_over;

    public HudRenderSystem(OrthographicCamera camera, SpriteBatch batch, TextureAtlas textureAtlas, BitmapFont font, BitmapFont levelFont) {
        this.camera = camera;
        this.batch = batch;
        this.textureAtlas = textureAtlas;
        this.font = font;
        this.levelFont = levelFont;
    }

    @Override
    protected void initialize() {
        ds = world.getSystem(DirectorSystem.class);
        profile = SpacePeng.profileManager.retrieveProfile();

        regions = new HashMap<String, AtlasRegion>();
        for (AtlasRegion r : textureAtlas.getRegions()) {
            regions.put(r.name, r);
        }

        overlay_ready = new com.badlogic.gdx.graphics.g2d.Sprite(textureAtlas.findRegion("ready"));
        overlay_ready.setCenter(Const.WIDTH / 2, Const.HEIGHT / 2);

        overlay_level_done = new com.badlogic.gdx.graphics.g2d.Sprite(textureAtlas.findRegion("leveldone"));
        overlay_level_done.setCenter(Const.WIDTH / 2, Const.HEIGHT / 2);

        overlay_game_over = new com.badlogic.gdx.graphics.g2d.Sprite(textureAtlas.findRegion("gameover"));
        overlay_game_over.setCenter(Const.WIDTH / 2, Const.HEIGHT / 2);

        font.setUseIntegerPositions(false);
    }

    protected void drawQuarterCentered(SpriteBatch batch, String heading, int value, int column) {
        float x, y;
        x = column * Const.WIDTH / 4 + (Const.WIDTH / 4 - font.getBounds(heading).width) / 2;
        y = Const.HEIGHT;
        font.draw(batch, heading, x, y);

        String s = String.valueOf(value);
        x = column * Const.WIDTH / 4 + (Const.WIDTH / 4 - font.getBounds(s).width) / 2;
        y -= 20;
        font.draw(batch, s, x, y);
    }

    @Override
    protected void processSystem() {
        String s;
        float x, y;

        Entity playerEntity = world.getManager(TagManager.class).getEntity(Tags.PLAYER);
        Player player = ply_cm.get(playerEntity);

        drawQuarterCentered(batch, "Lives", player.lives, 0);
        drawQuarterCentered(batch, "SCORE", player.score, 1);
        drawQuarterCentered(batch, "HIGH SCORE", profile.getHighScore(), 2);
        drawQuarterCentered(batch, "LEVEL", ds.getLevel(), 3);

        switch (overlay) {
            case LEVEL:
                s = "Level " + ds.getLevel();
                x = (Const.WIDTH - levelFont.getBounds(s).width) / 2;
                y = (Const.HEIGHT + levelFont.getBounds(s).height) / 2;
                levelFont.draw(batch, s, x, y);
                break;

            case NEW_HIGHSCORE:
                s = "New";
                x = (Const.WIDTH - levelFont.getBounds(s).width) / 2;
                y = (Const.HEIGHT + levelFont.getBounds(s).height) / 2 + 40;
                levelFont.draw(batch, s, x, y);
                s = "HighScore";
                x = (Const.WIDTH - levelFont.getBounds(s).width) / 2;
                y = (Const.HEIGHT + levelFont.getBounds(s).height) / 2 - 40;
                levelFont.draw(batch, s, x, y);
                break;

            case READY:
                overlay_ready.draw(batch);
                break;

            case LEVEL_DONE:
                overlay_level_done.draw(batch);
                break;

            case GAME_OVER:
                overlay_game_over.draw(batch);
                break;
        }

        if (Const.DEV_MODE) {
            font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, Const.HEIGHT - 40);
            font.draw(batch, "Active entities: " + world.getEntityManager().getActiveEntityCount(), 0, Const.HEIGHT - 60);
            font.draw(batch, "Total created: " + world.getEntityManager().getTotalCreated(), 0, Const.HEIGHT - 80);
            font.draw(batch, "Total deleted: " + world.getEntityManager().getTotalDeleted(), 0, Const.HEIGHT - 100);
        }
    }
}
