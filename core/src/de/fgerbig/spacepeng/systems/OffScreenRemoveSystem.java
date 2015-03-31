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
import de.fgerbig.spacepeng.components.OffScreenRemove;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.global.Const;

public class OffScreenRemoveSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<Position> pm;

    public OffScreenRemoveSystem() {
        super(Aspect.getAspectForAll(OffScreenRemove.class, Position.class));
    }

    @Override
    protected void process(Entity e) {
        Position position = pm.get(e);

        // check off screen
        if ((position.x + Const.WIDTH < 0) ||
                (position.x > Const.WIDTH) ||
                (position.y + Const.HEIGHT < 0) ||
                (position.y > Const.HEIGHT)) {
            e.deleteFromWorld();
        }
    }
}
