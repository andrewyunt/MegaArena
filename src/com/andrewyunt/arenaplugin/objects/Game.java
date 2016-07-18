package com.andrewyunt.arenaplugin.objects;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.GameException;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Game {
	
	public enum Side {
		BLUE,
		GREEN,
		INDEPENDENT
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
		
		Player bp = player.getBukkitPlayer();
		
		player.setPreviousHealth(player.getBukkitPlayer().getHealth());
		player.setPreviousExp(bp.getExp());
		player.setPreviousLevel(bp.getLevel());
	
		Side side = null;
		
		spawnPlayer(player, side);
		player.setSide(side);
		players.add(player);
		player.setGame(this);
	}
	
	public void removePlayer(ArenaPlayer player) {
		
		if (arena.getType() == ArenaType.DUEL) {
			try {
				ArenaPlugin.getInstance().getGameManager().deleteGame(this, String.format("Your opponent %s left the game.", player.getName()));
			} catch (GameException e) {
			}
		}
		
		players.remove(player);
		
		try {
			player.getBukkitPlayer().setHealth(ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName()).getPreviousHealth());
		} catch (PlayerException e) {
		}
		
		player.getBukkitPlayer().setHealth(player.getPreviousHealth());
		player.getBukkitPlayer().setExp(player.getPreviousExp());
		player.getBukkitPlayer().setLevel(player.getPreviousLevel());
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
	
	
	public void start() throws GameException {
		
		if (arena.getType() == ArenaType.FFA || arena.getType() == ArenaType.TDM)
			throw new GameException("You cannot start an FFA or TDM game.");
	}
	
	public void end() {
		
		for (ArenaPlayer player : players)
			removePlayer(player);
	}
	
	public void setCountdown(int countdown) {
		
		this.countdown = countdown;
	}
	
	public int getCountdown() {
		
		return countdown;
	}
	
	public void spawnPlayer(ArenaPlayer player, Side side) {
		
		List<Spawn> spawns = null;
		
		if (arena.getType() == ArenaType.DUEL) {
			
			side = Side.INDEPENDENT;
			
			spawns = (List<Spawn>) arena.getSpawns(side);
			
			for (Spawn spawn : arena.getSpawns()) {
				if (spawn.isUsed())
					continue;
				
				player.spawn(spawn);
				spawn.setUsed(true);
			}	
			
		} else if (arena.getType() == ArenaType.FFA) {
			
			side = Side.INDEPENDENT;
			
			spawns = (List<Spawn>) arena.getSpawns(side);
			Collections.shuffle(spawns);
			
			player.spawn(spawns.get(0));
			
		} else if (arena.getType() == ArenaType.TDM) {
			
			double rand = Math.random();
			
			if (rand <= 50)
				side = Side.BLUE;
			else
				side = Side.GREEN;
			
			spawns = (List<Spawn>) arena.getSpawns(side);
			Collections.shuffle(spawns);
			
			player.spawn(spawns.get(0));
		}
	}
}