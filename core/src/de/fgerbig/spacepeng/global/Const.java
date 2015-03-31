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

package de.fgerbig.spacepeng.global;

public class Const {

    public static final String VERSION = "V1.58-1";

    public static final String NAME = "SpacePeng";
    ;

    // whether we are in development mode
    public static final boolean DEV_MODE = false;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;

    public static final float POWERUP_BORDER = 50.0f;

    public static final boolean IS_OS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");

}
