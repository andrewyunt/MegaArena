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
package com.andrewyunt.megaarena.objects;

import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;

/**
 * The enumeration for abilities, their names, and the method to use them.
 * 
 * @author Andrew Yunt
 */
public enum Skill {
	
	RESIST("Resist"),
	SWIFTNESS("Swiftness"),
	BOOMERANG("Boomerang"),
	MUTUAL_WEAKNESS("Mutual Weakness"),
	RECHARGE("Recharge"),
	FLURRY("Flurry"),
	POWERFUL_WEAKNESS("Powerful Weakness"),
	SUPPORT("Support"),
	WEAKENING_SWING("Weakening Swing"),
	SWIFT_BACKUP("Swift Backup"),
	SOUL_SUCKER("Soul Sucker"),
	UNDEAD("Undead");
	
	String name;
	
	Skill(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	/**
	 * Gets the skill level for the specified player.
	 * 
	 * @param player
	 * 		The specified player to get the skill level of.
	 * @return
	 * 		The skill level (1-9) of the specified player.
	 */
	public int getLevel(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("megaarena.%s.%s", this.toString().toLowerCase(), i)))
				return i;
		
		return 1;
	}
	
	/**
	 * Sets the skill level for the specified player to the specified level.
	 * 
	 * <p>
	 * The level integer must be from 1-9 inclusive.
	 * </p>
	 * 
	 * @param player
	 * 		The player to set the skill level of.
	 * @param level
	 * 		The level to set the player's ability to.
	 */
	public void setLevel(GamePlayer player, int level) {
		
		MegaArena.getInstance().getServer().dispatchCommand(MegaArena.getInstance().getServer().getConsoleSender(),
				String.format("pex user %s add megaarena.%s.%s", player.getName(), this.toString().toLowerCase(), level));
	}
}