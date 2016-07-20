package com.andrewyunt.arenaplugin.managers;

import java.util.HashSet;
import java.util.Set;

import com.andrewyunt.arenaplugin.exception.GameException;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Game;
import com.andrewyunt.arenaplugin.objects.Game.Side;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class GameManager {
	
	public Set<Game> games = new HashSet<Game>();
	
	public Game createGame(Arena arena, Set<ArenaPlayer> players) throws GameException {
		
		if (arena.getType() == ArenaType.FFA)
			if (arena.getSpawns().size() < 2) {
				throw new GameException(String.format("The match for the arena %s was not able to start because the minimum number"
						+ " of INDEPENDENT spawns were not defined.", arena.getName()));
		} else
			if (arena.getSpawns(Side.GREEN).size() < 1 || arena.getSpawns(Side.BLUE).size() < 1)
				throw new GameException(String.format("The TDM match for the arena %s was not able to start because the"
						+ " minimum number of spawns for each team were not defined.", arena.getName()));
			
		Game game = new Game(arena, players);
		
		games.add(game);
		arena.setGame(game);
		
		return game;
	}
	
	public void deleteGame(Game game, String msg) throws GameException {
		
		if (game.getArena().getType() == ArenaType.TDM || game.getArena().getType() == ArenaType.FFA)
			throw new GameException("FFA and TDM games cannot be deleted.");
		
		games.remove(game);
		
		for (ArenaPlayer player : game.getPlayers())
			player.getBukkitPlayer().sendMessage(msg);
	}
	
	public Set<Game> getGames() {
		
		return games;
	}
}