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
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import de.fgerbig.spacepeng.components.*;
import de.fgerbig.spacepeng.events.Event;
import de.fgerbig.spacepeng.events.reflection.Handles;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.global.Events;
import de.fgerbig.spacepeng.global.Groups;
import de.fgerbig.spacepeng.global.Tags;
import de.fgerbig.spacepeng.path.DefaultAlienAttackPathFunction;
import de.fgerbig.spacepeng.path.DefaultAlienMovementPathFunction;
import de.fgerbig.spacepeng.services.EntityFactory;

public class AlienBehaviourSystem extends EntityProcessingSystem {

    @Wire
    ComponentMapper<Alien> alien_cm;
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<IsAttacking> isAttacking_cm;
    @Wire
    ComponentMapper<Health> hlth_cm;
    @Wire
    ComponentMapper<PathMovement> pmv_cm;
    @Wire
    ComponentMapper<TransitionToNewPathMovement> tpmv_cm;
    @Wire
    ComponentMapper<AttackPathMovement> apmv_cm;

    private final float MAX_LEVEL = 100.0f;

    private boolean isAttackingAllowed;

    private GroupManager gm;

    private float levelInfluence;
    private float time;
    private int maxAliens;

    private float timeRage, killRage;

    public AlienBehaviourSystem() {
        super(Aspect.getAspectForAll(Alien.class, Position.class, PathMovement.class));
    }

    @Override
    protected void initialize() {
        gm = world.getManager(GroupManager.class);
    }

    protected float weightedChance(float min, float max, float levelInfluence, float timeRage, float killRage) {
        return MathUtils.clamp(min + (max - min) * levelInfluence + 0.01f * timeRage * killRage, min, max);
    }

    @Override
    protected void process(final Entity e) {
        final Alien alien = alien_cm.get(e);
        final Position position = pos_cm.get(e);

        boolean boss = hlth_cm.has(e); // only bosses have health

        // aliens get angry
        if (this.isAttackingAllowed) {
            this.time += world.getDelta();
        }

        int alienCount = gm.getEntities(Groups.ALIENS).size();

        timeRage = MathUtils.clamp(time / 10000, 0, 1);
        killRage = MathUtils.clamp((maxAliens - alienCount) / (float) maxAliens, 0.01f, 1);

//        Gdx.app.log(Const.NAME, "levelInfluence=" + levelInfluence);
//        Gdx.app.log(Const.NAME, "timeRage=" + timeRage);
//        Gdx.app.log(Const.NAME, "killRage=" + killRage);
//        Gdx.app.log(Const.NAME, "boss=" + boss);

        float chance_switchToAttacking = weightedChance(0.00001f, 0.001f, levelInfluence, timeRage, killRage);
//        Gdx.app.log(Const.NAME, "chance_switchToAttacking=" + chance_switchToAttacking);
        float chance_shootAttacking = weightedChance(0.0025f, 0.025f, levelInfluence, timeRage, killRage);
//        Gdx.app.log(Const.NAME, "chance_shootAttacking=" + chance_shootAttacking);
        float chance_shootNoAttacking = weightedChance(0.0001f, 0.01f, levelInfluence, timeRage, killRage);
//        Gdx.app.log(Const.NAME, "chance_shootNoAttacking=" + chance_shootNoAttacking);

        if (boss) {
            chance_switchToAttacking *= 100;
            chance_shootNoAttacking *= 25;
        }

        // set alien to attacking
        if (!isAttacking_cm.has(e) && !tpmv_cm.has(e) && MathUtils.randomBoolean(chance_switchToAttacking) && isAttackingAllowed) {
            String group = getAttackGroup(e);
            String pathFunctionId = DefaultAlienAttackPathFunction.id;

            // if special attack path movement specified, get the path function id
            if (apmv_cm.has(e)) {
                AttackPathMovement attackPathMovement = apmv_cm.get(e);
                pathFunctionId = attackPathMovement.pathFunctionId;
            }

            if (group != null) {
                setGroupAttacking(group, pathFunctionId);
            } else {
                IsAttacking isAttacking = new IsAttacking();
                isAttacking.onExpiry = new Runnable() {
                    @Override
                    public void run() {
                        e.edit().add(new TransitionToNewPathMovement(DefaultAlienMovementPathFunction.id));
                    }
                };
                isAttacking.delay = 3.14f;
                e.edit().add(isAttacking);
                e.edit().add(new TransitionToNewPathMovement(pathFunctionId));
            }
        }

        if (isAttackingAllowed) {
            Entity playerEntity = world.getManager(TagManager.class).getEntity(Tags.PLAYER);
            Position playerPos = pos_cm.get(playerEntity);

            boolean directlyAbovePlayer = Math.abs(position.x - playerPos.x) <= 10;

            if (isAttacking_cm.has(e)) { // shoot more often if attacking
                // shoot
                if (MathUtils.randomBoolean(chance_shootAttacking)) {
                    EntityFactory.createAlienMultiShot(world, position.x, position.y);
                }

                // shoot if directly above player
                if (directlyAbovePlayer && MathUtils.randomBoolean(chance_shootAttacking * 10.0f)) {
                    EntityFactory.createAlienShot(world, position.x, position.y);
                }

            } else {
                // shoot
                if (MathUtils.randomBoolean(chance_shootNoAttacking)) {
                    EntityFactory.createAlienShot(world, position.x, position.y);
                }

                // shoot if directly above player
                if (directlyAbovePlayer && MathUtils.randomBoolean(chance_shootNoAttacking)) {
                    EntityFactory.createAlienShot(world, position.x, position.y);
                }

            }
        }
    }

