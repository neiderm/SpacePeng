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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Sprite;
import de.fgerbig.spacepeng.components.StayOnScreen;
import de.fgerbig.spacepeng.components.Velocity;
import de.fgerbig.spacepeng.global.Const;

public class StayOnScreenSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Sprite> spr_cm;
    @Wire
    ComponentMapper<Velocity> vlc_cm;

    private TextureAtlas textureAtlas;

    public StayOnScreenSystem(TextureAtlas atlas) {
        super(Aspect.getAspectForAll(StayOnScreen.class, Position.class));
        this.textureAtlas = atlas;
    }

    @Override
    protected void process(Entity e) {
        Position position = pos_cm.get(e);

        float sx = 0, sy = 0;

        if (spr_cm.has(e)) {
            Sprite sprite = spr_cm.get(e);
            TextureAtlas.AtlasRegion region = textureAtlas.findRegion(sprite.name);
            sx = region.getRegionWidth();
            sy = region.getRegionHeight();
        }

        float oldPosX = position.x;
        float oldPosY = position.y;

        position.x = MathUtils.clamp(position.x, sx / 2, Const.WIDTH - sx / 2);
        position.y = MathUtils.clamp(position.y, sy / 2, Const.HEIGHT - sy / 2);

        // set the velocity to zero if screen border was hit
        if (vlc_cm.has(e)) {
            Velocity velocity = vlc_cm.get(e);

            if (oldPosX != position.x) {
                velocity.vectorX = 0;
            }

            if (oldPosY != position.y) {
                velocity.vectorY = 0;
            }
        }
    }
}
