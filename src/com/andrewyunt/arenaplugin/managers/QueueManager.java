package com.andrewyunt.arenaplugin.managers;

import java.util.HashMap;
import java.util.Map;

import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Queue;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

public class QueueManager {

	private Map<ArenaType, Queue> queues = new HashMap<ArenaType, Queue>();
	
	public boolean queuePlayer(ArenaPlayer player, ArenaType type) {
		
		if (!player.isQueued()) {
			queues.get(type).addPlayer(player);
			return true;
		} else {
			return false;
		}
    }
	
	public void removePlayerFromQueue(ArenaPlayer player) {
		
		player.getQueue().removePlayer(player);
	}
	
	public void removeQueue(Queue queue) {
		
		queues.remove(queue);
	}
}