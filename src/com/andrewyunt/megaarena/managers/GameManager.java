package com.andrewyunt.megaarena.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.Arena.ArenaType;
import com.andrewyunt.megaarena.objects.Game.Side;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class GameManager {

	public Set<Game> games = new HashSet<Game>();

	public Game createGame(Arena arena) throws GameException {

		if (arena.getType() == ArenaType.DUEL)
			if (arena.getSpawns().size() < 2)
				throw new GameException(ChatColor.GREEN + String.format(
						"The match for the arena %s was not able to start because the minimum number"
								+ " of INDEPENDENT spawns were not defined.",
						ChatColor.AQUA + arena.getName() + ChatColor.GREEN));
			else if (arena.getType() == ArenaType.TDM)
				if (arena.getSpawns(Side.GREEN).size() < 1 || arena.getSpawns(Side.BLUE).size() < 1)
					throw new GameException(ChatColor.GREEN + String.format(
							"The TDM match for the arena %s was not able to start because the"
									+ " minimum number of spawns for each team were not defined.",
							ChatColor.AQUA + arena.getName() + ChatColor.GREEN));

		Game game = new Game(arena);

		games.add(game);
		arena.setGame(game);

		return game;
	}

	public void deleteGame(Game game, String msg) {

		games.remove(game);
		game.end();

		for (GamePlayer player : game.getPlayers())
			player.getBukkitPlayer().sendMessage(msg);
	}

	public Set<Game> getGames() {

		return games;
	}

	public Set<Game> getGames(ArenaType type) {

		Set<Game> games = new HashSet<Game>();

		for (Game game : this.games)
			if (game.getArena().getType() == type)
				games.add(game);

		return games;
	}

	public void matchMake(GamePlayer player, ArenaType type) throws GameException {

		if (type == ArenaType.DUEL)
			throw new GameException("Matchmaking is not available for duels.");

		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
			@Override
			public void run() {
				Player bp = player.getBukkitPlayer();

				if (player.isInGame()) {
					bp.sendMessage(ChatColor.RED + "You are already in a game.");
					return;
				}

				if (!(player.hasSelectedClass())) {
					bp.sendMessage(ChatColor.RED + "You must select a class before entering a game.");
					return;
				}

				List<Game> games = new ArrayList<Game>(getGames(type));

				if (games.size() < 1) {
					bp.sendMessage(String.format(ChatColor.RED + "There are not active %s games at the moment.",
							type.toString()));
					return;
				}

				Collections.shuffle(games);
				Game game = games.get(0);

				game.addPlayer(player);
			}
		}, 1L);
	}
}