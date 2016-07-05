package com.andrewyunt.arenaplugin.managers;

import java.util.HashSet;
import java.util.Set;

import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Game;

public class GameManager {
	
	public Set<Game> games = new HashSet<Game>();
	
	public Game createGame(Arena arena, Set<ArenaPlayer> players) {
		
		Game game = new Game(arena, players);
		
		games.add(game);
		
		return game;
	}
	
	public Set<Game> getGames() {
		
		return games;
	}
}