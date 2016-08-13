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
 * The utilities class used to perform operations on directions
 * and the data related to directions.
 * 
 * @author Andrew Yunt
 */
public class DirectionUtils {

	/**
	 * The enumeration for cardinal directions, their names, and their degrees.
	 * 
	 * @author Andrew Yunt
	 */
	public enum CardinalDirection {

		NORTH("North", 0),
		NORTH_NORTH_EAST("North North East", 22.5),
		NORTH_EAST("North East", 45),
		EAST_NORTH_EAST("East North East", 67.5),
		EAST("East", 90),
		EAST_SOUTH_EAST("East South East", 112.5),
		SOUTH_EAST("South East", 135),
		SOUTH_SOUTH_EAST("South South East", 157.5),
		SOUTH("South", 180),
		SOUTH_SOUTH_WEST("South South West", 202.5),
		SOUTH_WEST("South West", 225),
		WEST_SOUTH_WEST("West South West", 247.5),
		WEST("West", 270),
		WEST_NORTH_WEST("West North West", 292.5),
		NORTH_WEST("North West", 315),
		NORTH_NORTH_WEST("North North West", 337.5);

		private String name;
		private double degrees;

		CardinalDirection(String name, double degrees) {

			this.name = name;
			this.degrees = degrees;
		}

		/**
		 * Gets the name of the cardinal direction.
		 * 
		 * @return
		 * 		The name of the cardinal direction.
		 */
		public String getName() {

			return name;
		}
		
		/**
		 * Gets the degrees in which the cardinal direction is facing.
		 * 
		 * @return
		 * 		The degrees the cardinal direction is facing.
		 */
		public double getDegrees() {
			
			return degrees;
		}
		
		/**
		 * Gets the cardinal direction rotated by the specified number of degrees.
		 * 
		 * @param degrees
		 * 		The number of degrees to rotate the cardinal direction.
		 * @return
		 * 		The cardinal direction for the rotated number of degrees.
		 */
		public CardinalDirection rotate(double degrees) {
			
			double newDegrees = this.degrees + degrees;
			
			while (true) {
				if (newDegrees > 360) {
					newDegrees = newDegrees % 360;
					continue;
				}
				
				if (newDegrees < 0) {
					newDegrees = 360 - newDegrees;
					continue;
				}
				
				break;
			}
			
			return getCardinalDirection(newDegrees);
		}
	}

	/**
	 * Gets the cardinal direction the specified location is facing.
	 * 
	 * <p>
	 * (Can be used to get the way a player is facing by getting their eye location)
	 * </p>
	 * 
	 * @param location
	 * 		The location which you want to get the direction it is facing.
	 * @return
	 * 		The Direction enumeration for the direction the input location is facing.
	 */
	public static CardinalDirection getCardinalDirection(Location loc) {

		double degrees = (loc.getYaw() - 90) % 360;

		return getCardinalDirection(degrees);
	}
	
	/**
	 * Gets a cardinal direction from the specified number of degrees.
	 * 
	 * <p>
	 * Credit to sk89q for the degrees of degrees and their corresponding cardinal
	 * directions.
	 * </p>
	 * 
	 * @param degrees
	 * 		The number of degrees the direction is facing.
	 * @return
	 * 		The cardinal direction converted from the specified number of degrees.
	 */
	public static CardinalDirection getCardinalDirection(double degrees) {
		
		if (degrees < 0)
			degrees += 360.0;

		if (0 <= degrees && degrees < 22.5)
			return CardinalDirection.NORTH;
		else if (22.5 <= degrees && degrees < 67.5)
			return CardinalDirection.NORTH_EAST;
		else if (67.5 <= degrees && degrees < 112.5)
			return CardinalDirection.EAST;
		else if (112.5 <= degrees && degrees < 157.5)
			return CardinalDirection.SOUTH_EAST;
		else if (157.5 <= degrees && degrees < 202.5)
			return CardinalDirection.SOUTH;
		else if (202.5 <= degrees && degrees < 247.5)
			return CardinalDirection.SOUTH_WEST;
		else if (247.5 <= degrees && degrees < 292.5)
			return CardinalDirection.WEST;
		else if (292.5 <= degrees && degrees < 337.5)
			return CardinalDirection.NORTH_WEST;
		else if (337.5 <= degrees && degrees < 360.0)
			return CardinalDirection.NORTH;
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
	public static Location getRelativeLocation(Location loc, CardinalDirection dir, int dist) {
	
		Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		
		newLoc.setPitch(loc.getPitch());
		newLoc.setYaw(loc.getYaw());
		newLoc.setDirection(loc.getDirection());
		
		if (dir == CardinalDirection.NORTH_EAST) {
			newLoc.setX(loc.getX() +  dist);
			newLoc.setZ(loc.getZ() - dist);
		} else if (dir == CardinalDirection.SOUTH_EAST) {
			newLoc.setX(loc.getX() +  dist);
			newLoc.setZ(loc.getZ() + dist);
		} else if (dir == CardinalDirection.SOUTH_WEST) {
			newLoc.setX(loc.getX() - dist);
			newLoc.setZ(loc.getZ() + dist);
		} else if (dir == CardinalDirection.NORTH_WEST) {
			newLoc.setX(loc.getX() - dist);
			newLoc.setZ(loc.getZ() - dist);
		} else if (dir == CardinalDirection.NORTH)
			newLoc.setZ(loc.getZ() - dist);
		else if (dir == CardinalDirection.SOUTH)
			newLoc.setZ(loc.getZ() + dist);
		else if (dir == CardinalDirection.EAST)
			newLoc.setX(loc.getX() +  dist);
		else if (dir == CardinalDirection.WEST)
			newLoc.setX(loc.getX() - dist);
        
		return newLoc;
	}
}