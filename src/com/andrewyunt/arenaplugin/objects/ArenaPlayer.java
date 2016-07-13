package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.exception.ArenaException;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPlayer {
	
	private String name;
	private Game game;
	private ArenaPlayer requestingPlayer;
	private ClassType classType;
	private boolean hasFallen;
	private ItemStack[] previousContents;
	private Arena selectedArena;
	
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
	
	public void setHasFallen(boolean hasFallen) {
		
		this.hasFallen = hasFallen;
	}
	
	public boolean hasFallen() {
		
		return hasFallen;
	}
	
	public void setPreviousContents(ItemStack[] previousContents) {
		
		this.previousContents = previousContents;
	}
	
	public ItemStack[] getPreviousContents() {
		
		return previousContents;
	}
	
	public void selectArena(Arena selectedArena) {
		
		this.selectedArena = selectedArena;
		
		getBukkitPlayer().sendMessage(String.format(ChatColor.GOLD + "You have selected the arena %s", selectedArena.getName()));
	}
	
	public boolean hasSelectedArena() {
		
		return selectedArena != null;
	}
	
	public Arena getSelectedArena() throws ArenaException {
		
		if (selectedArena == null)
			throw new ArenaException("The player has not selected an arena");
		
		return selectedArena;
	}
}