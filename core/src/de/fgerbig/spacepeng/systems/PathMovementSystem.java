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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.fgerbig.spacepeng.components.BasePosition;
import de.fgerbig.spacepeng.components.PathMovement;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.TransitionToNewPathMovement;
import de.fgerbig.spacepeng.path.*;

import java.util.HashMap;

public class PathMovementSystem extends EntityProcessingSystem {
    @Wire
    ComponentMapper<BasePosition> bpos_cm;
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<PathMovement> pmv_cm;
    @Wire
    ComponentMapper<TransitionToNewPathMovement> tpmv_cm;

    private HashMap<String, PathFunction> pathFunctions;

    public PathMovementSystem() {
        super(Aspect.getAspectForAll(BasePosition.class, Position.class, PathMovement.class));
    }

    protected void initialize() {
        pathFunctions = new HashMap<String, PathFunction>();
        pathFunctions.put(DefaultAlienMovementPathFunction.id, new DefaultAlienMovementPathFunction());
        pathFunctions.put(DefaultAlienAttackPathFunction.id, new DefaultAlienAttackPathFunction());
        pathFunctions.put(AlienBossAttackPathFunction.id, new AlienBossAttackPathFunction());
        pathFunctions.put(TwoAlienBossesAttackPathFunction.id, new TwoAlienBossesAttackPathFunction());
    }

    @Override
    protected void process(Entity e) {
        Vector2 pathPos;
        float delta = world.delta;

        BasePosition basePosition = bpos_cm.get(e);
        Position position = pos_cm.get(e);

        PathMovement pathMovement = pmv_cm.get(e);

        if (!tpmv_cm.has(e)) {
            // no transition
            pathPos = getPos(pathMovement);
        } else {
            // in transition to new path movement function
            TransitionToNewPathMovement transitionToNewPathMovement = tpmv_cm.get(e);
            pathPos = getPos(pathMovement, transitionToNewPathMovement);

            // advance transition
            transitionToNewPathMovement.factor += delta;

            // transition completed?
            if (transitionToNewPathMovement.factor >= 1) {
                pathMovement.pathFunctionId = transitionToNewPathMovement.pathFunctionId;
                e.edit().remove(TransitionToNewPathMovement.class);
            }
        }

        position.x = basePosition.x + pathPos.x;
        position.y = basePosition.y + pathPos.y;

        pathMovement.rad += delta * 2;
        if (pathMovement.rad >= MathUtils.PI2) {
            pathMovement.rad -= MathUtils.PI2;
        }
    }

    protected Vector2 getPos(PathMovement pathMovement) {
        PathFunction f1;
        Vector2 pos;

        f1 = pathFunctions.get(pathMovement.pathFunctionId);
        pos = f1.getXY(pathMovement.rad);

        return pos;
    }

    protected Vector2 getPos(PathMovement pathMovement, TransitionToNewPathMovement transitionToNewPathMovement) {
        PathFunction f1, f2;
        Vector2 pos;

        f1 = pathFunctions.get(pathMovement.pathFunctionId);
        f2 = pathFunctions.get(transitionToNewPathMovement.pathFunctionId);

        Vector2 oldPathXY = f1.getXY(pathMovement.rad);
        Vector2 newPathXY = f2.getXY(pathMovement.rad);

        // blend values of old and new movement function
        float factor = transitionToNewPathMovement.factor;
        float x = (1 - factor) * oldPathXY.x + factor * newPathXY.x;
        float y = (1 - factor) * oldPathXY.y + factor * newPathXY.y;

        pos = new Vector2(x, y);

        return pos;
    }
}
