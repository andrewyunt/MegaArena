package com.andrewyunt.arenaplugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class DuelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("duel"))
			return false;
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (!(args.length > 0)) {
			sender.sendMessage(ChatColor.RED + "Usage: /duel [player]");
			return false;
		}
		
		if (args[0].equalsIgnoreCase(sender.getName())) {
			sender.sendMessage(ChatColor.RED + "You may not request to duel yourself.");
			return false;
		}
		
		ArenaPlayer player = null;
		
		try {
			player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName());
		} catch (PlayerException e) {
		}
		
		if (player.isInGame()) {
			sender.sendMessage(ChatColor.RED + "You are currently in a game and cannot duel.");
			return false;
		}
		
		ArenaPlayer targetPlayer = null;
		
		try {
			targetPlayer = ArenaPlugin.getInstance().getPlayerManager().getPlayer(args[0]);
		} catch (PlayerException e) {
			sender.sendMessage(ChatColor.RED + "The target player is currently offline.");
			return false;
		}
		
		if (targetPlayer.isInGame()) {
			sender.sendMessage(String.format(ChatColor.RED + "The player %s is currently in a game and cannot duel.", targetPlayer.getName()));
			return false;
		}
		
		if (!player.hasSelectedClass()) {
			player.getBukkitPlayer().sendMessage(ChatColor.RED + "You must select a class before requesting to duel a player.");
			return false;
		}
		
		targetPlayer.setRequestingPlayer(player);
		
		Player targetBukkitPlayer = targetPlayer.getBukkitPlayer();
		
		targetBukkitPlayer.sendMessage(String.format(ChatColor.GOLD + "%s is currently requesting you to a duel.", player.getName()));
		targetBukkitPlayer.sendMessage(ChatColor.YELLOW + "/duelaccept");
		targetBukkitPlayer.sendMessage(ChatColor.YELLOW + "/dueldeny");
		
		sender.sendMessage(String.format(ChatColor.GOLD + "You have requested %s to a duel.", targetBukkitPlayer.getName()));
		
		return true;
	}
}