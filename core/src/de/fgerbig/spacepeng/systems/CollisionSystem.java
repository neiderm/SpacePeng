/*
 * From "Artemis-odb" entity component system
 * by Adrian Papari (junkdog)
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

package de.fgerbig.spacepeng.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.components.*;
import de.fgerbig.spacepeng.components.collision.CircleBounds;
import de.fgerbig.spacepeng.components.collision.RectangleBounds;
import de.fgerbig.spacepeng.components.powerup.Coin;
import de.fgerbig.spacepeng.components.powerup.DoubleShot;
import de.fgerbig.spacepeng.components.powerup.Shield;
import de.fgerbig.spacepeng.events.EventManager;
import de.fgerbig.spacepeng.global.Events;
import de.fgerbig.spacepeng.global.Groups;
import de.fgerbig.spacepeng.services.EntityFactory;

public class CollisionSystem extends VoidEntitySystem {
    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<CircleBounds> circb_cm;
    @Wire
    ComponentMapper<RectangleBounds> rectb_cm;
    @Wire
    ComponentMapper<Health> hlth_cm;
    @Wire
    ComponentMapper<IsAttacking> isAttacking_cm;
    @Wire
    ComponentMapper<Coin> coin_cm;
    @Wire
    ComponentMapper<Player> player_cm;
    @Wire
    ComponentMapper<Shield> shield_cm;
    @Wire
    ComponentMapper<Sprite> spr_cm;

    protected DirectorSystem ds;
    EventManager eventManager;

    private Bag<CollisionPair> collisionPairs;

    public CollisionSystem(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void initialize() {
        ds = world.getSystem(DirectorSystem.class);

        collisionPairs = new Bag<CollisionPair>();

        // player shoots alien
        collisionPairs.add(new CollisionPair(Groups.PLAYER_SHOTS, Groups.ALIENS, new CollisionHandler() {
            @Override
            public void handleCollision(Entity playerShot, Entity alien) {
                playerShot.deleteFromWorld();

                int bonusForAlienIsAttacking = isAttacking_cm.has(alien)?1:0;
                boolean boss = hlth_cm.has(alien); // only bosses have health
                float health = 0;

                if (boss) {
                    Health h = hlth_cm.get(alien);
                    h.health -= 1;
                    health = h.health;
                    ds.increaseScore(25 + bonusForAlienIsAttacking * 25);
                }

                if (health <= 0) {
                    Position alienPos = pos_cm.get(alien);
                    if (boss) {
                        ds.increaseScore(100 + bonusForAlienIsAttacking * 50);
                        EntityFactory.createAlienBossExplosion(world, alienPos.x, alienPos.y);
                    } else {
                        ds.increaseScore(25 + bonusForAlienIsAttacking * 25);
                        EntityFactory.createAlienExplosion(world, alienPos.x, alienPos.y);
                    }
                    alien.deleteFromWorld();
                    eventManager.submit(Events.CHECK_IS_LEVEL_CLEAR, this);

                } else {
                    if (boss) {
                        SpacePeng.soundManager.play(SoundKey.ALIEN_BOSS_HIT);
                    }
                }
            }
        }));

        // player shoots alien shot
        collisionPairs.add(new CollisionPair(Groups.PLAYER_SHOTS, Groups.ALIEN_SHOTS, new CollisionHandler() {
            @Override
            public void handleCollision(Entity playerShot, Entity alienShot) {
                Position alienShotPos = pos_cm.get(alienShot);
                for (int i = 0; i < 10; i++) {
                    EntityFactory.createParticle(world, alienShotPos.x, alienShotPos.y);
                }

                alienShot.deleteFromWorld();
            }
        }));

        // player collides alien
        collisionPairs.add(new CollisionPair(Groups.PLAYER, Groups.ALIENS, new CollisionHandler() {
            @Override
            public void handleCollision(Entity player, Entity alien) {
                float health = 0;

                if (hlth_cm.has(alien)) {
                    Health h = hlth_cm.get(alien);
                    h.health -= 3;
                    health = h.health;
                }

                if (health <= 0) {
                    Position alienPos = pos_cm.get(alien);
                    EntityFactory.createAlienExplosion(world, alienPos.x, alienPos.y);
                    ds.increaseScore(25);
                    alien.deleteFromWorld();
                    eventManager.submit(Events.CHECK_IS_LEVEL_CLEAR, this);
                }


                // if player has a shield, don't kill him
                if (shield_cm.has(player)) {
                    return;
                }

                Position playerPos = pos_cm.get(player);
                EntityFactory.createPlayerExplosion(world, playerPos.x, playerPos.y);

                eventManager.submit(Events.PLAYER_KILLED, this);
            }
        }));

        // player collides coin
        collisionPairs.add(new CollisionPair(Groups.PLAYER, Groups.COINS, new CollisionHandler() {
            @Override
            public void handleCollision(Entity player, Entity coin) {
                float health = 0;

                coin.deleteFromWorld();
                SpacePeng.soundManager.play(SoundKey.BOING);

                switch (coin_cm.get(coin).type) {
                    case EXTRALIFE:
                        Player p = player_cm.get(player);
                        p.lives += 1;
                        break;
                    case DOUBLESHOT:
                        DoubleShot doubleShot = new DoubleShot();
                        doubleShot.delay = 5.0f;
                        player.edit().add(doubleShot);
                        break;
                    case SHIELD:
                        Shield shield = new Shield();
                        shield.delay = 5.0f;
                        final Sprite sprite = spr_cm.get(player);
                        sprite.name = Player.SPRITE_NAME_SHIELD;
                        shield.onExpiry = new Runnable() {
                            @Override
                            public void run() {
                                sprite.name = Player.SPRITE_NAME;
                            }
                        };
                        player.edit().add(shield);
                        break;
                }
            }
        }));

        // alien shoots player
        collisionPairs.add(new CollisionPair(Groups.ALIEN_SHOTS, Groups.PLAYER, new CollisionHandler() {
            @Override
            public void handleCollision(Entity alienShot, Entity player) {
                alienShot.deleteFromWorld();

                // if player has a shield, don't kill him
                if (shield_cm.has(player)) {
                    return;
                }

                Position playerPos = pos_cm.get(player);
                EntityFactory.createPlayerExplosion(world, playerPos.x, playerPos.y);

                eventManager.submit(Events.PLAYER_KILLED, this);
            }
        }));
    }

    @Override
    protected void processSystem() {
        for (int i = 0; collisionPairs.size() > i; i++) {
            collisionPairs.get(i).checkForCollisions();
        }
    }

    private class CollisionPair {
        private ImmutableBag<Entity> groupEntitiesA;
        private ImmutableBag<Entity> groupEntitiesB;
        private CollisionHandler handler;

        public CollisionPair(String group1, String group2, CollisionHandler handler) {
            groupEntitiesA = world.getManager(GroupManager.class).getEntities(group1);
            groupEntitiesB = world.getManager(GroupManager.class).getEntities(group2);
            this.handler = handler;
        }

        public void checkForCollisions() {
            for (int a = 0; groupEntitiesA.size() > a; a++) {
                for (int b = 0; groupEntitiesB.size() > b; b++) {
                    Entity entityA = groupEntitiesA.get(a);
                    Entity entityB = groupEntitiesB.get(b);
                    if (collisionExists(entityA, entityB)) {
                        handler.handleCollision(entityA, entityB);
                    }
                }
            }
        }

        private boolean collisionExists(Entity e1, Entity e2) {

            if (e1 == null || e2 == null) {
                return false;
            }

            Position p1 = pos_cm.getSafe(e1);
            Position p2 = pos_cm.getSafe(e2);

            if (p1 == null || p2 == null) {
                return false;
            }

            // circle, circle
            if (circb_cm.has(e1) && circb_cm.has(e2)) {
                CircleBounds cb1 = circb_cm.get(e1);
                CircleBounds cb2 = circb_cm.get(e2);
                Circle c1 = new Circle(p1.x, p1.y, cb1.radius);
                Circle c2 = new Circle(p2.x, p2.y, cb2.radius);
                return Intersector.overlaps(c1, c2);
                // circle, rectangle
            } else if (circb_cm.has(e1) && rectb_cm.has(e2)) {
                CircleBounds cb1 = circb_cm.get(e1);
                RectangleBounds rb2 = rectb_cm.get(e2);
                Circle c1 = new Circle(p1.x, p1.y, cb1.radius);
                Rectangle r2 = new Rectangle(p2.x - rb2.width / 2, p2.y - rb2.height / 2, rb2.width, rb2.height);
                return Intersector.overlaps(c1, r2);
                // rectangle, circle
            } else if (rectb_cm.has(e1) && circb_cm.has(e2)) {
                RectangleBounds rb1 = rectb_cm.get(e1);
                CircleBounds cb2 = circb_cm.get(e2);
                Rectangle r1 = new Rectangle(p1.x - rb1.width / 2, p1.y - rb1.height / 2, rb1.width, rb1.height);
                Circle c2 = new Circle(p2.x, p2.y, cb2.radius);
                return Intersector.overlaps(c2, r1);
                // rectangle, rectangle
            } else if (rectb_cm.has(e1) && rectb_cm.has(e2)) {
                RectangleBounds rb1 = rectb_cm.get(e1);
                RectangleBounds rb2 = rectb_cm.get(e2);
                Rectangle r1 = new Rectangle(p1.x - rb1.width / 2, p1.y - rb1.height / 2, rb1.width, rb1.height);
                Rectangle r2 = new Rectangle(p2.x - rb2.width / 2, p2.y - rb2.height / 2, rb2.width, rb2.height);
                return Intersector.overlaps(r1, r2);
            }

            return false;
        }
    }

    private interface CollisionHandler {
        void handleCollision(Entity a, Entity b);
    }

}
