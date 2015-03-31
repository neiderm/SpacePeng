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
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.components.Invisible;
import de.fgerbig.spacepeng.components.Player;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Velocity;
import de.fgerbig.spacepeng.events.Event;
import de.fgerbig.spacepeng.events.EventManager;
import de.fgerbig.spacepeng.events.reflection.Handles;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.global.Events;
import de.fgerbig.spacepeng.global.Groups;
import de.fgerbig.spacepeng.global.Tags;
import de.fgerbig.spacepeng.path.AlienBossAttackPathFunction;
import de.fgerbig.spacepeng.path.TwoAlienBossesAttackPathFunction;
import de.fgerbig.spacepeng.screens.MenuScreen;
import de.fgerbig.spacepeng.services.EntityFactory;
import de.fgerbig.spacepeng.services.Profile;

import java.util.LinkedList;
import java.util.Queue;

public class DirectorSystem extends VoidEntitySystem {

    abstract class QueueEvent {
        float delay;

        public QueueEvent(float delay) {
            this.delay = delay;
        }

        abstract void run();
    }

    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Velocity> vlc_cm;
    @Wire
    ComponentMapper<Player> ply_cm;
    @Wire
    ComponentMapper<Invisible> inv_cm;

    EventManager eventManager;
    Queue<QueueEvent> queue;

    Entity playerEntity;
    Player player;
    Profile profile;
    Position playerPos;
    Velocity playerVlc;
    HudRenderSystem hud;
    PlayerInputSystem input;
    AlienBehaviourSystem alienBehaviour;

    protected int level;

    public DirectorSystem(EventManager eventManager) {
        this.eventManager = eventManager;
        this.queue = new LinkedList<QueueEvent>();
    }

    public void setup() {
        playerEntity = world.getManager(TagManager.class).getEntity(Tags.PLAYER);
        player = ply_cm.get(playerEntity);
        profile = SpacePeng.profileManager.retrieveProfile();

        playerPos = pos_cm.get(playerEntity);
        playerVlc = vlc_cm.get(playerEntity);

        hud = world.getSystem(HudRenderSystem.class);
        input = world.getSystem(PlayerInputSystem.class);
        alienBehaviour = world.getSystem(AlienBehaviourSystem.class);

        level = profile.getLastPlayedLevel(); // set to one at (re)start
        input.setFireBlockedForSeconds(2.5f);
        setupPlayer();
        setPlayerUncollidable();
        setupLevel(level);
        eventManager.submit(Events.DISABLE_ACTION, this);

        queue.clear();
        queue.add(new QueueEvent(0.5f) {
            @Override
            void run() {
                hud.overlay = HudRenderSystem.Overlay.LEVEL;
            }
        });

        queue.add(new QueueEvent(2f) {
            @Override
            void run() {
                setupPlayer();
                hud.overlay = HudRenderSystem.Overlay.NONE;
                input.setFireAllowed();
                eventManager.submit(Events.ENABLE_ACTION, this);
            }
        });

    }

    protected void setupPlayer() {
        // player position
        playerPos.x = Const.WIDTH / 2;
        playerPos.y = Const.HEIGHT / 10;
        playerVlc.vectorX = 0;
        playerVlc.vectorY = 0;
        setPlayerVisible();
        setPlayerCollidable();
    }

    protected void setupLevel(int level) {
        int alienCount = 0;

        // construct level
        switch (level % 10) {
            case 1:
                alienCount += setupAliens(0, 0, 10, 3);
                break;
            case 2:
                alienCount += setupAliens(0, 0, 10, 4);
                break;
            case 3:
                EntityFactory.createAlienBoss(world, Const.WIDTH / 2, Const.HEIGHT * 2 / 3, 10, AlienBossAttackPathFunction.id);
                alienCount += 1;
                break;
            case 4:
                alienCount += setupAliens(0, 0, 10, 3);
                break;
            case 5:
                alienCount += setupAliens(0, 0, 10, 4);
                break;
            case 6:
                alienCount += setupAliens(0, 2, 3, 1);
                alienCount += setupAliens(7, 2, 3, 1);
                EntityFactory.createAlienBoss(world, Const.WIDTH / 2, Const.HEIGHT * 2 / 3, 10, AlienBossAttackPathFunction.id);
                alienCount += 1;
                break;
            case 7:
                alienCount += setupAliens(0, 0, 10, 3);
                break;
            case 8:
                alienCount += setupAliens(0, 0, 10, 4);
                break;
            case 9:
                alienCount += setupAliens(0, 0, 3, 3);
                alienCount += setupAliens(7, 0, 3, 3);
                EntityFactory.createAlienBoss(world, Const.WIDTH / 2, Const.HEIGHT * 2 / 3, 10, AlienBossAttackPathFunction.id);
                alienCount += 1;
                break;
            case 0:
                alienCount += setupAliens(3, 0, 4, 3);
                EntityFactory.createAlienBoss(world, Const.WIDTH * 1 / 4, Const.HEIGHT * 2 / 3, 10, TwoAlienBossesAttackPathFunction.id);
                EntityFactory.createAlienBoss(world, Const.WIDTH * 3 / 4, Const.HEIGHT * 2 / 3, 10, TwoAlienBossesAttackPathFunction.id);
                alienCount += 2;
                break;
        }

        alienBehaviour.setLevelParameters(level, alienCount);
    }

