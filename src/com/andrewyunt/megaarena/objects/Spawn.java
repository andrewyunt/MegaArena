package com.andrewyunt.megaarena.objects;

import org.bukkit.Location;

import com.andrewyunt.megaarena.objects.Game.Side;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Spawn {
	
	private boolean isUsed;
	private Arena arena;
	private Location location;
	private Side side;
	private String name;
	
	public Spawn(String name, Arena arena, Location location, Side side) {
		
		this.name = name;
		this.arena = arena;
		this.location = location;
		this.side = side;
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

	public Side getSide() {
		
		return side;
	}
}