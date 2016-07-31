package com.andrewyunt.arenaplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class DuelDenyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("dueldeny"))
			return false;
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		ArenaPlayer player = null;
		
		try {
			player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName());
		} catch (PlayerException e) {
		}
		
		player.setRequestingPlayer(null);
		
		ArenaPlayer requestingPlayer = player.getRequestingPlayer();
		
		player.getBukkitPlayer().sendMessage(ChatColor.GREEN + String.format("You denied %s's request to a duel.",
				ChatColor.AQUA + requestingPlayer.getName() + ChatColor.GREEN));
		
		requestingPlayer.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s denied your request to a duel.",
				player.getName() + ChatColor.GREEN));
		
		return true;
	}
}