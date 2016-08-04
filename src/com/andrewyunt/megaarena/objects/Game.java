package com.andrewyunt.megaarena.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.objects.Arena.ArenaType;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Game {
	
	public enum Side {
		
		BLUE("Blue", ChatColor.BLUE),
		GREEN("Green", ChatColor.DARK_GREEN),
		INDEPENDENT("Independent", ChatColor.DARK_RED);
		
		private String name;
		private ChatColor nameColor;
		
		Side(String name, ChatColor nameColor) {
			
			this.name = name;
			this.nameColor = nameColor;
		}
		
		public String getName() {
			
			return name;
		}
		
		public ChatColor getNameColor() {
			
			return nameColor;
		}
	}
	
	private Set<GamePlayer> players = new HashSet<GamePlayer>();
	private Set<Block> placedBlocks = new HashSet<Block>();
	private Arena arena;

	public Game(Arena arena) {
		
		this.arena = arena;
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public void addPlayer(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		player.setPreviousGameMode(bp.getGameMode());
		player.setPreviousLocation(bp.getLocation());
	
		Side side = null;
		
		if (arena.getType() == ArenaType.DUEL || arena.getType() == ArenaType.FFA)
			side = Side.INDEPENDENT;
		else if (arena.getType() == ArenaType.TDM) {
			int bluePlayers = 0;
			int greenPlayers = 0;
			
			for (GamePlayer sidePlayer : getPlayers())
				if (sidePlayer.getSide() == Side.BLUE)
					bluePlayers++;
				else
					greenPlayers++;
			
			if (bluePlayers == greenPlayers) {
				double rand = Math.random();
				
				if (rand <= .5)
					side = Side.BLUE;
				else
					side = Side.GREEN;
			} else if (bluePlayers < greenPlayers)
				side = Side.BLUE;
			else
				side = Side.GREEN;
		}
		
		players.add(player);
		player.setGame(this);
		player.setSide(side);
		
		spawnPlayer(player, side);
	}
	
	public void removePlayer(GamePlayer player) {
		
		/* Remove player from players set and set the player's game to null */
		players.remove(player);
		player.setGame(null);
	
		/* Set player energy to 0 */
		player.setEnergy(0);
		
		Player bp = player.getBukkitPlayer();
		
		/* Set player lobby health, food, experience and game mode */
		bp.setMaxHealth(20.0D);
		bp.setHealth(20.0D);
		bp.setFoodLevel(20);
		bp.setExp(0.0F);
		bp.setLevel(0);
		bp.setGameMode(player.getPreviousGameMode());
		
		/* Set player lobby inventory */
		player.updateHotBar();
		
		/* Teleport player to previous location */
		bp.teleport(player.getPreviousLocation());
	}
	
	public Set<GamePlayer> getPlayers() {
		
		return players;
	}
	
	public void start() throws GameException {
		
		if (arena.getType() == ArenaType.FFA || arena.getType() == ArenaType.TDM)
			throw new GameException("You cannot start an FFA or TDM game.");
	}
	
	public void end() {
		
		Set<GamePlayer> toRemove = new HashSet<GamePlayer>();
		
		for (GamePlayer player : players)
			toRemove.add(player);
		
		for (GamePlayer player : toRemove) {
			removePlayer(player);
			player.getBukkitPlayer().sendMessage(String.format(ChatColor.RED + "The game for the arena %s just ended.", arena.getName()));
		}
		
		for (Block block : placedBlocks)
			block.setType(Material.AIR);
		
		if (arena.getType() == ArenaType.DUEL)
			for (Spawn spawn : arena.getSpawns())
				spawn.setUsed(false);
		
		arena.setGame(null);
	}
	
	public void spawnPlayer(GamePlayer player, Side side) {
		
		List<Spawn> spawns = new ArrayList<Spawn>(arena.getSpawns(side));
		
		if (arena.getType() == ArenaType.DUEL) {
			
			for (Spawn spawn : arena.getSpawns()) {
				if (spawn.isUsed())
					continue;
				
				player.spawn(spawn);
				spawn.setUsed(true);
				
				break;
			}
			
		} else if (arena.getType() == ArenaType.FFA) {
			
			player.setHasFallen(false);
			Collections.shuffle(spawns);
			player.spawn(spawns.get(0));
			
		} else if (arena.getType() == ArenaType.TDM) {
			
			player.setHasFallen(false);
			Collections.shuffle(spawns);
			player.spawn(spawns.get(0));
		}
	}
	
	public void addPlacedBlock(Block block) {
		
		placedBlocks.add(block);
	}
	
	public void removePlacedBlock(Block block) {
		
		placedBlocks.remove(block);
	}
	
	public Set<Block> getPlacedBlocks() {
		
		return placedBlocks;
	}
}