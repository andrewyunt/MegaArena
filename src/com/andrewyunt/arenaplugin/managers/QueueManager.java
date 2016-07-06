package com.andrewyunt.arenaplugin.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Queue;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

public class QueueManager {

	private Map<ArenaType, Queue> queues = new HashMap<ArenaType, Queue>();
	
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
}