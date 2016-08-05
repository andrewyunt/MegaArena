package com.andrewyunt.megaarena.objects;

import org.bukkit.Location;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Spawn {
	
	private boolean isUsed;
	private Arena arena;
	private Location location;
	private GameSide.Type sideType;
	private String name;
	
	public Spawn(String name, Arena arena, Location location, GameSide.Type sideType) {
		
		this.name = name;
		this.arena = arena;
		this.location = location;
		this.sideType = sideType;
	}
	
	public void setUsed(boolean isUsed) {
		
		this.isUsed = isUsed;
	}
	
	public boolean isUsed() {
		
		return isUsed;
	}
	
	public String getName() {
		
		return name;
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public Location getLocation() {
		
		return location;
	}

	public GameSide.Type getSide() {
		
		return sideType;
	}
}