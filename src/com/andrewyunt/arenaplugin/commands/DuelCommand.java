package com.andrewyunt.arenaplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

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
		
		ArenaPlayer player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName());
		
		if (player.isInGame()) {
			sender.sendMessage(ChatColor.RED + "You are currently in a game and cannot duel.");
			return false;
		}
		
		ArenaPlayer targetPlayer = ArenaPlugin.getInstance().getPlayerManager().getPlayer(args[0]);
		
		if (targetPlayer == null) {
			sender.sendMessage(ChatColor.RED + "The target player is currently offline.");
			return false;
		}
		
		if (targetPlayer.isInGame()) {
			sender.sendMessage(String.format(ChatColor.RED + "The player %s is currently in a game and cannot duel.", targetPlayer.getName()));
			return false;
		}
		
		targetPlayer.setRequestingPlayer(player);
		
		Player targetBukkitPlayer = targetPlayer.getBukkitPlayer();
		
		targetBukkitPlayer.sendMessage(String.format(ChatColor.GOLD + "%s is currently requesting you to a duel.", player.getName()));
		targetBukkitPlayer.sendMessage(ChatColor.YELLOW + "/duelaccept");
		targetBukkitPlayer.sendMessage(ChatColor.YELLOW + "/dueldeny");
		
		return true;
	}
}