/*
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

/**
 * The class used to store side information.
 * 
 * @author Andrew Yunt
 */
public class GameSide {
	
	/**
	 * The enumeration for the type a GameSide is.
	 * 
	 * <p>
	 * The preferred way to use this enum is GameSide.Type.member
	 * </p>
	 * 
	 * @author Andrew Yunt
	 */
	public enum Type {
		
		BLUE("Blue", ChatColor.BLUE),
		GREEN("Green", ChatColor.GREEN),
		SOLO("Solo", ChatColor.RED);
		
		private final String name;
		private final ChatColor nameColor;
		
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
	
	private final Game game;
	private final Type sideType;
	
	public GameSide(Game game, Type type) {
		
		this.game = game;
		this.sideType = type;
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public Type getSideType() {
		
		return sideType;
	}
}