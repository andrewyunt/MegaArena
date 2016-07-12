package com.andrewyunt.arenaplugin.managers;

import java.util.HashSet;
import java.util.Set;

import com.andrewyunt.arenaplugin.exception.GameException;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Game;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class GameManager {
	
	public Set<Game> games = new HashSet<Game>();
	
	public Game createGame(Arena arena, Set<ArenaPlayer> players) {
		
		Game game = new Game(arena, players);
		
		games.add(game);
		
		if (arena.getType() == ArenaType.TDM || arena.getType() == ArenaType.FFA)
			game.setActive(true);
		else if (arena.getType() == ArenaType.DUEL) {
			
		}
			
		
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