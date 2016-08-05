/**
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megaarena.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.GamePlayer;

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
		
		if (!sender.hasPermission("megaarena.duel")) {
			sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
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
		
		GamePlayer player = null;
		
		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName());
		} catch (PlayerException e) {
		}
		
		if (player.isInGame()) {
			sender.sendMessage(ChatColor.RED + "You are currently in a game and cannot duel.");
			return false;
		}
		
		GamePlayer targetPlayer = null;
		
		try {
			targetPlayer = MegaArena.getInstance().getPlayerManager().getPlayer(args[0]);
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
		
		targetBukkitPlayer.sendMessage(String.format(ChatColor.AQUA + "%s is currently requesting you to a duel.",
				player.getName() + ChatColor.GREEN));
		targetBukkitPlayer.sendMessage(ChatColor.AQUA + "/duelaccept");
		targetBukkitPlayer.sendMessage(ChatColor.AQUA + "/dueldeny");
		
		sender.sendMessage(String.format(ChatColor.GREEN + "You have requested %s to a duel.",
				ChatColor.AQUA + targetBukkitPlayer.getName() + ChatColor.GREEN));
		
		return true;
	}
}