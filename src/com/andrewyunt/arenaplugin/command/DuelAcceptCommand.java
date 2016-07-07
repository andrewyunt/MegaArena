package com.andrewyunt.arenaplugin.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

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
		
		ArenaPlayer player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName());
		
		if (!(player.hasDuelRequest()))
			return false;
		
		if (player.isInGame()) {
			sender.sendMessage(ChatColor.RED + "You are currently in a game and cannot accept a duel request.");
			return false;
		}
		
		ArenaPlayer requestingPlayer = player.getRequestingPlayer();
	
		player.setRequestingPlayer(null);
		
		if (requestingPlayer.isInGame()) {
			sender.sendMessage(String.format(ChatColor.RED + "The player %s is currently in a game and cannot duel.", requestingPlayer.getName()));
			return false;
		}
		
		Arena arena = null;
		
		for (Arena duelArena : ArenaPlugin.getInstance().getArenaManager().getArenas(ArenaType.DUEL)) {
			if (!duelArena.isInUse()) {
				arena = duelArena;
				break;
			}
		}
		
		if (arena == null) {
			sender.sendMessage(ChatColor.RED + "There are currently no arenas available for a duel.");
			return false;
		}
		
		Set<ArenaPlayer> players = new HashSet<ArenaPlayer>();
		
		players.add(player);
		players.add(requestingPlayer);
		
		ArenaPlugin.getInstance().getGameManager().createGame(arena, players);
		
		return true;
	}
}