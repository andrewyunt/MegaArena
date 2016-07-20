package com.andrewyunt.arenaplugin.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.SpawnException;
import com.andrewyunt.arenaplugin.objects.Game.Side;
import com.andrewyunt.arenaplugin.utilities.Utils;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Arena {

	public enum ArenaType {
		DUEL,
		FFA,
		TDM
	}
	
	private Map<String, Spawn> spawns = new HashMap<String, Spawn>();
	private ArenaType type;
	private String name;
	private Game game;
	private boolean isEdit;
	
	public Arena(String name, ArenaType type) {
		
		this.name = name;
		this.type = type;
	}
	
	public ArenaType getType() {
		
		return type;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setGame(Game game) {
		
		this.game = game;
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public boolean isInUse() {
		
		return game != null;
	}
	
	public Spawn addSpawn(String name, Arena arena, Location loc, Side side) {
		
		Spawn spawn = new Spawn(name, arena, loc, side);
		
		spawns.put(name, spawn);
		
		save();
		
		return spawn;
	}
	
	public void addSpawn(Spawn spawn) {
		
		spawns.put(spawn.getName(), spawn);
	}
	
	public void removeSpawn(Spawn spawn) {
		
		spawns.remove(spawn);
	}
	
	public Spawn getSpawn(String name) throws SpawnException {
		
		if (!spawns.containsKey(name))
			throw new SpawnException(String.format("The spawn %s does not exist in the arena %s.", name, this.name));
		
		return spawns.get(name);
	}
	
	public Collection<Spawn> getSpawns() {
		
		return spawns.values();
	}
	
	public Collection<Spawn> getSpawns(Side side) {
		
		Collection<Spawn> spawns = new HashSet<Spawn>();
		
		for (Map.Entry<String, Spawn> entry : this.spawns.entrySet()) {
			Spawn spawn = entry.getValue();
			
			if (spawn.getSide() == side)
				spawns.add(spawn);
		}
		
		return spawns;
	}
	
	public void setEdit(boolean isEdit) {
		
		this.isEdit = isEdit;
	}
	
	public boolean isEdit() {
		
		return isEdit;
	}
	
	public void save() {
		
		ArenaPlugin plugin = ArenaPlugin.getInstance();
		FileConfiguration arenaConfig = plugin.getArenaConfig().getConfig();
		
		arenaConfig.set("arenas." + name + ".type", type.toString());
		
		ConfigurationSection spawnsSection = arenaConfig.createSection("arenas." + name + ".spawns");
		
		for (Spawn spawn : spawns.values()) {
			ConfigurationSection spawnSection = spawnsSection.createSection(spawn.getName());
			
			arenaConfig.set("arenas." + name + ".spawns." + spawn.getName() + ".side", spawn.getSide().toString());
			spawnSection.createSection("location", Utils.serializeLocation(spawn.getLocation()));
		}
		
		ArenaPlugin.getInstance().getArenaConfig().saveConfig();
		
		/* Error starts here */
		ArenaPlugin.getInstance().getArenaManager().loadArena(arenaConfig.getConfigurationSection("arenas." + name));
	}
	
	public static Arena loadFromConfig(ConfigurationSection section) {
		
		Arena arena = new Arena(section.getName(), ArenaType.valueOf(section.getString("type")));
		
		ConfigurationSection spawnsSection = section.getConfigurationSection("spawns");
		
		for (String key : spawnsSection.getKeys(false)) {
			Location loc = Utils.deserializeLocation(spawnsSection.getConfigurationSection(key).getConfigurationSection("location"));
			
			arena.addSpawn(new Spawn(key, arena, loc, Side.valueOf(spawnsSection.getConfigurationSection(key).getString("side"))));
		}
		
		return arena;
	}
}