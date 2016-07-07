package com.andrewyunt.arenaplugin.objects;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Arena {

	public enum ArenaType {
		DUEL,
		FFA,
		TDM
	}
	
	private ArenaType type;
	private String name;
	private Game game;
	private boolean isEdit;
	
	public Arena(String name, ArenaType type) {
		
		this.name = name;
		this.type = type;
	}
	
	public ArenaType getType() {
		
		return type;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
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
	
	public void addSpawn() {
		
	}
	
	public void setEdit(boolean isEdit) {
		
		this.isEdit = isEdit;
	}
	
	public boolean isEdit() {
		
		return isEdit;
	}
}