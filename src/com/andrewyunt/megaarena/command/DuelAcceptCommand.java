package com.andrewyunt.megaarena.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.GamePlayer;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class DuelAcceptCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("duelaccept"))
			return false;
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (!sender.hasPermission("megaarena.duelaccept")) {
			sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			return false;
		}
		
		GamePlayer player = null;
		
		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName());
		} catch (PlayerException e) {
		}
		
		if (!(player.hasDuelRequest()))
			return false;
		
		if (player.isInGame()) {
			sender.sendMessage(ChatColor.RED + "You are currently in a game and cannot accept a duel request.");
			return false;
		}
		
		GamePlayer requestingPlayer = player.getRequestingPlayer();
	
		player.setRequestingPlayer(null);
		
		if (requestingPlayer.isInGame()) {
			sender.sendMessage(String.format(ChatColor.RED + "The player %s is currently in a game and cannot duel.", requestingPlayer.getName()));
			return false;
		}
		
		if (!player.hasSelectedClass()) {
			player.getBukkitPlayer().sendMessage(ChatColor.RED + "You must select a class before accepting a duel.");
			requestingPlayer.getBukkitPlayer().sendMessage(ChatColor.RED + "The player you requested to duel must select a class before accepting to duel.");
			return false;
		}
		
		Arena arena = null;
		
		for (Arena duelArena : MegaArena.getInstance().getArenaManager().getArenas(Arena.Type.DUEL)) {
			if (duelArena.isEdit())
				continue;
			
			if (!duelArena.isInUse()) {
				if (duelArena.getSpawns().size() < 2)
					continue;
				arena = duelArena;
				break;
			}
		}
		
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "There are currently no arenas available for a duel.");
			return false;
		}
		
		Game game = null;
		
		try {
			game = MegaArena.getInstance().getGameManager().createGame(arena);
		} catch (GameException e) {
		}
		
		game.addPlayer(player);
		game.addPlayer(requestingPlayer);

		return true;
	}
}