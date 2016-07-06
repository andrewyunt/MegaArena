package com.andrewyunt.arenaplugin.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

public class Queue {
	
	private ArenaType type;
	private Set<ArenaPlayer> players = new HashSet<ArenaPlayer>();
	
	public Queue(ArenaType type) {
		
		this.type = type;
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(ArenaPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
            		broadcast(String.format(
            				ChatColor.GOLD + "You are in the queue for a"
            						+ " %s, waiting for other players to join...",
            						type.toString().toLowerCase()));
            }
        }, 0L, 100L);
    }
	
	public ArenaType getType() {
		
		return type;
	}
	
	public void addPlayer(ArenaPlayer player) {
		
		players.add(player);
	}
	
	public void removePlayer(ArenaPlayer player) {
		
		players.remove(player);
		
		player.setQueue(null);
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
	
	public void broadcast(String msg) {
		
		for (ArenaPlayer player : players)
			player.getBukkitPlayer().sendMessage(msg);
	}
}