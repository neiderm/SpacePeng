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
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.Bag;
import de.fgerbig.spacepeng.components.ExpiringComponent;

public class ExpiringComponentSystem extends EntityProcessingSystem {

    public ExpiringComponentSystem() {
        // interested in all entities
        super(Aspect.getAspectForAll());
    }

    @Override
    protected void process(Entity e) {
        // check all components of the entity
        for (Component component : e.getComponents(new Bag<Component>())) {

            if (component instanceof ExpiringComponent) {
                // component is an expiring component
                ExpiringComponent expiringComponent = (ExpiringComponent) component;
                // subtract passed time from delay
                expiringComponent.delay -= world.getDelta();

                // if component has expired, remove it
                if (expiringComponent.delay <= 0) {
                    if (expiringComponent.onExpiry != null) {
                        expiringComponent.onExpiry.run();
                    }
                    e.edit().remove(expiringComponent);
                }
            }
        }

    }
}
