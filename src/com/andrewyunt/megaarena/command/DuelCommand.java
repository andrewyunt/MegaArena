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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * The duelaccept command class which is used as a Bukkit CommandExecutor
 * to request to duel players.
 * 
 * @author Andrew Yunt
 */
public class DuelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (!sender.hasPermission("megaarena.duel")) {
			sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
			return false;
		}
		
		if (!(args.length > 0)) {
			sender.sendMessage(Utils.getFormattedMessage("messages.duel-command-usage"));
			return false;
		}
		
		if (args[0].equalsIgnoreCase(sender.getName())) {
			sender.sendMessage(Utils.getFormattedMessage("messages.not-able-duel-self"));
			return false;
		}
		
		GamePlayer player = null;
		
		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName());
		} catch (PlayerException e) {
		}
		
		if (player.isInGame()) {
			sender.sendMessage(Utils.getFormattedMessage("messages.in-game-cannot-duel"));
			return false;
		}
		
		Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
		GamePlayer targetGP = null;
		
		try {
			targetGP = MegaArena.getInstance().getPlayerManager().getPlayer(targetPlayer.getName());
		} catch (PlayerException e) {
			sender.sendMessage(Utils.getFormattedMessage("messages.target-player-offline"));
			return false;
		} catch (NullPointerException e) {
			sender.sendMessage(Utils.getFormattedMessage("messages.specified-player-not-exists"));
			return false;
		}
		
		if (!targetGP.isAcceptingDuels()) {
			sender.sendMessage(Utils.getFormattedMessage("messages.target-player-not-accepting-duel-requests"));
			return false;
		}
		
		if (targetGP.isInGame()) {
			sender.sendMessage(String.format(
					Utils.getFormattedMessage("messages.cannot-duel-in-game"),
					targetPlayer.getName()));
			return false;
		}
		
		if (!player.hasSelectedClass()) {
			player.getBukkitPlayer().sendMessage(Utils.getFormattedMessage("messages.select-class-request-duel"));
			return false;
		}
		
		targetGP.addRequestingPlayer(player);
		
		targetPlayer.sendMessage(String.format(Utils.getFormattedMessage("messages.requesting-duel"), player.getName()));
		targetPlayer.sendMessage(Utils.getFormattedMessage("messages.duel-accept-command"));
		targetPlayer.sendMessage(Utils.getFormattedMessage("messages.duel-deny-command"));
		
		sender.sendMessage(String.format(Utils.getFormattedMessage("messages.duel-requested"), targetPlayer.getName()));
		
		return true;
	}
}