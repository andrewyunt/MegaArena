/**
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megaarena.utilities;

import org.bukkit.Location;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class DirectionUtils {

	public enum Direction {

		NORTH("North"),
		NORTHEAST("North East"),
		EAST("North West"),
		SOUTHEAST("South West"),
		SOUTH("South"),
		SOUTHWEST("South West"),
		WEST("West"),
		NORTHWEST("North West");

		private String name;

		Direction(String name) {

			this.name = name;
		}

		public String getName() {

			return name;
		}
	}

	/**
	 * Gets the cardinal direction the specified location is facing.
	 * (Can be used to get the way a player is facing by getting their eye location)
	 * 
	 * Credit to sk89q for the degrees of rotation and their corresponding cardinal
	 * directions.
	 * 
	 * @param location
	 * 		The location which you want to get the direction it is facing.
	 * @return
	 * 		The Direction enumeration for the direction the input location is facing.
	 */
	public static Direction getDirection(Location loc) {

		double rotation = (loc.getYaw() - 90) % 360;

		if (rotation < 0)
			rotation += 360.0;

		if (0 <= rotation && rotation < 22.5)
			return Direction.NORTH;
		else if (22.5 <= rotation && rotation < 67.5)
			return Direction.NORTHEAST;
		else if (67.5 <= rotation && rotation < 112.5)
			return Direction.EAST;
		else if (112.5 <= rotation && rotation < 157.5)
			return Direction.SOUTHEAST;
		else if (157.5 <= rotation && rotation < 202.5)
			return Direction.SOUTH;
		else if (202.5 <= rotation && rotation < 247.5)
			return Direction.SOUTHWEST;
		else if (247.5 <= rotation && rotation < 292.5)
			return Direction.WEST;
		else if (292.5 <= rotation && rotation < 337.5)
			return Direction.NORTHWEST;
		else if (337.5 <= rotation && rotation < 360.0)
			return Direction.NORTH;
		else
			return null;
	}
	
	/**
	 * Gets the location relative to the specified location and
	 * cardinal direction at the specified distance.
	 * 
	 * @param location
	 * 		The specified location you wish to get the location next to
	 * 		in the specified cardinal direction.
	 * @param direction
	 * 		The cardinal direction you want to get the location of next
	 * 		to the specified location.
	 * @param distance
	 * 		The distance in in which you want to get the location of in the
	 * 		specified cardinal direction from the specified location.
	 * @return
	 * 		The location next to the specified location in the specified direction.
	 */
	public static Location getRelativeLocation(Location loc, Direction dir, int dist) {
	
		Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		
		newLoc.setPitch(loc.getPitch());
		newLoc.setYaw(loc.getYaw());
		newLoc.setDirection(loc.getDirection());
		
		if (dir == Direction.NORTHEAST) {
			newLoc.setX(loc.getX() +  dist);
			newLoc.setZ(loc.getZ() - dist);
		} else if (dir == Direction.SOUTHEAST) {
			newLoc.setX(loc.getX() +  dist);
			newLoc.setZ(loc.getZ() + dist);
		} else if (dir == Direction.NORTHWEST) {
			newLoc.setX(loc.getX() - dist);
			newLoc.setZ(loc.getZ() - dist);
		} else if (dir == Direction.NORTHEAST) {
			newLoc.setX(loc.getX() +  dist);
			newLoc.setZ(loc.getZ() - dist);
		} else if (dir == Direction.NORTH)
			newLoc.setZ(loc.getZ() - dist);
        else if (dir == Direction.SOUTH)
        	newLoc.setZ(loc.getZ() + dist);
        else if (dir == Direction.EAST)
        	newLoc.setX(loc.getX() +  dist);
        else if (dir == Direction.WEST)
        	newLoc.setX(loc.getX() - dist);
        
       return newLoc;
	}
}