    protected String getAttackGroup(Entity e) {
        // get all groups this entity belongs to
        ImmutableBag<String> groups = gm.getGroups(e);
        for (String group : groups) {
            // find attack group
            if (group.startsWith(Groups.ALIEN_ATTACK_GROUP)) {
                return group;
            }
        }
        // no attack group found
        return null;
    }

    protected void setGroupAttacking(String group, String pathFunctionId) {
        // get all entities in this group
        ImmutableBag<Entity> entities = gm.getEntities(group);
        for (final Entity e : entities) {
            // set attacking
            IsAttacking isAttacking = new IsAttacking();
            isAttacking.onExpiry = new Runnable() {
                @Override
                public void run() {
                    e.edit().add(new TransitionToNewPathMovement(DefaultAlienMovementPathFunction.id));
                }
            };
            isAttacking.delay = 3.14f; //TODO
            e.edit().add(isAttacking);
            e.edit().add(new TransitionToNewPathMovement(pathFunctionId));
        }
    }

    protected void setGroupNotAttacking(String group) {
        // get all entities in this group
        ImmutableBag<Entity> entities = gm.getEntities(group);
        for (Entity e : entities) {
            // set not attacking
            e.edit().remove(IsAttacking.class);
            e.edit().add(new TransitionToNewPathMovement(DefaultAlienMovementPathFunction.id));
        }
    }

    public void setLevelParameters(int level, int maxAliens) {
        this.levelInfluence = MathUtils.clamp(level / MAX_LEVEL, 0.0f, 1.0f); // MAX_LEVEL is hell :-)
        this.time = 0;
        this.maxAliens = maxAliens;
    }

    @Handles(ids = Events.ENABLE_ACTION)
    public void setAttackingAllowed(Event event) {
        this.isAttackingAllowed = true;
    }

    @Handles(ids = Events.DISABLE_ACTION)
    public void setNoAttacking(Event event) {
        this.isAttackingAllowed = false;
        this.time = 0;

        // set all aliens to not attacking and default path movement
        ImmutableBag<Entity> aliens = gm.getEntities(Groups.ALIENS);
        for (Entity e : aliens) {
            // not attacking
            e.edit().remove(IsAttacking.class);

            // default path movement
            if (pmv_cm.has(e)) {
                e.edit().add(new TransitionToNewPathMovement(DefaultAlienMovementPathFunction.id));
            }
        }
    }
}
