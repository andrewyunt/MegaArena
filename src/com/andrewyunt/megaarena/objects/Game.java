/*
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

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.exception.SideException;
import com.andrewyunt.megaarena.utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class used to store game attributes, placed blocks, and players.
 * 
 * @author Andrew Yunt
 */
public class Game {
	
	private final Arena arena;
	private final Set<GamePlayer> players = new HashSet<>();
	private final Set<Block> placedBlocks = new HashSet<>();
	private final Set<GameSide> sides = new HashSet<>();
	
	private int tournamentCountdownID, tournamentCountdownTime = 10;
	private boolean started = false;
	
	public Game(Arena arena) {
		
		this.arena = arena;
		
		if (!arena.isTournament()) {
            started = true;
        }
		
		if (arena.getType() == Arena.Type.TDM) {
			sides.add(new GameSide(this, GameSide.Type.BLUE));
			sides.add(new GameSide(this, GameSide.Type.GREEN));
		} else {
            sides.add(new GameSide(this, GameSide.Type.SOLO));
        }
		
		if (arena.getType() != Arena.Type.TDM) {
            return;
        }
		
		// Repeating task used for automatic team balancing in TDM
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), () -> {
			GameSide blue = null;
			GameSide green = null;
			
			try {
				blue = getSide(GameSide.Type.BLUE);
				green = getSide(GameSide.Type.GREEN);
			} catch (SideException e) {
				e.printStackTrace();
			}
			
			int bluePlayers = getPlayers(blue).size();
			int greenPlayers = getPlayers(green).size();
			int totalPlayers = bluePlayers + greenPlayers; 
			
			GameSide morePlayers = null;
			
			if (bluePlayers > greenPlayers) {
                morePlayers = blue;
            } else if (greenPlayers > bluePlayers) {
                morePlayers = green;
            } else {
                return;
            }
			
			int balanceGap = 0;
			
			if (totalPlayers > 20 && totalPlayers < 40) {
                balanceGap = 5;
            } else if (totalPlayers > 40) {
                balanceGap = 10;
            } else {
                return;
            }
			
			int playerDiff = 0;
			
			if (morePlayers == blue) {
				if (bluePlayers - greenPlayers < balanceGap) {
                    return;
                }
				
				playerDiff = bluePlayers - greenPlayers;
			} else if (morePlayers == green) {
				if (greenPlayers - bluePlayers < balanceGap) {
                    return;
                }
				
				playerDiff = greenPlayers - bluePlayers;
			}
			
			List<GamePlayer> morePlayersList = new ArrayList<>(getPlayers(morePlayers));
			
			while (playerDiff > 0) {
				Random random = new Random();
				GamePlayer moved = morePlayersList.get(random.nextInt(morePlayersList.size()));
				
				morePlayersList.remove(moved);
				
				try {
					removePlayer(moved);
					addPlayer(moved, true);
				} catch (PlayerException e) {
					e.printStackTrace();
				}
			}
		} , 0L, 6000L);
	}

	/**
	 * @return The arena which the game is running in.
	 */
	public Arena getArena() {

		return arena;
	}

	/**
	 * Adds a player to the game and sets their attributes.
	 * 
	 * @param player
	 * 		The player to be added to the game.
	 * @param teamBalance
	 * 		Set this value to true if the player was added due to a team balance.
	 */
	public void addPlayer(GamePlayer player, boolean teamBalance) {
		
		player.setPreviousGameMode(player.getBukkitPlayer().getGameMode());
		
		GameSide side = null;
		
		try {
			if (arena.getType() == Arena.Type.DUEL || arena.getType() == Arena.Type.FFA) {
                side = getSide(GameSide.Type.SOLO);
            } else if (arena.getType() == Arena.Type.TDM) {
				int bluePlayers = 0;
				int greenPlayers = 0;
				
				for (GamePlayer sidePlayer : getPlayers()) {
                    if (sidePlayer.getSide() == getSide(GameSide.Type.BLUE)) {
                        bluePlayers++;
                    } else {
                        greenPlayers++;
                    }
                }
				
				if (bluePlayers == greenPlayers) {
					double rand = Math.random();
					
					if (rand <= .5) {
                        side = getSide(GameSide.Type.BLUE);
                    } else {
                        side = getSide(GameSide.Type.GREEN);
                    }
				} else if (bluePlayers < greenPlayers) {
                    side = getSide(GameSide.Type.BLUE);
                } else {
                    side = getSide(GameSide.Type.GREEN);
                }
			}
		} catch (SideException e) {
			e.printStackTrace();
		}
		
		players.add(player);
		player.setGame(this);
		player.setSide(side);
		
		boolean spawn = false;
		
		if (arena.isTournament()) {
			if (players.size() > 12) {
				return;
			} else if (players.size() == 12) {
				for (GamePlayer toSpawn : players) {
                    spawnPlayer(toSpawn, toSpawn.getSide().getSideType(), false);
                }
				
				runTournamentCountdown();
			}
			
			try {
				player.getBukkitPlayer().teleport(arena.getQueueLocation());
			} catch (NullPointerException e) {
				player.getBukkitPlayer().sendMessage(String.format(
						Utils.getFormattedMessage("messages.no-queue-location"),
						arena.getName()));
				return;
			}
			
			if (tournamentCountdownTime <= 10) {
                spawn = true;
            }
			
			player.updateScoreboard();
			
			if (!started) {
                for (GamePlayer toSend : players) {
                    toSend.getBukkitPlayer().sendMessage(String.format(
                            Utils.getFormattedMessage("messages.tournament-join"),
                            player.getBukkitPlayer().getDisplayName(),
                            players.size()));
                }
            }
		} else {
            spawn = true;
        }
		
		if (spawn) {
            try {
                spawnPlayer(player, side.getSideType(), teamBalance);
            } catch (IndexOutOfBoundsException e) {
                player.getBukkitPlayer().sendMessage(String.format(
                        Utils.getFormattedMessage("messages.no-spawn-points"),
                        arena.getName()));
                return;
            }
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
		
		if (!players.contains(player)) {
            throw new PlayerException("The specified player is not in the game.");
        }
		
		Player bp = player.getBukkitPlayer();
		
		// Check players in-game
		checkPlayers();
		
		// Remove player from players set and then set the player's game to null
		players.remove(player);
		player.setGame(null);
		
		if (arena.isTournament()) {
            if (!started) {
                for (GamePlayer toSend : players) {
                    toSend.getBukkitPlayer().sendMessage(String.format(
                            Utils.getFormattedMessage("messages.tournament-leave"),
                            player.getBukkitPlayer().getDisplayName()));
                }
            } else {
                checkPlayers();
            }
        }
		
		// Remove player from scoreboard teams
		for (GamePlayer toRemove : players) {
            toRemove.getDisplayBoard().getScoreboard().getTeam(player.getSide().getSideType().getName())
                    .removePlayer(player.getBukkitPlayer());
        }
		
		// Update teams to remove player
		try {
			for (GameSide side : sides) {
				Team team = player.getDisplayBoard().getScoreboard().getTeam(side.getSideType().getName());
				
				team.getPlayers().forEach(team::removePlayer);
			}
		} catch (NullPointerException e) {
			// the player hasn't spawned in yet, but they are in-game so do nothing
		}
		
		// Set the player's side to null
		player.setSide(null);
		
		// Update player's scoreboard
		player.updateScoreboard();
		
		// Set player's energy to 0
		player.setEnergy(0);
		
		// Set player's killstreak to 0
		player.setKillStreak(0);
		
		// Set last damager to null
		player.setLastDamager(null);
		
		// Remove player's potion effects
		bp.getActivePotionEffects().clear();
		
		// Set player's lobby health, food, experience and game mode
		bp.setMaxHealth(20.0D);
		bp.setHealth(20.0D);
		bp.setFoodLevel(20);
		bp.setExp(0.0F);
		bp.setLevel(0);
		bp.setGameMode(player.getPreviousGameMode());
		
		// Set player's lobby inventory
		player.updateHotbar();
		
		// Teleport the player to the spawn location
		Location loc = bp.getWorld().getSpawnLocation().clone();
		
		Chunk chunk = loc.getChunk();
		
		if (!chunk.isLoaded()) {
            chunk.load();
        }
		
		loc.setY(loc.getY() + 1);
		
		bp.teleport(loc, TeleportCause.COMMAND);
	}

	/**
	 * Gets a random spawn of the specified side and checks if it is used if the
	 * arena is a duel, then spawns them.
	 * 
	 * @param player
	 * 		The specified player to be added to the game.
	 * @param sideType
	 * 		The specified side to spawn the player in.
	 * @param teamBalance
	 * 		Set this value to true if the player was spawned due to a team balance.
	 */
	public void spawnPlayer(GamePlayer player, GameSide.Type sideType, boolean teamBalance)
			throws IndexOutOfBoundsException {
		
		List<Spawn> spawns = new ArrayList<>(arena.getSpawns(sideType));
		
		if (arena.getType() == Arena.Type.DUEL) {
			for (Spawn spawn : arena.getSpawns()) {
				if (spawn.isUsed()) {
                    continue;
                }
				
				player.spawn(spawn);
				spawn.setUsed(true);
				
				break;
			}
		} else if (arena.getType() == Arena.Type.FFA) {
			player.setFallen(false);
			Collections.shuffle(spawns);
			player.spawn(spawns.get(0));
		} else if (arena.getType() == Arena.Type.TDM) {
			player.setFallen(false);
			Collections.shuffle(spawns);
			player.spawn(spawns.get(0));
		}
		
		for (GamePlayer updatePlayer : players) {
			Scoreboard scoreboard = updatePlayer.getDisplayBoard().getScoreboard();
			
			for (GameSide addSide : sides) {
				Team team = scoreboard.getTeam(addSide.getSideType().getName());
				
				if (team == null) {
                    team = scoreboard.registerNewTeam(addSide.getSideType().getName());
                }
				
				team.setPrefix(addSide.getSideType().getNameColor() + "");
				
				for (GamePlayer addPlayer : getPlayers(addSide)) {
                    team.addPlayer(addPlayer.getBukkitPlayer());
                }
			}
		}
		
		player.updateScoreboard();
		
		Player bp = player.getBukkitPlayer();
		
		bp.sendMessage(Utils.getFormattedMessage("messages.join-tip"));
		bp.sendMessage(Utils.getFormattedMessage("messages.cross-teaming-not-allowed"));
		bp.sendMessage("");
		
		if (teamBalance) {
            bp.sendMessage(String.format(
                    Utils.getFormattedMessage("messages.side-joined-balance"),
                    sideType.getName()));
        } else {
            bp.sendMessage(String.format(
                    Utils.getFormattedMessage("messages.side-joined"),
                    sideType.getName()));
        }
	}

	/**
	 * Removes all players / placed blocks, and sets the arena game to null.
	 */
	public void end() {
	
		Set<GamePlayer> toRemovePlayers = new HashSet<>();
		toRemovePlayers.addAll(players);
		for (GamePlayer player : toRemovePlayers) {
            try {
                removePlayer(player);
            } catch (PlayerException e) {
                e.printStackTrace();
            }
        }
		
		Set<Block> toRemoveBlocks = new HashSet<>();
		toRemoveBlocks.addAll(placedBlocks);
		toRemoveBlocks.forEach(this::removePlacedBlock);
		
		if (arena.getType() == Arena.Type.DUEL) {
            for (Spawn spawn : arena.getSpawns()) {
                spawn.setUsed(false);
            }
        }
		
		arena.setGame(null);
		
		if (arena.isTournament()) {
			for (Block block : placedBlocks) {
                block.setType(Material.AIR);
            }
			
			MegaArena.getInstance().runNextTournamentCountdown();
		}
	}
	
	public void runTournamentCountdown() {
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		tournamentCountdownID = scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), () -> {

            tournamentCountdownTime--;

            String message = null;

            switch (tournamentCountdownTime) {
            case 9: case 8: case 7: case 6:
            case 5: case 4: case 3: case 2:
            case 1:
                message = String.format(
                        Utils.getFormattedMessage("messages.tournament-start-x-seconds"),
                        String.valueOf(tournamentCountdownTime));
                break;
            case 0:
                started = true;
                scheduler.cancelTask(tournamentCountdownID);
                message = ChatColor.GREEN + "The tournament has started!";
                break;
            }

            for (GamePlayer player : players) {
if (message != null) {
player.getBukkitPlayer().sendMessage(message);
}
}
        }, 0L, 20L);
	}
	
	public void checkPlayers() {
		
		boolean won = false;
		
		if (arena.isTournament() && started) {
            if (arena.getType() == Arena.Type.FFA && players.size() == 1) {
                won = true;
            } else if (arena.getType() == Arena.Type.TDM) {
                List<GameSide> sidesWithPlayers = new ArrayList<>();

                for (GameSide side : sides) {
                    if (getPlayers(side).size() > 0) {
                        sidesWithPlayers.add(side);
                    }
                }

                if (sidesWithPlayers.size() == 1) {
                    won = true;
                }
            }
        }
		
		if (won) {
            for (GamePlayer player : players) {
                player.addCoins(MegaArena.getInstance().getConfig().getInt("win-coins"));
                player.getBukkitPlayer().sendMessage(String.format(
                        Utils.getFormattedMessage("messages.tournament-win-coins-received"),
                        String.valueOf(30)));
            }
        }
	}
	
	/**
	 * Gets all players currently in the game.
	 * 
	 * @returns A set of players currently in the game.
	 */
	public Set<GamePlayer> getPlayers() {

		return players;
	}

	/**
	 * Gets all players currently in the game from the specified side.
	 * 
	 * @return A set of players currently in the game on the specified side.
	 */
	public Set<GamePlayer> getPlayers(GameSide side) {
		
		Set<GamePlayer> players = new HashSet<>(this.players);
		Set<GamePlayer> toRemove = players.stream().filter(player -> player.getSide() != side)
				.collect(Collectors.toSet());
		
		toRemove.forEach(players::remove);
		
		return players;
	}
	
	/**
	 * Adds a block to the placed blocks set.
	 * 
	 * @param block
	 * 		The block to be added to the placed blocks set.
	 */
	public void addPlacedBlock(Block block) {
		
		placedBlocks.add(block);
		
		if (!arena.isTournament()) {
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> {
				if (!block.getChunk().isLoaded()) {
                    block.getChunk().load();
                }
				
				removePlacedBlock(block);
			}, 200L);
		}
	}

	/**
	 * Removes a block from the placed blocks set.
	 * 
	 * @param block
	 * 		The block to be removed from the placed blocks set.
	 */
	public void removePlacedBlock(Block block) {
		
		placedBlocks.remove(block);
		
		block.setType(Material.AIR);
	}

	/**
	 * Gets a Collection of placed blocks.
	 * 
	 * @return A collection of placed blocks.
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
	 * 		If there is no side in the game with the specified type, throw SideException.
	 */
	public GameSide getSide(GameSide.Type sideType) throws SideException {

		for (GameSide side : sides) {
            if (side.getSideType() == sideType) {
                return side;
            }
        }

		throw new SideException("The side of the specified type does not exist.");
	}
	
	public int getTournamentCountdownTime() {
		
		return tournamentCountdownTime;
	}
	
	public boolean hasStarted() {
		
		return started;
	}
}