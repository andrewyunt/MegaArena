package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

public class PlayerManager {
	
	private Map<String, ArenaPlayer> players = new HashMap<String, ArenaPlayer>();
	
	public Collection<ArenaPlayer> getPlayers() {
		
		return players.values();
	}
	
	public ArenaPlayer getPlayer(String name) {
		
		return players.get(name);
	}
}