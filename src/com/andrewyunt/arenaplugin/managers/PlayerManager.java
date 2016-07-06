package com.andrewyunt.arenaplugin.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

public class PlayerManager {
	
	private Map<String, ArenaPlayer> players = new HashMap<String, ArenaPlayer>();
	private Map<ArenaType, ArenaPlayer> inQueue = new HashMap<ArenaType, ArenaPlayer>();
	
	public Collection<ArenaPlayer> getPlayers() {
		
		return players.values();
	}
	
	public boolean queuePlayer(ArenaPlayer player, ArenaType type) {
		
		if (!player.isQueued()) {
			inQueue.put(type, player);
	        
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(ArenaPlugin.getInstance(), new Runnable() {
	            @Override
	            public void run() {
	            	
	            	if (!(player.isInGame())) {
	            		String name = type.name();
	            			
	            		player.getBukkitPlayer().sendMessage(String.format(ChatColor.GOLD +
	            				"You are in the queue for a %s, waiting for other players to join...", name.toLowerCase()));
	            	}	
	            }
	        }, 0L, 20L);
	        
			return true;
		} else {
			return false;
		}
    }
	
	public void removePlayerFromQueue(ArenaPlayer player) {
		
		inQueue.remove(player);
	}
	
	public Collection<ArenaPlayer> getPlayersInQueue(ArenaType type) {
		
		Set<ArenaPlayer> players = new HashSet<ArenaPlayer>();
		
		for (Map.Entry<ArenaType, ArenaPlayer> entry : inQueue.entrySet())
			if (entry.getValue().equals(type))
				players.add(entry.getValue());
		
		return players;
	}
	
	public ArenaPlayer getPlayer(String name) {
		
		return players.get(name);
	}
}