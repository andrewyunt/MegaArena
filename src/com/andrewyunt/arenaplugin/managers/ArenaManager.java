package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

public class ArenaManager {
	
	private Map<String, Arena> arenas = new HashMap<String, Arena>();
	
	public Collection<Arena> getArenas() {
		
		return arenas.values();
	}
	
	public Collection<Arena> getArenas(ArenaType type) {
		
		Collection<Arena> arenas = new HashSet<Arena>();
		
		for (Map.Entry<String, Arena> entry : this.arenas.entrySet()) {
			Arena arena = entry.getValue();
			
			if (arena.getType() == type)
				arenas.add(arena);
		}
		
		return arenas;
	}
	
	public Arena getArena(String name) {
		
		return arenas.get(name);
	}
}