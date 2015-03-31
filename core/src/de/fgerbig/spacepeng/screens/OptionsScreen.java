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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.utils.DefaultInputListener;

import java.util.Locale;

/**
 * A simple options screen.
 */
public class OptionsScreen extends AbstractMenuScreen {

    private Label volumeValue;

    public OptionsScreen(final SpacePeng game) {
        super(game);

        // retrieve the default table actor
        Table table = super.getTable();
        table.setSkin(game.getSkin());
        table.defaults().spaceBottom(30);
        table.columnDefaults(0).padRight(20);

        Label label = new Label("Options", game.getSkin());
        label.setStyle(labelStyle_Heading);
        table.add(label).colspan(3).spaceBottom(25);
        table.row();

        // create the labels widgets

        // only show if platform is desktop
        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            final CheckBox fullscreenCheckbox = new CheckBox("", checkBoxStyle_Default);
            fullscreenCheckbox.setChecked(SpacePeng.preferencesManager.isFullscreenEnabled());
            fullscreenCheckbox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean enabled = fullscreenCheckbox.isChecked();
                    SpacePeng.preferencesManager.setFullscreenEnabled(enabled);
                    SpacePeng.soundManager.play(SoundKey.CLICK);
                }
            });
            table.row();
            label = new Label("Full Screen", game.getSkin());
            label.setStyle(labelStyle_OptionsLabel);
            table.add(label);
            table.add(fullscreenCheckbox).colspan(2).left();
        }

        final CheckBox soundEffectsCheckbox = new CheckBox("", checkBoxStyle_Default);
        soundEffectsCheckbox.setChecked(SpacePeng.preferencesManager.isSoundEnabled());
        soundEffectsCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean enabled = soundEffectsCheckbox.isChecked();
                SpacePeng.preferencesManager.setSoundEnabled(enabled);
                SpacePeng.soundManager.setEnabled(enabled);
                SpacePeng.soundManager.play(SoundKey.CLICK);
            }
        });
        table.row();
        label = new Label("Sound Effects", game.getSkin());
        label.setStyle(labelStyle_OptionsLabel);
        table.add(label);
        table.add(soundEffectsCheckbox).colspan(2).left();

        final CheckBox musicCheckbox = new CheckBox("", checkBoxStyle_Default);
        musicCheckbox.setChecked(SpacePeng.preferencesManager.isMusicEnabled());
        musicCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean enabled = musicCheckbox.isChecked();
                SpacePeng.preferencesManager.setMusicEnabled(enabled);
                SpacePeng.musicManager.setEnabled(enabled);
                SpacePeng.soundManager.play(SoundKey.CLICK);
            }
        });
        table.row();
        label = new Label("Music", game.getSkin());
        label.setStyle(labelStyle_OptionsLabel);
        table.add(label);
        table.add(musicCheckbox).colspan(2).left();

        // range is [0.0,1.0]; step is 0.1f
        Slider volumeSlider = new Slider(0f, 1f, 0.1f, false, game.getSkin());
        volumeSlider.setValue(SpacePeng.preferencesManager.getVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = ((Slider) actor).getValue();
                SpacePeng.preferencesManager.setVolume(value);
                SpacePeng.musicManager.setVolume(value);
                SpacePeng.soundManager.setVolume(value);
                updateVolumeLabel();
            }
        });

        // create the volume label
        volumeValue = new Label("", game.getSkin());
        volumeValue.setStyle(labelStyle_OptionsLabel);
        updateVolumeLabel();
        // add the volume row
        table.row();
        label = new Label("Volume", game.getSkin());
        label.setStyle(labelStyle_OptionsLabel);
        table.add(label);
        table.add(volumeSlider);
        table.add(volumeValue).width(40);

        // register the back button
        TextButton backButton = new TextButton("Back", game.getSkin());
        backButton.setStyle(textButtonStyle_Default);
        backButton.addListener(new DefaultInputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                SpacePeng.soundManager.play(SoundKey.CLICK);
                game.setScreen(new MenuScreen(game));
            }
        });
        table.row();
        table.add(backButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).colspan(3);
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * Updates the volume label next to the slider.
     */
    private void updateVolumeLabel() {
        float volume = (SpacePeng.preferencesManager.getVolume() * 100f);
        volumeValue.setText(String.format(Locale.US, "%1.0f%%", volume));
    }
}
