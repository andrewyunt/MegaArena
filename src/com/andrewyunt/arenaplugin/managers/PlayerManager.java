package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class PlayerManager {
	
	private Map<String, ArenaPlayer> players = new HashMap<String, ArenaPlayer>();
	
	public ArenaPlayer createPlayer(String name) throws PlayerException {
		
		if (players.containsKey(name))
			throw new PlayerException(String.format("The player %s already exists.", name));
		
		ArenaPlayer player = new ArenaPlayer(name);
		
		players.put(name, player);
		
		return player;
	}
	
	public Collection<ArenaPlayer> getPlayers() {
		
		return players.values();
	}
	
	public ArenaPlayer getPlayer(String name) throws PlayerException {
		
		if (!players.containsKey(name))
			throw new PlayerException("The specified player does not exist.");
		
		return players.get(name);
	}
}