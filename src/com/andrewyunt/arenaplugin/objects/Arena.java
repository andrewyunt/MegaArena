package com.andrewyunt.arenaplugin.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import com.andrewyunt.arenaplugin.ArenaPlugin;
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
	
	private Set<Spawn> spawns = new HashSet<Spawn>();
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
	
	public void addSpawn(Spawn spawn) {
		
		spawns.add(spawn);
	}
	
	public void removeSpawn(Spawn spawn) {
		
		spawns.remove(spawn);
	}
	
	public Collection<Spawn> getSpawns() {
		
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
		
		ConfigurationSection typeSection = arenaConfig.createSection("arenas." + name + ".type");
		
		MemorySection.createPath(typeSection, type.toString());
		
		ConfigurationSection spawnsSection = arenaConfig.createSection("arenas." + name + ".spawns");
		
		for (Spawn spawn : spawns) {	
			ConfigurationSection spawnSection = spawnsSection.createSection(spawn.getName());
			
			spawnSection.createSection("location", Utils.serializeLocation(spawn.getLocation()));
			
			ConfigurationSection sideSection = arenaConfig.createSection("arenas." + name + ".side");
			
			MemorySection.createPath(sideSection, spawn.getSide().toString());
		}
		
		ArenaPlugin.getInstance().getArenaConfig().saveConfig();
		
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