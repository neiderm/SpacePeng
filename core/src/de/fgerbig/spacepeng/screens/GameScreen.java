/*
 * Copyright (C) 2015 F. Gerbig (fgerbig@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.fgerbig.spacepeng.screens;

import com.artemis.EntitySystem;
import com.artemis.Manager;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.MusicKey;
import de.fgerbig.spacepeng.events.EventManager;
import de.fgerbig.spacepeng.events.EventManagerImpl;
import de.fgerbig.spacepeng.events.reflection.EventListenerReflectionRegistrator;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.services.EntityFactory;
import de.fgerbig.spacepeng.systems.*;

public class GameScreen implements Screen {

    protected final int LIVES = 3;

    SpacePeng game;

    private World world;

    private SpriteRenderSystem spriteRenderSystem;
    private HealthRenderSystem healthRenderSystem;
    private HudRenderSystem hudRenderSystem;
    private BoundsRenderSystem boundsRenderSystem;

    private PlayerInputSystem playerInputSystem;

    protected DirectorSystem ds;
    protected EventManager eventManager;

    public GameScreen(final SpacePeng game) {
        this.game = game;

        eventManager = new EventManagerImpl();

        world = new World();

        world.setManager(new GroupManager());
        world.setManager(new TagManager());

        ds = new DirectorSystem(eventManager);
        world.setSystem(ds);

        world.setSystem(new ExpiringEntitySystem());
        world.setSystem(new ExpiringComponentSystem());
        world.setSystem(new ColorAnimationSystem());
        world.setSystem(new ScaleAnimationSystem());

        playerInputSystem = new PlayerInputSystem(eventManager, game.getCamera(), game.getViewport());
        world.setSystem(playerInputSystem);

        world.setSystem(new AlienBehaviourSystem());
        world.setSystem(new CoinSpawningSystem());

        world.setSystem(new VelocityMovementSystem());
        world.setSystem(new PathMovementSystem());
        world.setSystem(new StayOnScreenSystem(game.getAtlas()));
        world.setSystem(new OffScreenRemoveSystem());

        world.setSystem(new CollisionSystem(eventManager));

        spriteRenderSystem = world.setSystem(new SpriteRenderSystem(game.getCamera(), game.getSpriteBatch(), game.getAtlas(), game.getFont()), true);
        healthRenderSystem = world.setSystem(new HealthRenderSystem(game.getCamera(), game.getSpriteBatch(), game.getAtlas(), game.getSmallFont()), true);
        hudRenderSystem = world.setSystem(new HudRenderSystem(game.getCamera(), game.getSpriteBatch(), game.getAtlas(), game.getSmallFont(), game.getLargeFont()), true);
        boundsRenderSystem = world.setSystem(new BoundsRenderSystem(game.getCamera(), game.getSpriteBatch()), true);

        world.initialize();

        EventListenerReflectionRegistrator registrator = new EventListenerReflectionRegistrator(eventManager);
        // register managers for events
        for (Manager manager : world.getManagers()) {
            registrator.registerEventListeners(manager);
        }
        // register systems for events
        for (EntitySystem system : world.getSystems()) {
            registrator.registerEventListeners(system);
        }

        // background
        EntityFactory.createBackground(world, "background");
        EntityFactory.createPlayer(world);

        ds.setup();
    }

    String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    @Override
    public void render(float delta) {
        // process user input
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.log(Const.NAME, "ESCAPE press detected: " + getName());
            game.setScreen(new MenuScreen(game));
        }

        SpacePeng.glClear();

        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();

        world.setDelta(delta);
        world.process();

        spriteRenderSystem.process();
        healthRenderSystem.process();
        hudRenderSystem.process();

        batch.end();

        eventManager.process();
        if (Const.DEV_MODE) {
            boundsRenderSystem.process();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Const.NAME, "Resizing screen: " + getName() + " to: " + width + " x " + height);
        game.getViewport().update(width, height);
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        SpacePeng.musicManager.play(MusicKey.GAME);
    }

    @Override
    public void hide() {
        Gdx.app.log(Const.NAME, "Hiding screen: " + getName());

        // dispose when leaving the screen
        dispose();
    }

    @Override
    public void pause() {
        Gdx.app.log(Const.NAME, "Pausing screen: " + getName());
    }

    @Override
    public void resume() {
        Gdx.app.log(Const.NAME, "Resuming screen: " + getName());
    }

    @Override
    public void dispose() {
        Gdx.app.log(Const.NAME, "Disposing screen: " + getName());

        // the following call disposes the screen's stage, but on my computer it
        // crashes the game so I commented it out; more info can be found at:
        // http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3624
        if (world != null) {
            world.dispose();
        }
    }

}