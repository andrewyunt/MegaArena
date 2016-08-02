package com.andrewyunt.megaarena.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.GamePlayer;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class PlayerManager {

	private Map<String, GamePlayer> players = new HashMap<String, GamePlayer>();

	public GamePlayer createPlayer(String name) throws PlayerException {

		if (players.containsKey(name))
			throw new PlayerException(String.format("The player %s already exists.", name));

		GamePlayer player = new GamePlayer(name);

		players.put(name, player);

		return player;
	}

	public Collection<GamePlayer> getPlayers() {

		return players.values();
	}

	public GamePlayer getPlayer(String name) throws PlayerException {

		if (!players.containsKey(name))
			throw new PlayerException("The specified player does not exist.");

		return players.get(name);
	}
}