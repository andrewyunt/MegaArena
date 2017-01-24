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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.ArenaException;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.exception.SpawnException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.GameSide;
import com.andrewyunt.megaarena.objects.Spawn;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * The arena command class which is used as a Bukkit CommandExecutor.
 * 
 * @author Andrew Yunt
 */
public class ArenaCommand implements CommandExecutor {
	
	private static final List<String> help = new ArrayList<String>();

	static {
		help.add(ChatColor.DARK_GRAY + "=" + ChatColor.GRAY + "------------" + ChatColor.DARK_GRAY + "[ " + ChatColor.AQUA + 
				"MegaArena Help" + ChatColor.DARK_GRAY + " ]" + ChatColor.GRAY + "------------" + ChatColor.DARK_GRAY + "=");
		help.add(ChatColor.GREEN + "/arena list");
		help.add(ChatColor.GREEN + "/arena create " + ChatColor.AQUA + "[name] [type] [tournament]");
		help.add(ChatColor.GREEN + "/arena delete");
		help.add(ChatColor.GREEN + "/arena select " + ChatColor.AQUA + "[name]");
		help.add(ChatColor.GREEN + "/arena edit");
		help.add(ChatColor.GREEN + "/arena addspawn " + ChatColor.AQUA + "[name] [side]");
		help.add(ChatColor.GREEN + "/arena removespawn " + ChatColor.AQUA + "[name]");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(args.length > 0)) {
			
			if (!sender.hasPermission("megaarena.arena.help")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			for (String line : help)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			
			return false;
		}
	
		if (args[0].equalsIgnoreCase("addcoins")) {
			
			if (sender instanceof Player) {
				sender.sendMessage(Utils.getFormattedMessage("messages.only-execute-console"));
				return false;
			}
			
			if (!(args.length >= 3)) {
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-addcoins-usage"));
				return false;
			}
			
			Player coinsPlayer = Bukkit.getServer().getPlayer(args[1]);
			GamePlayer coinsGP = null;
			
			try {
				coinsGP = MegaArena.getInstance().getPlayerManager().getPlayer(coinsPlayer.getName());
			} catch (PlayerException e) {
			}
			
			int coins = 0;
			
			try {
				coins = Integer.valueOf(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-addcoins-usage"));
				return false;
			}
			
			coinsGP.addCoins(coins);
			coinsGP.getBukkitPlayer().sendMessage(String.format(
					Utils.getFormattedMessage("messages.coins-received-addcoins"), 
					String.valueOf(coins),
					sender.getName()));
		
			return true;
		}
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			
			if (!sender.hasPermission("megaarena.arena.help")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			for (String line : help)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			
			return false;
			
		} else if (args[0].equalsIgnoreCase("create")) {
			
			if (!sender.hasPermission("megaarena.arena.create")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			if (args.length < 4) {
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-create-usage"));
				sender.sendMessage(Utils.getFormattedMessage("messages.possible-arena-types"));
				return false;
			}
			
			Arena.Type type = null;
			
			try {
				type = Arena.Type.valueOf(args[2].toUpperCase());
			} catch (IllegalArgumentException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.invalid-arena-type-specified"));
				sender.sendMessage(Utils.getFormattedMessage("messages.possible-arena-types"));
				return false;
			}
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getArenaManager().createArena(args[1], type, Boolean.valueOf(args[3]));
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			if (arena == null)
				return false;
			
			try {
				MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).selectArena(arena);
			} catch (PlayerException e) {
			}
			
			arena.setEdit(true);
			
			sender.sendMessage(String.format(Utils.getFormattedMessage("messages.arena-created-successfully"),
					arena.getName()));
			
		} else if (args[0].equalsIgnoreCase("delete")) {
			
			if (!sender.hasPermission("megaarena.arena.delete")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException | PlayerException e) {
			}
			
			if (arena == null) {
				sender.sendMessage(Utils.getFormattedMessage("messages.select-arena-first"));
				return false;
			}
			
			if (!arena.isEdit()) {
				sender.sendMessage(Utils.getFormattedMessage("messages.edit-arena-first"));
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-edit-usage"));
				return false;
			}
			
			if (arena.isInUse()) {
				MegaArena.getInstance().getGameManager().deleteGame(
						arena.getGame(),
						Utils.getFormattedMessage("messages.arena-playing-deleted"));
				
				try {
					MegaArena.getInstance().getArenaManager().deleteArena(arena);
				} catch (ArenaException e) {
					e.printStackTrace();
				}
			}
			
			sender.sendMessage(String.format(
					Utils.getFormattedMessage("messages.arena-deleted-successfully"),
					arena.getName()));
			
		} else if (args[0].equalsIgnoreCase("select")) {
			
			if (!sender.hasPermission("megaarena.arena.select")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			if (!(args.length >= 2)) {
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-select-usage"));
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getArenaManager().getArena(args[1]);
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return false;
			}
			
			try {
				MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).selectArena(arena);
			} catch (PlayerException e) {
				return false;
			}
			
			sender.sendMessage(String.format(
					Utils.getFormattedMessage("messages.arena-selected"),
					arena.getName()));
			
		} else if (args[0].equalsIgnoreCase("addspawn")) {
			
			if (!sender.hasPermission("megaarena.arena.addspawn")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			if (args.length < 3) {
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-addspawn-usage"));
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.select-arena-first"));
			} catch (PlayerException e) {
			}
			
			if (!arena.isEdit()) {
				sender.sendMessage(Utils.getFormattedMessage("messages.edit-arena-first"));
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-edit-usage"));
				return false;
			}
			
			for (Spawn itrSpawn : arena.getSpawns())
				if (itrSpawn.getName().equalsIgnoreCase(args[1])) {
					sender.sendMessage(Utils.getFormattedMessage("messages.spawn-exists"));
					return false;
				}
			
			if (arena.getType() == Arena.Type.FFA || arena.getType() == Arena.Type.DUEL)
				if (!args[2].equalsIgnoreCase("solo")) {
					sender.sendMessage(Utils.getFormattedMessage("messages.cannot-add-solo-spawns-tdm"));
					return false;
				}
			
			Location loc = ((Player) sender).getLocation();
			
			Spawn spawn = arena.addSpawn(args[1], arena, loc, GameSide.Type.valueOf(args[2]));
			
			sender.sendMessage(String.format(
					Utils.getFormattedMessage("messages.spawn-created-successfully"), 
					spawn.getName(),
					arena.getName(),
					String.valueOf(loc.getX()),
					String.valueOf(loc.getY()),
					String.valueOf(loc.getZ()),
					loc.getWorld().getName()));
			
		} else if (args[0].equalsIgnoreCase("removespawn")) {
			
			if (!sender.hasPermission("megaarena.arena.removespawn")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			if (args.length < 2) {
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-removespawn-usage"));
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.select-arena-first"));
			} catch (PlayerException e) {
			}
			
			if (!arena.isEdit()) {
				sender.sendMessage(Utils.getFormattedMessage("messages.edit-arena-first"));
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-edit-usage"));
				return false;
			}
			
			Spawn spawn = null;
			
			try {
				spawn = arena.getSpawn(args[1]);
			} catch (SpawnException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			arena.removeSpawn(spawn);
			
		} else if (args[0].equalsIgnoreCase("setqueuelocation")) {
			
			if (!sender.hasPermission("megaarena.arena.setspawnloc")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			Arena arena;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.select-arena-first"));
				return false;
			} catch (PlayerException e) {
				e.printStackTrace();
				return false;
			}
			
			if (!arena.isEdit()) {
				sender.sendMessage(Utils.getFormattedMessage("messages.edit-arena-first"));
				sender.sendMessage(Utils.getFormattedMessage("messages.arena-edit-usage"));
				return false;
			}
			
			Location loc = ((Player) sender).getLocation();
			
			arena.setQueueLocation(loc);
			arena.save();
			
			sender.sendMessage(String.format(
					Utils.getFormattedMessage("messages.queue-location-set-successfully"), 
					arena.getName(),
					String.valueOf(loc.getX()),
					String.valueOf(loc.getY()),
					String.valueOf(loc.getZ()),
					loc.getWorld().getName()));
			
		} else if (args[0].equalsIgnoreCase("edit")) {
			
			if (!sender.hasPermission("megaarena.arena.edit")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.select-arena-first"));
			} catch (PlayerException e) {
				e.printStackTrace();
				return false;
			}
			
			if (arena == null)
				return false;
			
			if (arena.isEdit()) {
				arena.setEdit(false);
				
				try {
					MegaArena.getInstance().getGameManager().createGame(arena);
				} catch (GameException e) {
				}
				
				sender.sendMessage(String.format(
						Utils.getFormattedMessage("messages.edit-mode-disabled"),
						ChatColor.AQUA + arena.getName()));
			} else {
				if (arena.isInUse())
					MegaArena.getInstance().getGameManager().deleteGame(
							arena.getGame(),
							Utils.getFormattedMessage("messages.game-ended-edit"));
				arena.setEdit(true);
				sender.sendMessage(String.format(
						Utils.getFormattedMessage("messages.edit-mode-enabled"),
						arena.getName()));
			}
		
		} else if (args[0].equalsIgnoreCase("list")) {
			
			if (!sender.hasPermission("megaarena.arena.list")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			sender.sendMessage(ChatColor.DARK_GRAY + "=" + ChatColor.GRAY + "------------" + ChatColor.DARK_GRAY + "[ " + ChatColor.AQUA + 
				"Arenas List" + ChatColor.DARK_GRAY + " ]" + ChatColor.GRAY + "------------" + ChatColor.DARK_GRAY + "=");
			
			for (Arena arena : MegaArena.getInstance().getArenaManager().getArenas())
				sender.sendMessage(ChatColor.GREEN + arena.getName());
			
		} else if (args[0].equalsIgnoreCase("creategame")) {
			
			if (!sender.hasPermission("megaarena.arena.creategame")) {
				sender.sendMessage(Utils.getFormattedMessage("messages.no-permission-command"));
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.select-arena-first"));
			} catch (PlayerException e) {
				e.printStackTrace();
				return false;
			}
			
			if (arena == null)
				return false;
			
			if (!arena.isTournament()) {
				sender.sendMessage(Utils.getFormattedMessage("messages.only-able-create-tournament-games"));
				return false;
			}
			
			if (arena.isEdit()) {
				sender.sendMessage(Utils.getFormattedMessage("messages.cannot-create-game-edit-mode"));
				return false;
			}
			
			try {
				MegaArena.getInstance().getGameManager().createGame(arena);
			} catch (GameException e) {
				sender.sendMessage(Utils.getFormattedMessage("messages.game-created-unsuccessfully"));
			}
			
			sender.sendMessage(String.format(
					Utils.getFormattedMessage("messages.game-created-successfully"),
					arena.getName()));
		}
		
		return true;
	}
}