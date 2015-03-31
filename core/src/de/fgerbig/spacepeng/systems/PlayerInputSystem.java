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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.fgerbig.spacepeng.components.Player;
import de.fgerbig.spacepeng.components.Position;
import de.fgerbig.spacepeng.components.Velocity;
import de.fgerbig.spacepeng.components.powerup.DoubleShot;
import de.fgerbig.spacepeng.events.EventManager;
import de.fgerbig.spacepeng.global.Events;
import de.fgerbig.spacepeng.services.EntityFactory;

public class PlayerInputSystem extends EntityProcessingSystem implements InputProcessor, ControllerListener {

    public enum FireState {
        ALLOW,      // allow normal game input
        CONTINUE,   // if player shoots continue
        BLOCKED;    // block input
    }

    private static final float HORIZONTAL_THRUSTERS = 500;
    private static final float HORIZONTAL_MAX_SPEED = 500;
    private static final float FIRE_RATE = 0.25f;
    private static final float DAMPING = 25;

    @Wire
    ComponentMapper<Position> pos_cm;
    @Wire
    ComponentMapper<Velocity> vlc_cm;
    @Wire
    ComponentMapper<DoubleShot> doubleShot_cm;

    EventManager eventManager;

    private FireState fireState = FireState.ALLOW;

    private boolean left, right;
    private boolean shoot;
    private float timeToShoot;
    private float timeToContinue;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseVector;

    private boolean directInput;

    public PlayerInputSystem(EventManager eventManager, OrthographicCamera camera, Viewport viewport) {
        super(Aspect.getAspectForAll(Position.class, Velocity.class, Player.class));
        this.eventManager = eventManager;
        this.camera = camera;
        this.viewport = viewport;
        this.mouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
    }

    @Override
    protected void initialize() {
        Gdx.input.setInputProcessor(this);
        Controllers.addListener(this);
    }

    @Override
    protected void process(Entity e) {
        Position position = pos_cm.get(e);
        Velocity velocity = vlc_cm.get(e);

        mouseVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseVector, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

        if (directInput) {
            position.x = mouseVector.x;
        } else {
            if (left) {
                velocity.vectorX = MathUtils.clamp(velocity.vectorX - (world.getDelta() * HORIZONTAL_THRUSTERS), -HORIZONTAL_MAX_SPEED, HORIZONTAL_MAX_SPEED);
            } else if (right) {
                velocity.vectorX = MathUtils.clamp(velocity.vectorX + (world.getDelta() * HORIZONTAL_THRUSTERS), -HORIZONTAL_MAX_SPEED, HORIZONTAL_MAX_SPEED);
            } else {
                if (Math.abs(velocity.vectorX) < DAMPING) {
                    velocity.vectorX = 0;
                } else {
                    velocity.vectorX -= Math.signum(velocity.vectorX) * DAMPING;
                }
            }
        }

        switch (fireState) {

            case BLOCKED:
                timeToContinue -= world.delta;

                if (timeToContinue <= 0) {
                    timeToContinue = 0;
                    this.fireState = FireState.CONTINUE;
                }
                break;

            case CONTINUE:
                if (shoot) {
                    eventManager.submit(Events.CONTINUE, this);
                }
                break;

            case ALLOW:
                if (shoot) {
                    if (timeToShoot <= 0) {
                        if (doubleShot_cm.has(e)) {
                            EntityFactory.createPlayerShot(world, position.x - 15, position.y);
                            EntityFactory.createPlayerShot(world, position.x + 15, position.y);
                        } else {
                            EntityFactory.createPlayerShot(world, position.x, position.y);
                        }
                        timeToShoot = FIRE_RATE;
                    }
                }
                if (timeToShoot > 0) {
                    timeToShoot -= world.delta;
                    if (timeToShoot < 0) {
                        timeToShoot = 0;
                    }
                }
                break;
        }

    }

    public void setFireAllowed() {
        this.fireState = FireState.ALLOW;
    }

    /**
     * After the given seconds where no "fire" is possible, pressing the fire button will be counted as continue, until explicitly allowed.
     *
     * @param timeToContinue seconds to block "fire"
     */
    public void setFireBlockedForSeconds(float timeToContinue) {
        this.fireState = FireState.BLOCKED;
        this.timeToContinue = timeToContinue;
    }

