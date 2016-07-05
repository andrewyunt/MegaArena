package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewyunt.arenaplugin.objects.Arena;

public class ArenaManager {
	
	private Map<String, Arena> arenas = new HashMap<String, Arena>();
	
	public Collection<Arena> getArenas() {
		
		return arenas.values();
	}
	
	public Arena getArena(String name) {
		
		return arenas.get(name);
	}
}