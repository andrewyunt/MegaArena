package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPlayer {
	
	private String name;
	private Game game;
	private Queue queue;
	private ArenaPlayer requestingPlayer;
	private ClassType classType;
	private int classLevel;
	
	public ArenaPlayer(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public Player getBukkitPlayer() {
		
		return Bukkit.getServer().getPlayer(name);
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public void setGame(Game game) {
		
		this.game = game;
	}
	
	public boolean isInGame() {
		
		return game != null;
	}

	public boolean isQueued() {
		
		return queue != null;
	}

	public Queue getQueue() {
		
		return queue;
	}
	
	public void setQueue(Queue queue) {
		
		this.queue = queue;
	}
	
	public ArenaPlayer getRequestingPlayer() {
		
		return requestingPlayer;
	}
	
	public void setRequestingPlayer(ArenaPlayer requestingPlayer) {
		
		this.requestingPlayer = requestingPlayer;
	}
	
	public boolean hasDuelRequest() {
		
		return requestingPlayer != null;
	}
	
	public void setClassType(ClassType classType) {
		
		this.classType = classType;
	}
	
	public ClassType getClassType() {
		
		return classType;
	}
	
	public void setClassLevel(int classLevel) {
		
		this.classLevel = classLevel;
	}
	
	public int getClassLevel() {
		
		return classLevel;
	}
}