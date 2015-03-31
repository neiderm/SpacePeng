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

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import de.fgerbig.spacepeng.components.powerup.Coin;
import de.fgerbig.spacepeng.components.powerup.DoubleShot;
import de.fgerbig.spacepeng.components.powerup.Shield;
import de.fgerbig.spacepeng.events.Event;
import de.fgerbig.spacepeng.events.reflection.Handles;
import de.fgerbig.spacepeng.global.Events;
import de.fgerbig.spacepeng.global.Groups;
import de.fgerbig.spacepeng.global.Tags;
import de.fgerbig.spacepeng.services.EntityFactory;

public class CoinSpawningSystem extends VoidEntitySystem {
    @Wire
    ComponentMapper<DoubleShot> doubleShot_cm;
    @Wire
    ComponentMapper<Shield> shield_cm;

    private static final float MIN_DELAY = 7.0f;
    private static final float MAX_DELAY = 13.0f;

    private GroupManager gm;

    protected float delay;
    protected boolean enabled;

    @Override
    protected void initialize() {
        gm = world.getManager(GroupManager.class);
    }

    @Override
    protected void processSystem() {
        if (enabled) {
            delay -= world.delta;

            if (delay <= 0) {
                delay = MathUtils.random(MIN_DELAY, MAX_DELAY);
                dispenseRandomCoin();
            }
        }
    }

    public void dispenseRandomCoin() {
        float r = MathUtils.random();

        if (r < 0.10f) {
            EntityFactory.createCoin(world, Coin.Type.EXTRALIFE);
        } else if (r >= 0.10f && r < 0.50f) {
            EntityFactory.createCoin(world, Coin.Type.DOUBLESHOT);
        } else {
            EntityFactory.createCoin(world, Coin.Type.SHIELD);
        }
    }

    @Handles(ids = Events.ENABLE_ACTION)
    public void enable(Event event) {
        this.enabled = true;
        delay = MathUtils.random(MIN_DELAY, MAX_DELAY);
    }

    @Handles(ids = Events.DISABLE_ACTION)
    public void disable(Event event) {
        this.enabled = false;

        // remove all coins
        ImmutableBag<Entity> coins = gm.getEntities(Groups.COINS);
        for (Entity e : coins) {
            e.deleteFromWorld();
        }

        Entity playerEntity = world.getManager(TagManager.class).getEntity(Tags.PLAYER);

        if (doubleShot_cm.has(playerEntity)) {
            DoubleShot doubleShot = doubleShot_cm.get(playerEntity);
            doubleShot.delay = 0; //TODO
            //playerEntity.edit().remove(DoubleShot.class);
        }
        if (shield_cm.has(playerEntity)) {
            Shield shield = shield_cm.get(playerEntity);
            shield.delay = 0; //TODO
            //playerEntity.edit().remove(Shield.class);
        }

    }

}