    protected int setupAliens(int x, int y, int width, int height) {
        int aliens = 0;

        final int dx = 60;
        final int dy = 60;

        int[][] groupId = {
                {9, 1, 9, 7, 7, 8, 8, 10, 2, 10},
                {1, 1, 1, 5, 7, 8, 6, 2, 2, 2},
                {9, 3, 5, 5, 5, 6, 6, 6, 4, 10},
                {3, 3, 3, 12, 11, 11, 12, 4, 4, 4}};

        for (int ix = x; ix < x + width; ix++) {
            for (int iy = y; iy < y + height; iy++) {
                EntityFactory.createAlien(world, (Const.WIDTH - (9 * dx)) / 2 + ix * dx, Const.HEIGHT - dy * 3 / 2 - iy * dy, groupId[iy][ix]);
                aliens++;
            }
        }

        return aliens;
    }

    @Override
    protected void processSystem() {
        // update profile
        profile.setLastPlayedLevel(level);
        profile.setIfNewHighScore(player.score);

        if (!queue.isEmpty()) {
            QueueEvent queueEvent = queue.peek();
            queueEvent.delay -= world.delta;
            if (queueEvent.delay < 0) {
                queue.remove();
                queueEvent.run();
            }
        }
    }

    @Handles(ids = Events.CONTINUE)
    public void continueEvent(Event event) {
        if (!queue.isEmpty()) {
            QueueEvent queueEvent = queue.peek();
            queueEvent.delay = 0;
        }
    }

    protected void setPlayerVisible() {
        if (inv_cm.has(playerEntity)) {
            playerEntity.edit().remove(Invisible.class);
        }
    }

    protected void setPlayerInvisible() {
        playerEntity.edit().add(new Invisible());
    }

    protected void setPlayerUncollidable() {
        GroupManager gm = world.getManager(GroupManager.class);
        gm.removeFromAllGroups(playerEntity);
    }

    protected void setPlayerCollidable() {
        GroupManager gm = world.getManager(GroupManager.class);
        gm.add(playerEntity, Groups.PLAYER);
    }

    public void increaseScore(int score) {
        player.score += score;
    }

    public int getLevel() {
        return level;
    }

    @Handles(ids = Events.PLAYER_KILLED)
    public void playerKilled(Event event) {
        input.setFireBlockedForSeconds(2.5f);
        setPlayerInvisible();
        setPlayerUncollidable();
        player.lives -= 1;

        // reset aliens
        eventManager.submit(Events.DISABLE_ACTION, this);

        if (player.lives > 0) {
            // player has more lives
            player.setState(Player.State.RESPAWNING);
            eventManager.submit(Events.CHECK_IS_LEVEL_CLEAR, this);

            queue.clear();
            queue.add(new QueueEvent(2) {
                @Override
                void run() {
                    hud.overlay = HudRenderSystem.Overlay.READY;
                    setPlayerVisible();
                }
            });
            queue.add(new QueueEvent(1) {
                @Override
                void run() {
                    player.setState(Player.State.ALIVE);
                    hud.overlay = HudRenderSystem.Overlay.NONE;
                    input.setFireAllowed();
                    setupPlayer();
                    eventManager.submit(Events.ENABLE_ACTION, this);
                }
            });

        } else {
            // player is dead => game over
            player.setState(Player.State.DEAD);
            queue.clear();

            Profile profile = SpacePeng.profileManager.retrieveProfile();

            // new high score?
            if (player.score > profile.getHighScore()) {
                profile.setHighScore(player.score);

                queue.add(new QueueEvent(2f) {
                    @Override
                    void run() {
                        hud.overlay = HudRenderSystem.Overlay.NONE;
                    }
                });
                queue.add(new QueueEvent(0.5f) {
                    @Override
                    void run() {
                        hud.overlay = HudRenderSystem.Overlay.NEW_HIGHSCORE;
                    }
                });
            }

            // new level reached?
            if (level > profile.getLastPlayedLevel()) {
                profile.setLastPlayedLevel(level);
            }

            // display game over message
            queue.add(new QueueEvent(2) {
                @Override
                void run() {
                    eventManager.submit(Events.GAME_OVER, player);
                    hud.overlay = HudRenderSystem.Overlay.GAME_OVER;
                }
            });

            // back to menu
            queue.add(new QueueEvent(10) {
                @Override
                void run() {
                    SpacePeng.currentGame.setScreen(new MenuScreen(SpacePeng.currentGame));
                }
            });

        }
    }

    @Handles(ids = Events.CHECK_IS_LEVEL_CLEAR)
    public void checkIsLevelClear(Event event) {
        if (player.isState(Player.State.DEAD)) {
            return;
        }

        ImmutableBag<Entity> aliens = world.getManager(GroupManager.class).getEntities(Groups.ALIENS);
        if (aliens.size() <= 0) {
            // level is clear
            setPlayerUncollidable();
            eventManager.submit(Events.DISABLE_ACTION, this);
            input.setFireBlockedForSeconds(4.5f);

            queue.clear();
            queue.add(new QueueEvent(1f) {
                @Override
                void run() {
                    hud.overlay = HudRenderSystem.Overlay.LEVEL_DONE;
                }
            });

            queue.add(new QueueEvent(2f) {
                @Override
                void run() {
                    hud.overlay = HudRenderSystem.Overlay.NONE;
                }
            });

            queue.add(new QueueEvent(0.5f) {
                @Override
                void run() {
                    level += 1;
                    hud.overlay = HudRenderSystem.Overlay.LEVEL;
                }
            });

            queue.add(new QueueEvent(2f) {
                @Override
                void run() {
                    setupPlayer();
                    hud.overlay = HudRenderSystem.Overlay.NONE;
                    input.setFireAllowed();
                    setupLevel(level);
                    eventManager.submit(Events.ENABLE_ACTION, this);
                }
            });

        }
    }

}
