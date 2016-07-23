package com.andrewyunt.arenaplugin.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
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
	
	private Set<ArenaPlayer> players = new HashSet<ArenaPlayer>();
	private Arena arena;

	public Game(Arena arena) {
		
		this.arena = arena;
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public void addPlayer(ArenaPlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		player.setPreviousHealth(player.getBukkitPlayer().getHealth());
		player.setPreviousFoodLevel(player.getPreviousFoodLevel());
		player.setPreviousExp(bp.getExp());
		player.setPreviousLevel(bp.getLevel());
		player.setPreviousGameMode(bp.getGameMode());
		player.setPreviousContents(bp.getInventory().getContents());
		player.setPreviousLocation(bp.getLocation());
	
		Side side = null;
		
		if (arena.getType() == ArenaType.DUEL || arena.getType() == ArenaType.FFA)
			side = Side.INDEPENDENT;
		else if (arena.getType() == ArenaType.TDM) {
			double rand = Math.random();
			
			if (rand <= 50)
				side = Side.BLUE;
			else
				side = Side.GREEN;
		}
		
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
		player.setGame(null);
		
		try {
			player.getBukkitPlayer().setHealth(ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName()).getPreviousHealth());
		} catch (PlayerException e) {
		}
		
		Player bp = player.getBukkitPlayer();
		
		bp.setHealth(player.getPreviousHealth());
		bp.setFoodLevel(player.getPreviousFoodLevel());
		bp.setExp(player.getPreviousExp());
		bp.setLevel(player.getPreviousLevel());
		bp.setGameMode(player.getPreviousGameMode());
		bp.getInventory().setContents(player.getPreviousContents());
		bp.teleport(player.getPreviousLocation());
	}
	
	public Set<ArenaPlayer> getPlayers() {
		
		return players;
	}
	
	
	public void start() throws GameException {
		
		if (arena.getType() == ArenaType.FFA || arena.getType() == ArenaType.TDM)
			throw new GameException("You cannot start an FFA or TDM game.");
	}
	
	public void end() {
		
		Set<ArenaPlayer> toRemove = new HashSet<ArenaPlayer>();
		
		for (ArenaPlayer player : players)
			toRemove.add(player);
		
		for (ArenaPlayer player : toRemove) {
			removePlayer(player);
			player.getBukkitPlayer().sendMessage(String.format(ChatColor.RED + "The game for the arena %s just ended.", arena.getName()));
		}
		
		arena.setGame(null);
	}
	
	public void spawnPlayer(ArenaPlayer player, Side side) {
		
		List<Spawn> spawns = null;
		
		if (arena.getType() == ArenaType.DUEL) {
			
			spawns = new ArrayList<Spawn>(arena.getSpawns(side));
			
			for (Spawn spawn : arena.getSpawns()) {
				if (spawn.isUsed())
					continue;
				
				player.spawn(spawn);
				spawn.setUsed(true);
				break;
			}	
			
		} else if (arena.getType() == ArenaType.FFA) {
			
			spawns = (List<Spawn>) arena.getSpawns(side);
			Collections.shuffle(spawns);
			
			player.spawn(spawns.get(0));
			
		} else if (arena.getType() == ArenaType.TDM) {
			
			spawns = (List<Spawn>) arena.getSpawns(side);
			Collections.shuffle(spawns);
			
			player.spawn(spawns.get(0));
		}
	}
}