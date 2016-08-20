/**
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megaarena.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.exception.SideException;

/**
 * The class used to store game attributes, placed blocks, and players.
 * 
 * @author Andrew Yunt
 */
public class Game {
	
	private Arena arena;
	private Set<GamePlayer> players = new HashSet<GamePlayer>();
	private Set<Block> placedBlocks = new HashSet<Block>();
	private Set<GameSide> sides = new HashSet<GameSide>();
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

	public Game(Arena arena) {
		
		this.arena = arena;
		
		if (arena.getType() == Arena.Type.TDM) {
			sides.add(new GameSide(this, GameSide.Type.BLUE));
			sides.add(new GameSide(this, GameSide.Type.GREEN));
		} else
			sides.add(new GameSide(this, GameSide.Type.INDEPENDENT));
	}
	
	/**
	 * Gets the arena the game is running in.
	 * 
	 * @return
	 * 		The arena which the game is running in.
	 */
	public Arena getArena() {
		
		return arena;
	}
	
	/**
	 * Adds a player to the game and sets their attributes.
	 * 
	 * @param player
	 * 		The player to be added to the game.
	 */
	public void addPlayer(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		player.setPreviousGameMode(bp.getGameMode());
		
		Location prevLoc = bp.getLocation();
		prevLoc.setY(prevLoc.getY() + 1);
		player.setPreviousLocation(prevLoc);
	
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
		
		side.getTeam().addPlayer(bp);
		bp.setScoreboard(scoreboard);
		
		GameSide.Type sideType = side.getSideType();
		
		bp.sendMessage(String.format(ChatColor.GREEN + "You have joined the %s side.",
				ChatColor.AQUA + sideType.getName() + ChatColor.GREEN));
		
		spawnPlayer(player, sideType);
	}
	
	/**
	 * Gets a random spawn of the specified side and checks if it is used if
	 * the arena is a duel, then spawns them.
	 * 
	 * @param player
	 * 		The specified player to be added to the game.
	 * @param sideType
	 * 		The specified side to spawn the player in.
	 */
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
	
	/**
	 * Removes a specified player from the game.
	 * 
	 * @param player
	 * 		The specified player to be removed from the game.
	 * @throws PlayerException
	 * 		If the player is not in the game, PlayerException is thrown.
	 */
	public void removePlayer(GamePlayer player) throws PlayerException {
		
		if (!players.contains(player))
			throw new PlayerException("The specified player is not in the game.");
		
		Player bp = player.getBukkitPlayer();
		
		/* Remove player from team scoreboard and set scoreboard to null */
		player.getSide().getTeam().removePlayer(bp);
		bp.setScoreboard(player.getDefaultScoreboard());
		
		/* Remove player from players set then set the player's game and side to null */
		players.remove(player);
		player.setGame(null);
		player.setSide(null);
		
		/* Set player energy to 0 */
		player.setEnergy(0);
		
		/* Set player lobby health, food, experience and game mode */
		bp.setMaxHealth(20.0D);
		bp.setHealth(20.0D);
		bp.setFoodLevel(20);
		bp.setExp(0.0F);
		bp.setLevel(0);
		bp.setGameMode(player.getPreviousGameMode());
		
		/* Set player lobby inventory */
		player.updateHotBar();
		
		Location loc = player.getPreviousLocation();
		
		/* Teleport player to previous location */
		loc.setY(loc.getY() + 1);
		bp.teleport(loc);
	}
	
	/**
	 * Gets all players currently in the game.
	 * 
	 * @return
	 * 		A collection of players currently in the game.
	 */
	public Collection<GamePlayer> getPlayers() {
		
		return players;
	}
	
	/**
	 * Removes all players / placed blocks, and sets the arena game to null.
	 */
	public void end() {
		
		Set<GamePlayer> toRemove = new HashSet<GamePlayer>();
		
		for (GamePlayer player : players)
			toRemove.add(player);
		
		for (GamePlayer player : toRemove)
			try {
				removePlayer(player);
			} catch (PlayerException e) {
			}
		
		for (Block block : placedBlocks)
			block.setType(Material.AIR);
		
		if (arena.getType() == Arena.Type.DUEL)
			for (Spawn spawn : arena.getSpawns())
				spawn.setUsed(false);
		
		arena.setGame(null);
	}
	
	/**
	 * Adds a block to the placed blocks set.
	 * 
	 * @param block
	 * 		The block to be added to the placed blocks set.
	 */
	public void addPlacedBlock(Block block) {
		
		placedBlocks.add(block);
	}
	
	/**
	 * Removes a block from the placed blocks set.
	 * 
	 * @param block
	 * 		The block to be removed from the placed blocks set. 
	 */
	public void removePlacedBlock(Block block) {
		
		placedBlocks.remove(block);
	}
	
	/**
	 * Gets a Collection of placed blocks.
	 * 
	 * @return
	 * 		A collection of placed blocks.
	 */
	public Collection<Block> getPlacedBlocks() {
		
		return placedBlocks;
	}
	
	/**
	 * Gets the side instance for the side type in the game.
	 * 
	 * @param sideType
	 * 		The type of the side instance that you want to fetch.
	 * @return
	 * 		The instance of the side for the specified side type.
	 * @throws SideException
	 * 		If there is no side in the game with the specified type,
	 * 		throw SideException.
	 */
	public GameSide getSide(GameSide.Type sideType) throws SideException {
		
		for (GameSide side : sides)
			if (side.getSideType() == sideType)
				return side;
		
		throw new SideException("The side of the specified type does not exist.");
	}
	
	/**
	 * Gets the scoreboard for the game.
	 * 
	 * @return
	 * 		The game's scoreboard object.
	 */
	public Scoreboard getScoreboard() {
		
		return scoreboard;
	}
}