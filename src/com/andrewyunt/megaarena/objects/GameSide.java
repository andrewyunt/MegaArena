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

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class GameSide {
	
	public enum Type {
		
		BLUE("Blue", ChatColor.BLUE),
		GREEN("Green", ChatColor.DARK_GREEN),
		INDEPENDENT("Independent", ChatColor.DARK_RED);
		
		private String name;
		private ChatColor nameColor;
		
		Type(String name, ChatColor nameColor) {
			
			this.name = name;
			this.nameColor = nameColor;
		}
		
		public String getName() {
			
			return name;
		}
		
		public ChatColor getNameColor() {
			
			return nameColor;
		}
	}
	
	private Game game;
	private Type sideType;
	private Team team;
	
	public GameSide(Game game, Type type) {
		
		this.game = game;
		this.sideType = type;
		
		team = game.getScoreboard().registerNewTeam(type.getName());
		team.setPrefix(sideType.getNameColor() + "");
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public Type getSideType() {
		
		return sideType;
	}
	
	public Team getTeam() {
		
		return team;
	}
}