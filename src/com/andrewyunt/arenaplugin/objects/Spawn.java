package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Location;

import com.andrewyunt.arenaplugin.objects.Game.Side;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Spawn {
	
	private boolean used;
	private Arena arena;
	private Location location;
	private Side side;
	private String name;
	
	public Spawn(String name, Arena arena, Location location, Side side) {
		
		this.arena = arena;
		this.location = location;
		this.side = side;
	}
	
	public boolean isUsed() {
		
		return used;
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