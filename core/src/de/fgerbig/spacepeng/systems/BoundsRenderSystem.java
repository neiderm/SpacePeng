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

package de.fgerbig.spacepeng.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Sprite;
import de.fgerbig.spacepeng.components.collision.CircleBounds;
import de.fgerbig.spacepeng.components.collision.RectangleBounds;

public class BoundsRenderSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<Position> pm;
    @Wire
    ComponentMapper<CircleBounds> cbm;
    @Wire
    ComponentMapper<RectangleBounds> rbm;
    @Wire
    ComponentMapper<Sprite> sm;

    private static final Color debugColor = new Color(0, 1, 0, 0.85f);

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ShapeRenderer debugShapes;

    public BoundsRenderSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Aspect.getAspectForAll(Position.class, Sprite.class).one(CircleBounds.class, RectangleBounds.class));
        this.camera = camera;
        this.batch = batch;
    }

    @Override
    public void initialize() {
        debugShapes = new ShapeRenderer();
    }

    @Override
    protected void begin() {
        camera.update();
        debugShapes.begin(ShapeRenderer.ShapeType.Line);
        debugShapes.setColor(debugColor);
    }

    @Override
    protected void process(Entity e) {
        if (cbm.has(e)) {
            drawCircleBounds(e);
        }
        if (rbm.has(e)) {
            drawRectangleBounds(e);
        }
    }

    @Override
    protected void end() {
        debugShapes.end();
    }

    protected void drawCircleBounds(Entity e) {
        Position position = pm.get(e);
        CircleBounds cb = cbm.get(e);
        Sprite sprite = sm.get(e);

        debugShapes.circle(position.x, position.y, cb.radius);
    }

    protected void drawRectangleBounds(Entity e) {
        Position position = pm.get(e);
        RectangleBounds rb = rbm.get(e);
        Sprite sprite = sm.get(e);

        debugShapes.rect(position.x - rb.width / 2, position.y - rb.height / 2, rb.width, rb.height);
    }

}
