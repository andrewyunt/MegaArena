package com.andrewyunt.arenaplugin.objects;

public class Arena {

	public enum ArenaType {
		DUEL,
		FFA,
		TDM
	}
	
	private ArenaType type;
	private String name;
	private Game game;
	
	public Arena(String name, ArenaType type) {
		
		this.name = name;
		this.type = type;
	}
	
	public ArenaType getType() {
		
		return type;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public void setGame(Game game) {
		
		this.game = game;
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public boolean isInUse() {
		
		return game != null;
	}
}