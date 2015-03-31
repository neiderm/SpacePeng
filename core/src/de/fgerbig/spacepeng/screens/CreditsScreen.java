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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.utils.DefaultInputListener;

public class CreditsScreen extends AbstractMenuScreen {

    private final int CREDITS_WIDTH = (int) (Const.WIDTH * 90 / 100.0);
    private final int CREDITS_HEIGHT = (int) (Const.HEIGHT * 52.5 / 100.0);

    private final String CREDITS = "SpacePeng\n" +
            "\n" +
            "Copyright (c) 2015 by Frank Gerbig\n" +
            "with ideas from Lukas und Julia\n" +
            "\n" +
            "This program is free software: you can redistribute it and/or modify\n" +
            "it under the terms of the GNU General Public License as published by\n" +
            "the Free Software Foundation, either version 3 of the License, or\n" +
            "(at your option) any later version.\n" +
            "\n" +
            "This program is distributed in the hope that it will be useful,\n" +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
            "GNU General Public License for more details.\n" +
            "\n" +
            "You should have received a copy of the GNU General Public License\n" +
            "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n" +
            "\n" +
            "\n" +
            "GRAPHICS\n" +
            "========\n" +
            "\n" +
            "Background art \"Alien planet with a moon\"\n" +
            "by Kris Lachowski released as Creative Commons (CC 0)\n" +
            "\n" +
            "UI skin and credits font derived from \"libgdx test suit\"\n" +
            "by Badlogic Games released under the Apache License Version 2.0\n" +
            "\n" +
            "Font \"Technique BRK\"\n" +
            "by Brian Kent released as Freeware\n" +
            "\n" +
            "Alien sprites \"Purple Monsters\"\n" +
            "by Jojo Mendoza \"Deleket\" released royalty free for personal and non-commercial projects\n" +
            "\n" +
            "Alien shot derived from \"ember01\" from \"particle-pack-01\"\n" +
            "by Iron Star Media Ltd released as Creative Commons (CC BY-2.0 UK)\n" +
            "\n" +
            "Coin animation \"Spinning coin\"\n" +
            "by Jose Maria Atencia (JM.Atencia) released as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "Explosion animation \"Some explosions and a protective shield\"\n" +
            "by GameProgrammingSlave released as Creative Commons (CC)\n" +
            "\n" +
            "Particle \"ember01\" from \"particle-pack-01\"\n" +
            "by Iron Star Media Ltd released as Creative Commons (CC BY-2.0 UK)\n" +
            "\n" +
            "Player Space Ship sprite\n" +
            "by Mattahan (Paul Davey) released as Creative Commons (CC BY-NC-SA)\n" +
            "\n" +
            "Player shield \"Some explosions and a protective shield\"\n" +
            "by GameProgrammingSlave released as Creative Commons (CC)\n" +
            "\n" +
            "Player Shot derived from \"explosionstreak01\" from \"particle-pack-01\"\n" +
            "by Iron Star Media Ltd released as Creative Commons (CC BY-2.0 UK)\n" +
            "\n" +
            "\n" +
            "MUSIC\n" +
            "=====\n" +
            "\n" +
            "Menu music \"Frozen Jam\"\n" +
            "by Jordan Trudgett (tgfcoder) released as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "Game music \"Data Corruption\"\n" +
            "by FoxSynergy released as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "\n" +
            "SOUNDS\n" +
            "======\n" +
            "\n" +
            "Alien boss explosion \"explosion1\" from \"WGS Sound FX - Explosion 1\"\n" +
            "by WrathGames Studio (http://wrathgames.com/blog) released as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "Alien boss hit \"bodyslam\" from \"Action Shooter Soundset (WWVi)\"\n" +
            "by Stephen M. Cameron released as Creative Commons (CC BY-SA-3.0)\n" +
            "\n" +
            "Alien explosion \"qubodup-BangShort\" from \"3 Background Crash Explosion Bang Sounds\"\n" +
            "by Iwan Gabovitch (qubodup) released as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "Alien shot \"FX062\" from \"Collaboration / Sound Effects FX 007\"\n" +
            "by jalastram release as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "Boing from \"Basement Pack 1 - jawharp_boing.wav\"\n" +
            "by plingativator (http://www.gabrielkoenig.com) released as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "Click created with LMMS plugin \"Mallets\"\n" +
            "by Frank Gerbig released as Creative Commons (CC 0)\n" +
            "\n" +
            "Player explosion \"big_explosion\" from \"Action Shooter Soundset (WWVi)\"\n" +
            "by Stephen M. Cameron released as Creative Commons (CC BY-SA-3.0)\n" +
            "\n" +
            "Player shot \"FX067\" from \"Collaboration / Sound Effects FX 007\"\n" +
            "by jalastram release as Creative Commons (CC BY-3.0)\n" +
            "\n" +
            "\n" +
            "SOFTWARE\n" +
            "========\n" +
            "\n" +
            "libGDX \"Android/iOS/HTML5/desktop game development framework\"\n" +
            "by Badlogic Games released under the Apache License Version 2.0\n" +
            "\n" +
            "Artemis-odb entity component system\n" +
            "by Adrian Papari (junkdog) released under the Apache License Version 2.0\n" +
            "\n" +
            "Universal Tween Engine\n" +
            "by Aurelien Ribon released under the Apache License Version 2.0\n" +
            "\n" +
            "Code from \"commons-gdx\"\n" +
            "by Gemserk (http://blog.gemserk.com) released under the Apache License Version 2.0\n" +
            "\n" +
            "Code from the libGDX tutorial \"Tyrian\"\n" +
            "by Gustavo Steigert released under the Apache License Version 2.0\n" +
            "\n" +
            "With code examples from the article \"Animating transitions between Libgdx screens using TweenEngine\"\n" +
            "by Tina Denuit-Wojcik posted on Enplug (https://enplug.com)\n" +
            "\n";


    public CreditsScreen(final SpacePeng game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        // retrieve the default table actor
        Table table = super.getTable();
        table.setSkin(game.getSkin());

        Label label = new Label("Credits", game.getSkin());
        label.setStyle(labelStyle_Heading);
        table.add(label).spaceBottom(25);
        table.row();

        Label creditsText = new Label(CREDITS, game.getSkin());
        Label.LabelStyle labelStyle = creditsText.getStyle();
        labelStyle.font = game.getFont();
        creditsText.setStyle(labelStyle);
        creditsText.setAlignment(Align.center);
        ScrollPane scrollPane = new ScrollPane(creditsText);
        table.add(scrollPane).size(CREDITS_WIDTH, CREDITS_HEIGHT).spaceBottom(25);
        table.row();

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
}
