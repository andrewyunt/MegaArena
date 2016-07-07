package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Location;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Spawn {
	
	private Game game;
	private boolean used;
	private Location location;
	
	private Spawn(Game game) {
		
		this.game = game;;
	}
	
	public boolean isUsed() {
		
		return used;
	}
	
	public Location getLocation() {
		
		return location;
	}
	
	public Game getGame() {
		
		return game;
	}
}