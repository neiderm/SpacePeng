/*
 * From libGDX tutorial "Tyrian"
 * by Gustavo Steigert (https://code.google.com/p/steigert-libgdx)
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

package de.fgerbig.spacepeng.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.services.Profile;
import de.fgerbig.spacepeng.tween.CellTween;
import de.fgerbig.spacepeng.utils.DefaultInputListener;

public class MenuScreen extends AbstractMenuScreen {

    final float BUTTON_START_WIDTH = 190;
    final float BUTTON_START_HEIGHT = 50;

    Profile profile;

    TextButton continueGameButton;
    float continueGameButton_w, continueGameButton_h;

    TextButton startGameButton;
    float startGameButton_w, startGameButton_h;

    TextButton creditsButton;
    float creditsButton_w, creditsButton_h;

    TextButton optionsButton;
    float optionsButton_w, optionsButton_h;

    TextButton quitButton;
    float quitButton_w, quitButton_h;

    public MenuScreen(final SpacePeng game) {
        super(game);

        // retrieve the default table actor
        Table table = super.getTable();
        table.setSkin(game.getSkin());

        Label label = new Label("SPACE PENG!", game.getSkin());
        label.setStyle(labelStyle_Heading);
        table.add(label).spaceBottom(25);
        table.row();

        profile = SpacePeng.profileManager.retrieveProfile();

        if (profile.getLastPlayedLevel() > 1) {
            // register the button "start game"
            continueGameButton = new TextButton("Level " + profile.getLastPlayedLevel(), game.getSkin());
            continueGameButton.setStyle(textButtonStyle_Default);
            continueGameButton.addListener(new DefaultInputListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    SpacePeng.soundManager.play(SoundKey.CLICK);
                    game.setScreen(new GameScreen(game));
                }
            });
            table.add(continueGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().spaceBottom(BUTTON_HEIGHT / 3);
            table.row();
        }

        // register the button "start game"
        String title = (profile.getLastPlayedLevel() > 1) ? "RESTART" : "START";
        startGameButton = new TextButton(title, game.getSkin());
        startGameButton.setStyle(textButtonStyle_Default);
        startGameButton.addListener(new DefaultInputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                SpacePeng.soundManager.play(SoundKey.CLICK);
                profile.setLastPlayedLevel(1);
                game.setScreen(new GameScreen(game));
            }
        });
        table.add(startGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().spaceBottom(BUTTON_SPACING);
        table.row();

        // register the button "Credits"
        creditsButton = new TextButton("Credits", game.getSkin());
        creditsButton.setStyle(textButtonStyle_Default);
        creditsButton.addListener(new DefaultInputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                SpacePeng.soundManager.play(SoundKey.CLICK);
                game.setScreen(new CreditsScreen(game));
            }
        });
        table.add(creditsButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(BUTTON_SPACING);
        table.row();

        // register the button "options"
        optionsButton = new TextButton("Options", game.getSkin());
        optionsButton.setStyle(textButtonStyle_Default);
        optionsButton.addListener(new DefaultInputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                SpacePeng.soundManager.play(SoundKey.CLICK);
                game.setScreen(new OptionsScreen(game));
            }
        });
        table.add(optionsButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform().fill().spaceBottom(BUTTON_HEIGHT / 3);
        table.row();

        // register the button "quit"
        quitButton = new TextButton("QUIT", game.getSkin());
        quitButton.setStyle(textButtonStyle_Default);
        quitButton.addListener(new DefaultInputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                SpacePeng.soundManager.play(SoundKey.CLICK);
                game.pause();
                game.dispose();
            }
        });
        table.add(quitButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);

        Cell cell;

        if (profile.getLastPlayedLevel() > 1) {
            cell = table.getCell(continueGameButton);
            continueGameButton_w = cell.getMaxWidth();
            continueGameButton_h = cell.getMaxHeight();
            cell.maxSize(BUTTON_START_WIDTH, BUTTON_START_HEIGHT);
        }

        cell = table.getCell(startGameButton);
        startGameButton_w = cell.getMaxWidth();
        startGameButton_h = cell.getMaxHeight();
        cell.maxSize(BUTTON_START_WIDTH, BUTTON_START_HEIGHT);

        cell = table.getCell(creditsButton);
        creditsButton_w = cell.getMaxWidth();
        creditsButton_h = cell.getMaxHeight();
        cell.maxSize(BUTTON_START_WIDTH, BUTTON_START_HEIGHT);

        cell = table.getCell(optionsButton);
        optionsButton_w = cell.getMaxWidth();
        optionsButton_h = cell.getMaxHeight();
        cell.maxSize(BUTTON_START_WIDTH, BUTTON_START_HEIGHT);

        cell = table.getCell(quitButton);
        quitButton_w = cell.getMaxWidth();
        quitButton_h = cell.getMaxHeight();
        cell.maxSize(BUTTON_START_WIDTH, BUTTON_START_HEIGHT);

        getTable().invalidate();
    }

    @Override
    public void show() {
        super.show();

        Cell cell;
        float delay = 0.0f;

        if (profile.getLastPlayedLevel() > 1) {
            delay = 0.33f;
            cell = getTable().getCell(continueGameButton);
            Tween.to(cell, CellTween.SCALE_XY, 1.0f)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            SpacePeng.soundManager.play(SoundKey.BOING);
                        }
                    })
                    .setCallbackTriggers(TweenCallback.BEGIN)
                    .target(continueGameButton_w, continueGameButton_h)
                    .ease(TweenEquations.easeOutBounce)
                    .start(SpacePeng.tweenManager);
        }

        cell = getTable().getCell(startGameButton);
        Tween.to(cell, CellTween.SCALE_XY, 1.0f)
                .delay(0.00f + delay)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        SpacePeng.soundManager.play(SoundKey.BOING);
                    }
                })
                .setCallbackTriggers(TweenCallback.BEGIN)
                .target(startGameButton_w, startGameButton_h)
                .ease(TweenEquations.easeOutBounce)
                .start(SpacePeng.tweenManager);

        cell = getTable().getCell(creditsButton);
        Tween.to(cell, CellTween.SCALE_XY, 1.0f)
                .delay(0.33f + delay)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        SpacePeng.soundManager.play(SoundKey.BOING);
                    }
                })
                .setCallbackTriggers(TweenCallback.BEGIN)
                .target(creditsButton_w, creditsButton_h)
                .ease(TweenEquations.easeOutBounce)
                .start(SpacePeng.tweenManager);

        cell = getTable().getCell(optionsButton);
        Tween.to(cell, CellTween.SCALE_XY, 1.0f)
                .delay(0.66f + delay)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        SpacePeng.soundManager.play(SoundKey.BOING);
                    }
                })
                .setCallbackTriggers(TweenCallback.BEGIN)
                .target(optionsButton_w, optionsButton_h)
                .ease(TweenEquations.easeOutBounce)
                .start(SpacePeng.tweenManager);

        cell = getTable().getCell(quitButton);
        Tween.to(cell, CellTween.SCALE_XY, 1.0f)
                .delay(1.00f + delay)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        SpacePeng.soundManager.play(SoundKey.BOING);
                    }
                })
                .setCallbackTriggers(TweenCallback.BEGIN)
                .target(quitButton_w, quitButton_h)
                .ease(TweenEquations.easeOutBounce)
                .start(SpacePeng.tweenManager);

    }

    @Override
    public void render(float delta) {
        SpacePeng.glClear();

        SpacePeng.tweenManager.update(delta);
        getTable().invalidate();

        // update the actors
        stage.act(delta);

        // draw the actors
        stage.draw();

        // print version
        Batch batch = stage.getBatch();
        batch.begin();
        float x = Const.WIDTH - game.getFont().getBounds(Const.VERSION).width;
        float y = game.getFont().getBounds(Const.VERSION).height;
        game.getFont().draw(stage.getBatch(), Const.VERSION, x, y);
        batch.end();
    }
}
