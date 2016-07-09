package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class PlayerManager {
	
	private Map<String, ArenaPlayer> players = new HashMap<String, ArenaPlayer>();
	
	public ArenaPlayer createPlayer(String name) {
		
		ArenaPlayer player = new ArenaPlayer(name);
		
		players.put(name, player);
		
		return player;
	}
	
	public Collection<ArenaPlayer> getPlayers() {
		
		return players.values();
	}
	
	public ArenaPlayer getPlayer(String name) {
		
		return players.get(name);
	}
}