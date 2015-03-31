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

package de.fgerbig.spacepeng.services;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import de.fgerbig.spacepeng.SpacePeng;
import de.fgerbig.spacepeng.assets.SoundKey;
import de.fgerbig.spacepeng.components.*;
import de.fgerbig.spacepeng.components.collision.CircleBounds;
import de.fgerbig.spacepeng.components.collision.RectangleBounds;
import de.fgerbig.spacepeng.components.powerup.Coin;
import de.fgerbig.spacepeng.global.Const;
import de.fgerbig.spacepeng.global.Groups;
import de.fgerbig.spacepeng.global.Tags;

public class EntityFactory {

    public static Entity createBackground(World world, String name) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = Const.WIDTH / 2;
        position.y = Const.HEIGHT / 2;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = "background";
        sprite.layer = Sprite.Layer.BACKGROUND;
        sprite.scaleX = 1.25f; // 800/640
        sprite.scaleY = 1.25f;
        e.edit().add(sprite);

        return e;
    }

    public static Entity createPlayer(World world) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = 0;
        position.y = 0;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = Player.SPRITE_NAME;
        sprite.layer = Sprite.Layer.ACTORS;
        e.edit().add(sprite);

        Velocity velocity = new Velocity();
        velocity.vectorX = 0;
        velocity.vectorY = 0;
        e.edit().add(velocity);

        CircleBounds bounds = new CircleBounds();
        bounds.radius = 16;
        e.edit().add(bounds);

        Player info = new Player();
        info.lives = info.DEFAULT_LIVES;
        info.score = 0;
        e.edit().add(info);

        StayOnScreen stayOnScreen = new StayOnScreen();
        e.edit().add(stayOnScreen);

        world.getManager(GroupManager.class).add(e, Groups.PLAYER); // for collision detection
        world.getManager(TagManager.class).register(Tags.PLAYER, e);

        return e;
    }

    public static Entity createPlayerShot(World world, float x, float y) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = x;
        position.y = y;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = "playerShot";
        sprite.layer = Sprite.Layer.EFFECTS;
        e.edit().add(sprite);

        Velocity velocity = new Velocity();
        velocity.vectorY = 800;
        e.edit().add(velocity);

        RectangleBounds bounds = new RectangleBounds();
        bounds.width = 10;
        bounds.height = 45;
        e.edit().add(bounds);

        OffScreenRemove offScreenRemove = new OffScreenRemove();
        e.edit().add(offScreenRemove);

        SpacePeng.soundManager.play(SoundKey.PLAYER_SHOT);

        world.getManager(GroupManager.class).add(e, Groups.PLAYER_SHOTS);

        return e;
    }

    public static Entity createAlien(World world, float x, float y, int groupId) {
        Entity e = world.createEntity();

        BasePosition basePosition = new BasePosition();
        basePosition.x = x;
        basePosition.y = y;
        e.edit().add(basePosition);

        Position position = new Position();
        position.x = x;
        position.y = y;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = Alien.SPRITE_NAME;
        sprite.layer = Sprite.Layer.ACTORS;
        e.edit().add(sprite);

        CircleBounds bounds = new CircleBounds();
        bounds.radius = 14;
        e.edit().add(bounds);

        PathMovement pathMovement = new PathMovement();
        e.edit().add(pathMovement);

        Alien alien = new Alien();
        e.edit().add(alien);

        world.getManager(GroupManager.class).add(e, Groups.ALIENS);
        world.getManager(GroupManager.class).add(e, Groups.ALIEN_ATTACK_GROUP + groupId);

        return e;
    }

    public static Entity createAlienBoss(World world, float x, float y, float health, String pathFunctionId) {
        Entity e = world.createEntity();

        BasePosition basePosition = new BasePosition();
        basePosition.x = x;
        basePosition.y = y;
        e.edit().add(basePosition);

        Position position = new Position();
        position.x = x;
        position.y = y;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = Alien.SPRITE_NAME_BOSS;
        sprite.layer = Sprite.Layer.ACTORS;
        e.edit().add(sprite);

        CircleBounds bounds = new CircleBounds();
        bounds.radius = 62;
        e.edit().add(bounds);

        Health h = new Health();
        h.health = h.maximumHealth = health;
        e.edit().add(h);

        PathMovement pathMovement = new PathMovement();
        e.edit().add(pathMovement);

        AttackPathMovement attackPathMovement = new AttackPathMovement();
        attackPathMovement.pathFunctionId = pathFunctionId;
        e.edit().add(attackPathMovement);

        Alien alien = new Alien();
        e.edit().add(alien);

        world.getManager(GroupManager.class).add(e, Groups.ALIENS);
        world.getManager(GroupManager.class).add(e, Groups.ALIEN_ATTACK_GROUP + 999);

        return e;
    }

    public static Entity createAlienShot(World world, float x, float y) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = x;
        position.y = y;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = "alienShot";
        sprite.layer = Sprite.Layer.EFFECTS;
        e.edit().add(sprite);

        Velocity velocity = new Velocity();
        velocity.vectorY = -400;
        e.edit().add(velocity);

        RectangleBounds bounds = new RectangleBounds();
        bounds.width = 2;
        bounds.height = 10;
        e.edit().add(bounds);

        OffScreenRemove offScreenRemove = new OffScreenRemove();
        e.edit().add(offScreenRemove);

        SpacePeng.soundManager.play(SoundKey.ALIEN_SHOT);

        world.getManager(GroupManager.class).add(e, Groups.ALIEN_SHOTS);

        return e;
    }

    public static void createAlienMultiShot(World world, float x, float y) {
        createAlienShot(world, x, y);
        float displacement;
        if (MathUtils.randomBoolean(0.66f)) {
            displacement = (MathUtils.randomBoolean() ? 1 : -1) * MathUtils.random(10f, 20f);
            EntityFactory.createAlienShot(world, x + displacement, y + MathUtils.random(-10f, 10f));
        }
        if (MathUtils.randomBoolean(0.33f)) {
            displacement = (MathUtils.randomBoolean() ? 1 : -1) * MathUtils.random(10f, 20f);
            EntityFactory.createAlienShot(world, x + displacement, y + MathUtils.random(-10f, 10f));
        }
    }

    protected static Entity createExplosion(World world, float x, float y, float scale) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = x;
        position.y = y;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = "explosion";
        sprite.scaleX = sprite.scaleY = scale;
        sprite.r = 1;
        sprite.g = 216 / 255f;
        sprite.b = 0;
        sprite.a = 0.5f;
        sprite.layer = Sprite.Layer.EFFECTS;
        e.edit().add(sprite);

        AnimationParameters animationParameters = new AnimationParameters();
        animationParameters.frameDuration = 0.5f / 3;
        e.edit().add(animationParameters);

        ExpiringEntity expiringEntity = new ExpiringEntity();
        expiringEntity.delay = 0.5f;
        e.edit().add(expiringEntity);

        ScaleAnimation scaleAnimation = new ScaleAnimation();
        scaleAnimation.active = true;
        scaleAnimation.max = scale;
        scaleAnimation.min = scale / 100f;
        scaleAnimation.speed = 1.0f;
        scaleAnimation.repeat = false;
        e.edit().add(scaleAnimation);

        return e;
    }

    public static Entity createPlayerExplosion(World world, float x, float y) {
        Entity e = EntityFactory.createExplosion(world, x, y, 10f);
        for (int i = 0; i < 50; i++) {
            EntityFactory.createParticle(world, x, y);
        }

        SpacePeng.soundManager.play(SoundKey.PLAYER_EXPLOSION);
        Gdx.input.vibrate(500); //TODO

        return e;
    }

    public static Entity createAlienExplosion(World world, float x, float y) {
        Entity e = EntityFactory.createExplosion(world, x, y, 1.5f);
        for (int i = 0; i < 10; i++) {
            EntityFactory.createParticle(world, x, y);
        }

        SpacePeng.soundManager.play(SoundKey.ALIEN_EXPLOSION);

        return e;
    }

    public static Entity createAlienBossExplosion(World world, float x, float y) {
        Entity e = EntityFactory.createExplosion(world, x, y, 10f);
        for (int i = 0; i < 50; i++) {
            EntityFactory.createParticle(world, x, y);
        }

        SpacePeng.soundManager.play(SoundKey.ALIEN_BOSS_EXPLOSION);

        return e;
    }

    public static Entity createParticle(World world, float x, float y) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = x;
        position.y = y;
        e.edit().add(position);

        Sprite sprite = new Sprite();
        sprite.name = "particle";
        sprite.scaleX = sprite.scaleY = MathUtils.random(0.5f, 1f);
        sprite.r = 1;
        sprite.g = 216 / 255f;
        sprite.b = 0;
        sprite.a = 1f;
        sprite.layer = Sprite.Layer.EFFECTS;
        e.edit().add(sprite);


        float radians = MathUtils.random(2 * MathUtils.PI);
        float magnitude = MathUtils.random(400f);

        Velocity velocity = new Velocity();
        velocity.vectorX = magnitude * MathUtils.cos(radians);
        velocity.vectorY = magnitude * MathUtils.sin(radians);
        e.edit().add(velocity);

        ExpiringEntity expiringEntity = new ExpiringEntity();
        expiringEntity.delay = 1;
        e.edit().add(expiringEntity);

        OffScreenRemove offScreenRemove = new OffScreenRemove();
        e.edit().add(offScreenRemove);

        ColorAnimation colorAnimation = new ColorAnimation();
        colorAnimation.alphaAnimate = true;
        colorAnimation.alphaSpeed = -1f;
        colorAnimation.alphaMin = 0f;
        colorAnimation.alphaMax = 1f;
        colorAnimation.repeat = false;
        e.edit().add(colorAnimation);

        return e;
    }

    public static Entity createCoin(World world, Coin.Type type) {
        Entity e = world.createEntity();

        Position position = new Position();
        position.x = MathUtils.random(Const.POWERUP_BORDER, Const.WIDTH - Const.POWERUP_BORDER);
        position.y = Const.HEIGHT;
        e.edit().add(position);

        e.edit().add(new Coin(type));

        Sprite sprite = new Sprite();
        sprite.name = type.spriteName;
        sprite.layer = Sprite.Layer.EFFECTS;
        sprite.scaleX = 0.75f;
        sprite.scaleY = 0.75f;
        sprite.r = type.r;
        sprite.g = type.g;
        sprite.b = type.b;
        e.edit().add(sprite);

        AnimationParameters animationParameters = new AnimationParameters();
        animationParameters.frameDuration = 0.1f;
        animationParameters.playMode = Animation.PlayMode.LOOP;
        e.edit().add(animationParameters);

        CircleBounds bounds = new CircleBounds();
        bounds.radius = 16;
        e.edit().add(bounds);

        Velocity velocity = new Velocity();
        velocity.vectorX = 0;
        velocity.vectorY = -100;
        e.edit().add(velocity);

        OffScreenRemove offScreenRemove = new OffScreenRemove();
        e.edit().add(offScreenRemove);

        world.getManager(GroupManager.class).add(e, Groups.COINS);

        return e;
    }
}
