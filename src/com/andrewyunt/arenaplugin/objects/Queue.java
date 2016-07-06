package com.andrewyunt.arenaplugin.objects;

import java.util.HashSet;
import java.util.Set;

import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

public class Queue {
	
	private ArenaType type;
	private Set<ArenaPlayer> players = new HashSet<ArenaPlayer>();
	
	public Queue(ArenaType type) {
		
		this.type = type;
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
}