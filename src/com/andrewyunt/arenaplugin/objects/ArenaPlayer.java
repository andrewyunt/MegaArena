package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ArenaPlayer {
	
	private String name;
	private Game game;
	
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
		// TODO Auto-generated method stub
		return false;
	}
}