    @Override
    public boolean keyDown(int keycode) {
        //Gdx.app.log(Const.NAME, "keyDown(keycode=" + keycode + ")");
        directInput = false;

        if ((keycode == Input.Keys.LEFT) ||
                (keycode == Input.Keys.NUMPAD_4) ||
                (keycode == Input.Keys.A) ||
                (keycode == Input.Keys.J)) {
            left = true;

        } else if ((keycode == Input.Keys.RIGHT) ||
                (keycode == Input.Keys.NUMPAD_6) ||
                (keycode == Input.Keys.D) ||
                (keycode == Input.Keys.L)) {
            right = true;

        } else if ((keycode == Input.Keys.SPACE) ||
                (keycode == Input.Keys.NUMPAD_0) ||
                (keycode == Input.Keys.CONTROL_LEFT) ||
                (keycode == Input.Keys.CONTROL_RIGHT)) {
            shoot = true;
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        //Gdx.app.log(Const.NAME, "keyUp(keycode=" + keycode + ")");

        if ((keycode == Input.Keys.LEFT) ||
                (keycode == Input.Keys.NUMPAD_4) ||
                (keycode == Input.Keys.A) ||
                (keycode == Input.Keys.J)) {
            left = false;

        } else if ((keycode == Input.Keys.RIGHT) ||
                (keycode == Input.Keys.NUMPAD_6) ||
                (keycode == Input.Keys.D) ||
                (keycode == Input.Keys.L)) {
            right = false;

        } else if ((keycode == Input.Keys.SPACE) ||
                (keycode == Input.Keys.NUMPAD_0) ||
                (keycode == Input.Keys.CONTROL_LEFT) ||
                (keycode == Input.Keys.CONTROL_RIGHT)) {
            shoot = false;
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        //Gdx.app.log(Const.NAME, "keyTyped(character=" + character + ")");
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        //Gdx.app.log(Const.NAME, "touchDown(x=" + x + ", y=" + y + ", pointer=" + pointer + ", button=" + button + ")");
        directInput = true;
        if (button == Input.Buttons.LEFT) {
            shoot = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        //Gdx.app.log(Const.NAME, "touchUp(x=" + x + ", y=" + y + ", pointer=" + pointer + ", button=" + button + ")");
        if (button == Input.Buttons.LEFT) {
            shoot = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        //Gdx.app.log(Const.NAME, "touchDragged(x=" + x + ", y=" + y + ", pointer=" + pointer + ")");
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        //Gdx.app.log(Const.NAME, "scrolled(amount=" + amount + ")");
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //Gdx.app.log(Const.NAME, "mouseMoved(screenX=" + screenX + ", screenY=" + screenY + ")");
        return false;
    }

    // controller
    @Override
    public void connected(Controller controller) {
        //Gdx.app.log(Const.NAME, "connected(controller=" + controller + ")");
    }

    @Override
    public void disconnected(Controller controller) {
        //Gdx.app.log(Const.NAME, "disconnected(controller=" + controller + ")");
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        //Gdx.app.log(Const.NAME, "buttonDown(controller=" + controller + ", buttonCode=" + buttonCode + ")");
        shoot = true;
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        //Gdx.app.log(Const.NAME, "buttonUp(controller=" + controller + ", buttonCode=" + buttonCode + ")");
        shoot = false;
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        //Gdx.app.log(Const.NAME, "axisMoved(controller=" + controller + ", axisCode=" + axisCode + ", value=" + value + ")");

        left = false;
        right = false;

        if (axisCode == 0) {
            if (value < -0.2f) {
                left = true;
            }
            if (value >= 0.2f) {
                right = true;
            }

        }
        return true;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        //Gdx.app.log(Const.NAME, "povMoved(controller=" + controller + ", povCode=" + povCode + ", value=" + value + ")");
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        //Gdx.app.log(Const.NAME, "xSliderMoved(controller=" + controller + ", sliderCode=" + sliderCode + ", value=" + value + ")");
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        //Gdx.app.log(Const.NAME, "ySliderMoved(controller=" + controller + ", sliderCode=" + sliderCode + ", value=" + value + ")");
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        //Gdx.app.log(Const.NAME, "accelerometerMoved(controller=" + controller + ", accelerometerCode=" + accelerometerCode + ", value=" + value + ")");
        return false;
    }


}
