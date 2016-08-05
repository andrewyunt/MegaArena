package com.andrewyunt.megaarena.objects;

import org.bukkit.ChatColor;

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
	private Type type;
	
	public GameSide(Game game, Type type) {
		
		this.game = game;
		this.type = type;
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public Type getType() {
		
		return type;
	}
}