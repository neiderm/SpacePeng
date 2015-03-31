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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.global.Assets;
import de.fgerbig.spacepeng.global.Const;

public class SplashScreen extends ScreenAdapter {
    private final int PROGRESSBAR_HEIGHT = (int) (Const.HEIGHT / 40f);
    private final Color PROGRESSBAR_COLOR = new Color(127 / 255f, 212 / 255f, 150 / 255f, 1f);
    private final SpacePeng game;

    private Texture splashTexture;
    private ShapeRenderer shapeRenderer;

    private boolean finished = false;

    public SplashScreen(final SpacePeng game) {
        this.game = game;
    }

    private String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    @Override
    public void show() {
        super.show();

        // load the splash image and create the texture region
        splashTexture = new Texture(Assets.SPLASH_IMAGE);

        // we set the linear texture filter to improve the stretching
        splashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        SpacePeng.glClear();

        // we tell the batch to draw the region starting at (0, 0) of the
        // lower-left corner with the size of the screen
        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();
        batch.draw(splashTexture, 0, 0, Const.WIDTH, Const.HEIGHT);
        batch.end();

        // draw progress bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, Const.WIDTH, PROGRESSBAR_HEIGHT);
        shapeRenderer.setColor(PROGRESSBAR_COLOR);
        shapeRenderer.rect(0, 0, (int) (Const.WIDTH * SpacePeng.assetManager.getProgress()), PROGRESSBAR_HEIGHT);
        shapeRenderer.end();

        if (finished) {
            return;
        }

        finished = SpacePeng.assetManager.update();

        if (finished) {
            Gdx.app.log(Const.NAME, "Finished loading with asset manager.");
            game.initAssets();
            SpacePeng.soundManager.play(SoundKey.BOING);
            game.setScreenWithTransition(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Const.NAME, "Resizing screen: " + getName() + " to: " + width + " x " + height);
        game.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        splashTexture.dispose();
    }
}
