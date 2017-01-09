/*
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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.GamePlayer;

import net.md_5.bungee.api.ChatColor;

/**
 * The dueldeny command class which is used as a Bukkit CommandExecutor
 * to deny duel requests
 * 
 * @author Andrew Yunt
 */
public class DuelDenyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (!sender.hasPermission("megaarena.dueldeny")) {
			sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			return false;
		}
		
		GamePlayer player = null;
		
		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName());
		} catch (PlayerException e) {
		}
		
		MegaArena plugin = MegaArena.getInstance();
		GamePlayer requestingPlayer = null;
		
		try {
			requestingPlayer = args.length > 0 
					? plugin.getPlayerManager().getPlayer(plugin.getServer().getPlayer(args[0]).getName())
					: player.getLastRequestingPlayer();
		} catch (PlayerException e) {
			sender.sendMessage(ChatColor.RED + "The specified player does not exist.");
			return false;
		} catch (NullPointerException e) {
			sender.sendMessage(ChatColor.RED + "You currently have no active duel requests.");
			return false;
		}
		
		if (!player.getRequestingPlayers().contains(requestingPlayer)) {
			sender.sendMessage(ChatColor.RED + "The specified player is not requesting you to a duel.");
			return false;
		}
		
		player.getBukkitPlayer().sendMessage(ChatColor.GREEN + String.format("You denied %s's request to a duel.",
				ChatColor.AQUA + requestingPlayer.getName() + ChatColor.GREEN));
		
		requestingPlayer.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s denied your request to a duel.",
				player.getName() + ChatColor.GREEN));
		
		requestingPlayer.removeRequestingPlayer(requestingPlayer);
		
		return true;
	}
}