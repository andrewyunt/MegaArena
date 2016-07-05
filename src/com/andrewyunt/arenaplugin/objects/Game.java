package com.andrewyunt.arenaplugin.objects;

import java.util.HashSet;
import java.util.Set;

public class Game {
	
	private Set<ArenaPlayer> players;
	private Set<Spawn> spawns = new HashSet<Spawn>();
	private Arena arena;
	
	public Game(Arena arena, Set<ArenaPlayer> players) {
		
		this.arena = arena;
		this.players = players;
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public void addPlayer(ArenaPlayer player) {
		
		players.add(player);
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
	
	public void loadSpawns() {
		
		
	}
}
