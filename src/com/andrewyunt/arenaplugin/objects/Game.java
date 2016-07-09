package com.andrewyunt.arenaplugin.objects;

import java.util.Set;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Game {
	
	public enum Side {
		BLUE,
		GREEN
	}
	
	private Set<ArenaPlayer> players;
	private Arena arena;
	private boolean isActive;
	
	public Game(Arena arena, Set<ArenaPlayer> players) {
		
		this.arena = arena;
		this.players = players;
		
		for (ArenaPlayer player : players)
			player.setGame(this);
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public void setActive(boolean isActive) {
		
		this.isActive = isActive;
	}
	
	public boolean isActive() {
		
		return isActive;
	}
	
	public void addPlayer(ArenaPlayer player) {
		
		players.add(player);
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
	
	
	public void start() {
		
	}
	
	public void end() {
		
	}
}