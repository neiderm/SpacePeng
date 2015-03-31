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

package de.fgerbig.spacepeng;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.fgerbig.spacepeng.assets.MusicKey;
import de.fgerbig.spacepeng.global.Assets;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.screens.SplashScreen;
import de.fgerbig.spacepeng.screens.TransitionScreen;
import de.fgerbig.spacepeng.services.MusicManager;
import de.fgerbig.spacepeng.services.PreferencesManager;
import de.fgerbig.spacepeng.services.ProfileManager;
import de.fgerbig.spacepeng.services.SoundManager;
import de.fgerbig.spacepeng.tween.CellTween;
import de.fgerbig.spacepeng.tween.SpriteTween;

public class SpacePeng extends Game {

    // services
    public static final AssetManager assetManager = new AssetManager();
    public static final PreferencesManager preferencesManager = new PreferencesManager();
    public static final ProfileManager profileManager = new ProfileManager();
    public static final MusicManager musicManager = new MusicManager();
    public static final SoundManager soundManager = new SoundManager();

    public static final TweenManager tweenManager = new TweenManager();

    public static SpacePeng currentGame = null;

    public static void glClear() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    // display
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    // a libgdx helper class that logs the current FPS each second
    private FPSLogger fpsLogger = new FPSLogger();

    private Skin skin;
    private BitmapFont smallFont;
    private BitmapFont mediumFont;
    private BitmapFont largeFont;
    private TextureAtlas atlas;
    private TextureRegion background;

    @Override
    public void create() {
        currentGame = this;
        Gdx.app.log(Const.NAME, "Creating game on " + Gdx.app.getType());

        profileManager.retrieveProfile();

        // note assets for loading via asset manager
        // (sounds are loaded on demand and cached)
        Gdx.app.log(Const.NAME, "Note skin '" + Assets.SKIN + "' for loading with asset manager.");
        assetManager.load(Assets.SKIN, Skin.class);

        Gdx.app.log(Const.NAME, "Note font '" + Assets.FONT_SMALL + "' for loading with asset manager.");
        assetManager.load(Assets.FONT_SMALL, BitmapFont.class);
        Gdx.app.log(Const.NAME, "Note font '" + Assets.FONT_MEDIUM + "' for loading with asset manager.");
        assetManager.load(Assets.FONT_MEDIUM, BitmapFont.class);
        Gdx.app.log(Const.NAME, "Note font '" + Assets.FONT_LARGE + "' for loading with asset manager.");
        assetManager.load(Assets.FONT_LARGE, BitmapFont.class);

        Gdx.app.log(Const.NAME, "Note image atlas '" + Assets.ATLAS + "' for loading with asset manager.");
        assetManager.load(Assets.ATLAS, TextureAtlas.class);

        for (MusicKey key : MusicKey.values()) {
            Gdx.app.log(Const.NAME, "Note music '" + key.toString() + "' for loading with asset manager.");
            assetManager.load(key.toString(), Music.class);
        }

        musicManager.setVolume(preferencesManager.getVolume());
        musicManager.setEnabled(preferencesManager.isMusicEnabled());

        soundManager.setVolume(preferencesManager.getVolume());
        soundManager.setEnabled(preferencesManager.isSoundEnabled());

        Tween.registerAccessor(Sprite.class, new SpriteTween());
        Tween.registerAccessor(Cell.class, new CellTween());

        camera = new OrthographicCamera(Const.WIDTH, Const.HEIGHT);
        viewport = new FitViewport(Const.WIDTH, Const.HEIGHT, camera);
        viewport.apply(true);
        batch = new SpriteBatch();

        if (Const.DEV_MODE) {
            fpsLogger = new FPSLogger();
        }

        this.setScreen(new SplashScreen(this));
    }

    public void initAssets() {
        skin = assetManager.get(Assets.SKIN);

        // magnify checkboxes
        final float scale = 1.5f;
        CheckBox.CheckBoxStyle checkBoxStyle_Default = new CheckBox.CheckBoxStyle(skin.get(CheckBox.CheckBoxStyle.class));
        checkBoxStyle_Default.checkboxOn.setMinWidth(checkBoxStyle_Default.checkboxOn.getMinWidth() * scale);
        checkBoxStyle_Default.checkboxOn.setMinHeight(checkBoxStyle_Default.checkboxOn.getMinHeight() * scale);
        checkBoxStyle_Default.checkboxOff.setMinWidth(checkBoxStyle_Default.checkboxOff.getMinWidth() * scale);
        checkBoxStyle_Default.checkboxOff.setMinHeight(checkBoxStyle_Default.checkboxOff.getMinHeight() * scale);

        smallFont = assetManager.get(Assets.FONT_SMALL);
        mediumFont = assetManager.get(Assets.FONT_MEDIUM);
        largeFont = assetManager.get(Assets.FONT_LARGE);
        atlas = assetManager.get(Assets.ATLAS);
        background = atlas.findRegion("background");

        for (MusicKey key : MusicKey.values()) {
            Music music = assetManager.get(key.toString());
            key.setMusic(music);
        }
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        super.render();

        if (Const.DEV_MODE) {
            fpsLogger.log();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        Gdx.app.log(Const.NAME, "Pausing game");
        super.pause();

        // persist the profile, because we don't know if the player will come back to the game
        profileManager.persist();
    }

    @Override
    public void resume() {
        super.resume();
        Gdx.app.log(Const.NAME, "Resuming game");
    }

    @Override
    public void setScreen(Screen newScreen) {
        Gdx.app.log(Const.NAME, "Setting screen: " + newScreen.getClass().getSimpleName());
        super.setScreen(newScreen);
    }

    public void setScreenWithTransition(Screen newScreen) {
        this.setScreenWithTransition(newScreen, currentGame.getViewport().getLeftGutterWidth(), currentGame.getViewport().getTopGutterHeight() + Const.HEIGHT, TweenEquations.easeOutBounce, 2.0f);
    }

    public void setScreenWithTransition(Screen newScreen, float startX, float startY, TweenEquation tweenEquation, float speed) {
        Gdx.app.log(Const.NAME, "Setting screen with transition: from " + screen.getClass().getSimpleName() + " to " + newScreen.getClass().getSimpleName());
        TransitionScreen transitionScreen = new TransitionScreen(screen, newScreen, this, startX, startY, tweenEquation, speed);
        setScreen(transitionScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log(Const.NAME, "Disposing game");

        // dispose services
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (musicManager != null) {
            musicManager.dispose();
        }
        if (soundManager != null) {
            soundManager.dispose();
        }

        System.exit(0);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    public Skin getSkin() {
        return skin;
    }

    public BitmapFont getFont() {
        return skin.getFont(Assets.FONT_DEFAULT);
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    public BitmapFont getMediumFont() {
        return mediumFont;
    }

    public BitmapFont getLargeFont() {
        return largeFont;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TextureRegion getBackground() {
        return background;
    }
}
