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
import com.andrewyunt.megaarena.exception.SideException;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Game {
	
	private Arena arena;
	private Set<GamePlayer> players = new HashSet<GamePlayer>();
	private Set<Block> placedBlocks = new HashSet<Block>();
	private Set<GameSide> sides = new HashSet<GameSide>();

	public Game(Arena arena) {
		
		this.arena = arena;
		
		if (arena.getType() == Arena.Type.TDM) {
			sides.add(new GameSide(this, GameSide.Type.BLUE));
			sides.add(new GameSide(this, GameSide.Type.GREEN));
		} else
			sides.add(new GameSide(this, GameSide.Type.INDEPENDENT));
	}
	
	public Arena getArena() {
		
		return arena;
	}
	
	public void addPlayer(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		player.setPreviousGameMode(bp.getGameMode());
		player.setPreviousLocation(bp.getLocation());
	
		GameSide side = null;
		
		try {
			if (arena.getType() == Arena.Type.DUEL || arena.getType() == Arena.Type.FFA)
				side = getSide(GameSide.Type.INDEPENDENT);
			else if (arena.getType() == Arena.Type.TDM) {
				int bluePlayers = 0;
				int greenPlayers = 0;
				
				for (GamePlayer sidePlayer : getPlayers())
					if (sidePlayer.getSide() == getSide(GameSide.Type.BLUE))
						bluePlayers++;
					else
						greenPlayers++;
				
				if (bluePlayers == greenPlayers) {
					double rand = Math.random();
					
					if (rand <= .5)
						side = getSide(GameSide.Type.BLUE);
					else
						side = getSide(GameSide.Type.GREEN);
				} else if (bluePlayers < greenPlayers)
					side = getSide(GameSide.Type.BLUE);
				else
					side = getSide(GameSide.Type.GREEN);
			}
		} catch (SideException e) {
		}
		
		players.add(player);
		player.setGame(this);
		player.setSide(side);
		
		bp.sendMessage(String.format(ChatColor.GREEN + "You have joined the %s side.",
				ChatColor.AQUA + side.getType().getName() + ChatColor.GREEN));
		
		spawnPlayer(player, side.getType());
	}
	
	public void spawnPlayer(GamePlayer player, GameSide.Type sideType) {
		
		List<Spawn> spawns = new ArrayList<Spawn>(arena.getSpawns(sideType));
		
		if (arena.getType() == Arena.Type.DUEL) {
			
			for (Spawn spawn : arena.getSpawns()) {
				if (spawn.isUsed())
					continue;
				
				player.spawn(spawn);
				spawn.setUsed(true);
				
				break;
			}
			
		} else if (arena.getType() == Arena.Type.FFA) {
			
			player.setHasFallen(false);
			Collections.shuffle(spawns);
			player.spawn(spawns.get(0));
			
		} else if (arena.getType() == Arena.Type.TDM) {
			
			player.setHasFallen(false);
			Collections.shuffle(spawns);
			player.spawn(spawns.get(0));
		}
	}
	
	public void removePlayer(GamePlayer player) {
		
		/* Remove player from players set then set the player's game and side to null */
		players.remove(player);
		player.setGame(null);
		player.setSide(null);
	
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
		
		if (arena.getType() == Arena.Type.FFA || arena.getType() == Arena.Type.TDM)
			throw new GameException("You cannot start an FFA or TDM game.");
	}
	
	public void end() {
		
		Set<GamePlayer> toRemove = new HashSet<GamePlayer>();
		
		for (GamePlayer player : players)
			toRemove.add(player);
		
		for (GamePlayer player : toRemove)
			removePlayer(player);
		
		for (Block block : placedBlocks)
			block.setType(Material.AIR);
		
		if (arena.getType() == Arena.Type.DUEL)
			for (Spawn spawn : arena.getSpawns())
				spawn.setUsed(false);
		
		arena.setGame(null);
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
	
	public GameSide getSide(GameSide.Type sideType) throws SideException {
		
		for (GameSide side : sides)
			if (side.getType() == sideType)
				return side;
		
		throw new SideException("The side of the specified type does not exist.");
	}
}