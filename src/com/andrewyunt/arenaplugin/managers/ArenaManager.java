package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.ArenaCreationException;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaManager {
	
	private Map<String, Arena> arenas = new HashMap<String, Arena>();
	
	public Arena createArena(String name, ArenaType type) throws ArenaCreationException {
		
		if (name == null || type == null)
			throw new ArenaCreationException();
		
		if (arenas.containsKey(name))
			throw new ArenaCreationException("The arena %s already exists and cannot be created again.");
		
		Arena arena = new Arena(name, type);
		
		arenas.put(name, arena);
		
		arena.setEdit(true);
		
		arena.save();
		
		ArenaPlugin.getInstance().logger.info(String.format("Arena %s has been created and set to edit mode.", name)); 
		
		return arena;
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
	
	public boolean arenaExists(String name) {
		
		return arenas.containsKey(name);
	}
	
	public Arena loadArena(ConfigurationSection section) {
		
		Arena arena = Arena.loadFromConfig(section);
		
		arenas.put(arena.getName(), arena);
		
		return arena;
	}
	
	public void loadArenas() {
		
		arenas.clear(); // Clear current arenas list
		
		if(!ArenaPlugin.getInstance().getArenaConfig().getConfig().contains("arenas"))
			return;
		
		ConfigurationSection arenas = ArenaPlugin.getInstance().getArenaConfig().getConfig().getConfigurationSection("arenas");
		
		for(String name : arenas.getValues(false).keySet())
			loadArena(arenas.getConfigurationSection(name));
	}
}