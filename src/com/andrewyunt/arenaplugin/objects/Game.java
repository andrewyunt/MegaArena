package com.andrewyunt.arenaplugin.objects;

import java.util.Set;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.GameException;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Game {
	
	public enum Side {
		BLUE,
		GREEN
	}
	
	private Set<ArenaPlayer> players;
	private Arena arena;
	private int countdown;
	private boolean isActive;
	
	public Game(Arena arena, Set<ArenaPlayer> players) {
		
		this.arena = arena;
		this.players = players;
		
		for (ArenaPlayer player : players)
			player.setGame(this);
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public void setActive(boolean isActive) {
		
		this.isActive = isActive;
	}
	
	public boolean isActive() {
		
		return isActive;
	}
	
	public void addPlayer(ArenaPlayer player) {
		
		players.add(player);
	}
	
	public void removePlayer(ArenaPlayer player) {
		
		if (arena.getType() == ArenaType.DUEL) {
			try {
				ArenaPlugin.getInstance().getGameManager().deleteGame(this, String.format("Your opponent %s left the game.", player.getName()));
			} catch (GameException e) {
			}
		}
		
		players.remove(player);
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
	
	
	public void start() throws GameException {
		
		if (arena.getType() == ArenaType.FFA || arena.getType() == ArenaType.TDM)
			throw new GameException("You cannot start an FFA or TDM game.");
	}
	
	public void end() throws GameException {
		
		if (arena.getType() == ArenaType.FFA || arena.getType() == ArenaType.TDM)
			throw new GameException("You cannot end an FFA or TDM game.");
	}
	
	public void setCountdown(int countdown) {
		
		this.countdown = countdown;
	}
	
	public int getCountdown() {
		
		return countdown;
	}
}