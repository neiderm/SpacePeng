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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.MusicKey;
import de.fgerbig.spacepeng.global.Const;

/**
 * The base class for all game screens.
 */
public abstract class AbstractMenuScreen implements Screen {
    protected final SpacePeng game;
    protected final Stage stage;
    protected final Table table;

    protected final int BUTTON_WIDTH = (int) (Const.WIDTH * 75 / 100.0);
    protected final int BUTTON_HEIGHT = (int) (Const.HEIGHT * 12 / 100.0);
    protected final int BUTTON_SPACING = (int) (BUTTON_HEIGHT / 6.0);

    protected Label.LabelStyle labelStyle_Heading;
    protected Label.LabelStyle labelStyle_OptionsLabel;
    protected TextButton.TextButtonStyle textButtonStyle_Default;
    protected CheckBox.CheckBoxStyle checkBoxStyle_Default;

    public AbstractMenuScreen(final SpacePeng game) {
        Gdx.app.log(Const.NAME, "New screen " + this.getName());
        this.game = game;
        stage = new Stage(game.getViewport(), game.getSpriteBatch());

        // background
        Image background = new Image(game.getBackground());
        background.setWidth(Const.WIDTH);
        background.setHeight(Const.HEIGHT);
        stage.addActor(background);

        // table
        table = new Table(game.getSkin());
        table.setFillParent(true);
        table.setDebug(Const.DEV_MODE);
        stage.addActor(table);

        SpacePeng.musicManager.play(MusicKey.MENU);

        labelStyle_Heading = new Label.LabelStyle(game.getSkin().get(Label.LabelStyle.class));
        labelStyle_Heading.font = game.getLargeFont();

        labelStyle_OptionsLabel = new Label.LabelStyle(game.getSkin().get(Label.LabelStyle.class));
        labelStyle_OptionsLabel.font = game.getMediumFont();

        textButtonStyle_Default = new TextButton.TextButtonStyle(game.getSkin().get(TextButton.TextButtonStyle.class));
        textButtonStyle_Default.font = game.getMediumFont();

        checkBoxStyle_Default = new CheckBox.CheckBoxStyle(game.getSkin().get(CheckBox.CheckBoxStyle.class));
        checkBoxStyle_Default.font = game.getMediumFont();
    }

    String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    protected Table getTable() {
        return table;
    }

    // Screen implementation

    @Override
    public void show() {
        Gdx.app.log(Const.NAME, "Showing screen: " + getName());

        // set the stage as the input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Const.NAME, "Resizing screen: " + getName() + " to: " + width + " x " + height);
        game.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        SpacePeng.glClear();

        SpacePeng.tweenManager.update(delta);

        // update the actors
        stage.act(delta);

        // draw the actors
        stage.draw();
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
        if (stage != null) {
            stage.dispose();
        }
    }
}
