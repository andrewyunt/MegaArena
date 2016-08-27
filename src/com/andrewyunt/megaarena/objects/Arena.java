/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megaarena.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.SpawnException;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * The class used to store arena information.
 * 
 * @author Andrew Yunt
 */
public class Arena {

	/**
	 * @author Andrew Yunt
	 */
	public enum Type {
		DUEL,
		FFA,
		TDM
	}
	
	private Map<String, Spawn> spawns = new HashMap<String, Spawn>();
	private Type type;
	private String name;
	private Game game;
	private boolean isEdit;
	
	public Arena(String name, Type type) {
		
		this.name = name;
		this.type = type;
	}
	
	public Type getType() {
		
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
	
	public Spawn addSpawn(String name, Arena arena, Location loc, GameSide.Type sideType) {
		
		Spawn spawn = new Spawn(name, arena, loc, sideType);
		
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
	
	public Collection<Spawn> getSpawns(GameSide.Type sideType) {
		
		Collection<Spawn> spawns = new HashSet<Spawn>();
		
		for (Map.Entry<String, Spawn> entry : this.spawns.entrySet()) {
			Spawn spawn = entry.getValue();
			
			if (spawn.getSide() == sideType)
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
		
		MegaArena plugin = MegaArena.getInstance();
		FileConfiguration arenaConfig = plugin.getArenaConfig().getConfig();
		
		arenaConfig.set("arenas." + name + ".type", type.toString());
		
		ConfigurationSection spawnsSection = arenaConfig.createSection("arenas." + name + ".spawns");
		
		for (Spawn spawn : spawns.values()) {
			ConfigurationSection spawnSection = spawnsSection.createSection(spawn.getName());
			
			arenaConfig.set("arenas." + name + ".spawns." + spawn.getName() + ".side", spawn.getSide().toString());
			spawnSection.createSection("location", Utils.serializeLocation(spawn.getLocation()));
		}
		
		MegaArena.getInstance().getArenaConfig().saveConfig();
		
		/* Error starts here */
		MegaArena.getInstance().getArenaManager().loadArena(arenaConfig.getConfigurationSection("arenas." + name));
	}
	
	public static Arena loadFromConfig(ConfigurationSection section) {
		
		Arena arena = new Arena(section.getName(), Arena.Type.valueOf(section.getString("type")));
		
		ConfigurationSection spawnsSection = section.getConfigurationSection("spawns");
		
		for (String key : spawnsSection.getKeys(false)) {
			Location loc = Utils.deserializeLocation(spawnsSection.getConfigurationSection(key).getConfigurationSection("location"));
			
			arena.addSpawn(new Spawn(key, arena, loc, GameSide.Type.valueOf(spawnsSection.getConfigurationSection(key).getString("side"))));
		}
		
		return arena;
	}
}