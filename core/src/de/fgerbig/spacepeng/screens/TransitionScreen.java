/*
 * With code examples from the article "Animating transitions between Libgdx screens using TweenEngine"
 * by Tina Denuit-Wojcik posted on Enplug (https://enplug.com)
 */

package de.fgerbig.spacepeng.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.tween.SpriteTween;

public class TransitionScreen implements Screen {
    protected SpacePeng game;

    private Screen currentScreen;
    private Screen nextScreen;

    private SpriteBatch spriteBatch;
    private TweenCallback backgroundAnimationTweenComplete;

    private FrameBuffer currentScreenBuffer;
    private FrameBuffer nextScreenBuffer;

    private Sprite currentScreenSprite;
    private Sprite nextScreenSprite;

    private float startX, startY, speed;
    private TweenEquation tweenEquation;

    public TransitionScreen(Screen currentScreen, Screen nextScreen, SpacePeng game, float startX, float startY, TweenEquation tweenEquation, float speed) {
        this.currentScreen = currentScreen;
        this.nextScreen = nextScreen;
        this.game = game;
        this.startX = startX;
        this.startY = startY;
        this.tweenEquation = tweenEquation;
        this.speed = speed;
    }

    String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    @Override
    public void render(float delta) {
        SpacePeng.glClear();

        SpacePeng.tweenManager.update(delta);

        spriteBatch.begin();

        if (currentScreenSprite == null) {
            currentScreenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.getViewport().getScreenWidth(), game.getViewport().getScreenHeight(), false);
            currentScreenBuffer.begin();
            currentScreen.render(Gdx.graphics.getDeltaTime());
            currentScreenBuffer.end();

            currentScreen.dispose();

            currentScreenSprite = new Sprite(currentScreenBuffer.getColorBufferTexture());
            currentScreenSprite.setPosition(game.getViewport().getLeftGutterWidth(), game.getViewport().getTopGutterHeight());
            currentScreenSprite.flip(false, true);
        }

        currentScreenSprite.draw(spriteBatch);
        nextScreenSprite.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Const.NAME, "Resizing screen: " + getName() + " to: " + width + " x " + height);
        game.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void show() {
        Gdx.app.log(Const.NAME, "Showing screen: " + getName());

        spriteBatch = new SpriteBatch();

        nextScreenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, game.getViewport().getScreenWidth(), game.getViewport().getScreenHeight(), false);
        nextScreenBuffer.begin();
        nextScreen.render(Gdx.graphics.getDeltaTime());
        nextScreenBuffer.end();

        nextScreenSprite = new Sprite(nextScreenBuffer.getColorBufferTexture());
        nextScreenSprite.setPosition(startX, startY);
        nextScreenSprite.flip(false, true);

        backgroundAnimationTweenComplete = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(nextScreen);
            }
        };

        Tween.to(nextScreenSprite, SpriteTween.POS_XY, speed)
                .target(game.getViewport().getLeftGutterWidth(), game.getViewport().getTopGutterHeight())
                .setCallback(backgroundAnimationTweenComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(tweenEquation)
                .start(SpacePeng.tweenManager);
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.app.log(Const.NAME, "Hiding screen: " + getName());
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(Const.NAME, "Disposing screen: " + getName());
        if (currentScreen != null) {
            currentScreen.dispose();
        }

        if (currentScreenBuffer != null) {
            currentScreenBuffer.dispose();
        }
        if (nextScreenBuffer != null) {
            nextScreenBuffer.dispose();
        }
    }
}