/**
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
package com.andrewyunt.megaarena.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.ArenaException;
import com.andrewyunt.megaarena.objects.Arena;

/**
 * The class used to cache arenas, load arenas, and perform operations on them.
 * 
 * @author Andrew Yunt
 */
public class ArenaManager {

	private Map<String, Arena> arenas = new HashMap<String, Arena>();

	/**
	 * Creates an arena with the specified name and arena type.
	 * 
	 * @param name
	 * 		The name of the arena to be created.
	 * @param type
	 * 		The type of the arena to be created from the Arena.Type enumeration.
	 * 		(TDM, FFA, DUEL)
	 * @return
	 * 		The arena that was created and added to the arenas list.
	 * @throws ArenaException
	 * 		If the arena already exists, ArenaException is thrown.
	 */
	public Arena createArena(String name, Arena.Type type) throws ArenaException {

		if (name == null || type == null)
			throw new ArenaException();

		if (arenas.containsKey(name))
			throw new ArenaException("The arena %s already exists and cannot be created again.");

		Arena arena = new Arena(name, type);

		arenas.put(name, arena);

		arena.setEdit(true);

		arena.save();

		MegaArena.getInstance().getLogger().info(ChatColor.GREEN + String
				.format("Arena %s has been created and set to edit mode.", ChatColor.AQUA + name + ChatColor.GREEN));

		return arena;
	}

	/**
	 * Removes the specified arena from the arenas list.
	 * 
	 * @param arena
	 * 		The arena to be deleted from the plugin's records.
	 * @throws ArenaException
	 * 		If the specified arena is null, ArenaException is thrown.
	 */
	public void deleteArena(Arena arena) throws ArenaException {

		if (arena == null)
			throw new ArenaException();

		arenas.remove(arena);
	}

	/**
	 * Gets all registered arenas of the specified arena type.
	 * 
	 * @param type
	 * 		The arena type the arenas must have to be listed.
	 * @return
	 * 		A collection of registered arenas of the specified arena type.
	 */
	public Collection<Arena> getArenas(Arena.Type type) {

		Collection<Arena> arenas = new HashSet<Arena>();

		for (Map.Entry<String, Arena> entry : this.arenas.entrySet()) {
			Arena arena = entry.getValue();

			if (arena.getType() == type)
				arenas.add(arena);
		}

		return arenas;
	}

	/**
	 * Gets all registered arenas on the server.
	 * 
	 * @return
	 * 		A collection of all registered arenas on the server.
	 */
	public Collection<Arena> getArenas() {

		Collection<Arena> arenas = new HashSet<Arena>();

		for (Map.Entry<String, Arena> entry : this.arenas.entrySet())
			arenas.add(entry.getValue());

		return arenas;
	}

	/**
	 * Gets a registered arena of the specified name.
	 * 
	 * @param name
	 * 		The name of the specified arena.
	 * @return
	 * 		The arena fetched of the specified name.
	 * @throws ArenaException
	 * 		If an arena with the specified name does not exist.
	 */
	public Arena getArena(String name) throws ArenaException {

		if (!arenas.containsKey(name))
			throw new ArenaException("The specified arena does not exist.");

		return arenas.get(name);
	}

	/**
	 * Checks if an arena exists with the specified name.
	 * 
	 * @param name
	 * 		The specified name of the arena to check for existence.
	 * @return
	 * 		A boolean value of the arena with the specified name exists.
	 */
	public boolean arenaExists(String name) {

		return arenas.containsKey(name);
	}
	
	/**
	 * Iterates through all arenas in the arenas.yml file and loads them.
	 */
	public void loadArenas() {

		arenas.clear(); // Clear the current arenas list

		if (!MegaArena.getInstance().getArenaConfig().getConfig().contains("arenas"))
			return;

		ConfigurationSection arenas = MegaArena.getInstance().getArenaConfig().getConfig()
				.getConfigurationSection("arenas");

		for (String name : arenas.getValues(false).keySet())
			loadArena(arenas.getConfigurationSection(name));
	}

	/**
	 * Loads the arena from the specified configuration section.
	 * 
	 * @param section
	 * 		The configuration section for the arena to be loaded.
	 * @return
	 * 		The loaded arena from the specified configuration section.
	 */
	public Arena loadArena(ConfigurationSection section) {

		Arena arena = Arena.loadFromConfig(section);

		if (arenaExists(arena.getName()))
			arenas.remove(arena.getName());

		arenas.put(arena.getName(), arena);

		return arena;
	}
}