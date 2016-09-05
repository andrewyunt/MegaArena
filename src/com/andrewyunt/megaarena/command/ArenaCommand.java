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

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.ArenaException;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.exception.SpawnException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.GameSide;
import com.andrewyunt.megaarena.objects.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
		help.add(ChatColor.GREEN + "/arena create " + ChatColor.AQUA + "[name] [type]");
		help.add(ChatColor.GREEN + "/arena delete");
		help.add(ChatColor.GREEN + "/arena select " + ChatColor.AQUA + "[name]");
		help.add(ChatColor.GREEN + "/arena edit");
		help.add(ChatColor.GREEN + "/arena addspawn " + ChatColor.AQUA + "[name] [side]");
		help.add(ChatColor.GREEN + "/arena removespawn " + ChatColor.AQUA + "[name]");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("arena"))
			return false;
		
		if (!(args.length > 0)) {
			
			if (!sender.hasPermission("megaarena.arena.help")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			for (String line : help)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			
			return false;
		}
	
		if (args[0].equalsIgnoreCase("addcoins")) {
			
			if (sender instanceof Player) {
				System.out.println("You may only execute that command from the console.");
				return false;
			}
			
			if (!(args.length >= 3)) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena addcoins [player] [amount]");
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
				sender.sendMessage(ChatColor.RED + "Usage: /arena addcoins [player] [amount]");
				return false;
			}
			
			coinsGP.addCoins(coins);
			coinsGP.getBukkitPlayer().sendMessage(ChatColor.GREEN + String.format("You received %s coins from %s.", 
					ChatColor.AQUA + String.valueOf(coins) + ChatColor.GREEN ,
					ChatColor.AQUA + sender.getName() + ChatColor.GREEN));
			
			return true;
		}
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			
			if (!sender.hasPermission("megaarena.arena.help")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			for (String line : help)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			
			return false;
			
		} else if (args[0].equalsIgnoreCase("create")) {
			
			if (!sender.hasPermission("megaarena.arena.create")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			if (!(args.length >= 3)) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena create [name] [type]");
				sender.sendMessage(ChatColor.RED + "Possible Arena Types: DUEL, FFA, TDM");
				return false;
			}
			
			Arena.Type type = null;
			
			try {
				type = Arena.Type.valueOf(args[2].toUpperCase());
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + "Error: Invalid arena type specified.");
				sender.sendMessage(ChatColor.RED + "Possible Arena Types: DUEL, FFA, TDM");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getArenaManager().createArena(args[1], type);
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
			
			sender.sendMessage(String.format(ChatColor.GREEN + "You have"
					+ " created the arena %s, "
					+ "selected the arena, and set it to edit mode.",
					ChatColor.AQUA + arena.getName() + ChatColor.GREEN));
			
		} else if (args[0].equalsIgnoreCase("delete")) {

			if (!sender.hasPermission("megaarena.arena.delete")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException | PlayerException e) {
			}

			if (arena == null) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
				return false;
			}
			
			if (arena.isEdit()) {
				sender.sendMessage(ChatColor.RED + "You must set the arena to edit mode before using that command");
				sender.sendMessage(ChatColor.RED + "Usage: /arena edit");
				return false;
			}
			
			if (arena.isInUse()) {
				MegaArena.getInstance().getGameManager().deleteGame(arena.getGame(), 
						ChatColor.RED + "The arena you were playing in has been deleted.");
				
				try {
					MegaArena.getInstance().getArenaManager().deleteArena(arena);
				} catch (ArenaException e) {
				}
			}
			
		} else if (args[0].equalsIgnoreCase("select")) {
			
			if (!sender.hasPermission("megaarena.arena.select")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			if (!(args.length >= 2)) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena select [name]");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getArenaManager().getArena(args[1]);
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			try {
				MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).selectArena(arena);
			} catch (PlayerException e) {
			}
			
			sender.sendMessage(String.format(ChatColor.GREEN + "You have selected the arena %s.",
					ChatColor.AQUA + arena.getName() + ChatColor.GREEN));
			
		} else if (args[0].equalsIgnoreCase("addspawn")) {
			
			if (!sender.hasPermission("megaarena.arena.addspawn")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena addspawn [name] [side]");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
			} catch (PlayerException e) {
			}
			
			if (!(arena.isEdit())) {
				sender.sendMessage(ChatColor.RED + "You must set the arena to edit mode before using that command");
				sender.sendMessage(ChatColor.RED + "Usage: /arena edit");
				return false;
			}
			
			for (Spawn itrSpawn : arena.getSpawns())
				if (itrSpawn.getName().equalsIgnoreCase(args[1])) {
					sender.sendMessage(ChatColor.RED + "A spawn with that name already exists.");
					return false;
				}
			
			if (arena.getType() == Arena.Type.FFA || arena.getType() == Arena.Type.DUEL)
				if (!args[2].equalsIgnoreCase("INDEPENDENT")) {
					sender.sendMessage(ChatColor.RED + "You can only add INDEPENDENT spawns to a DUEL or TDM arena.");
					return false;
				}
			
			Location loc = ((Player) sender).getLocation();
			
			Spawn spawn = arena.addSpawn(args[1], arena, loc, GameSide.Type.valueOf(args[2]));
			
			sender.sendMessage(String.format(ChatColor.GREEN + "You have created the spawn %s in the arena %s at %s.", 
					ChatColor.AQUA + spawn.getName() + ChatColor.GREEN,
					ChatColor.AQUA + arena.getName() + ChatColor.GREEN,
					String.format("X:%s Y:%s Z:%s world: %s", 
							ChatColor.AQUA + String.valueOf(loc.getX()) + ChatColor.GREEN,
							ChatColor.AQUA + String.valueOf(loc.getY()) + ChatColor.GREEN,
							ChatColor.AQUA + String.valueOf(loc.getZ()) + ChatColor.GREEN,
							ChatColor.AQUA + loc.getWorld().getName())  + ChatColor.GREEN));
			
		} else if (args[0].equalsIgnoreCase("removespawn")) {
			
			if (!sender.hasPermission("megaarena.arena.removespawn")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena removespawn [name]");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
			} catch (PlayerException e) {
			}
			
			if (!(arena.isEdit())) {
				sender.sendMessage(ChatColor.RED + "You must set the arena to edit mode before using that command");
				sender.sendMessage(ChatColor.RED + "Usage: /arena edit");
				return false;
			}
			
			Spawn spawn = null;
			
			try {
				spawn = arena.getSpawn(args[1]);
			} catch (SpawnException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			arena.removeSpawn(spawn);
			
		} else if (args[0].equalsIgnoreCase("edit")) {
			
			if (!sender.hasPermission("megaarena.arena.edit")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = MegaArena.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
			} catch (PlayerException e) {
			}
			
			if (arena.isEdit()) {
				arena.setEdit(false);
				sender.sendMessage(String.format(ChatColor.GREEN + "You have disabled edit mode for the arena %s.",
						ChatColor.AQUA + arena.getName() + ChatColor.GREEN));
			} else {
				MegaArena.getInstance().getGameManager().deleteGame(arena.getGame(),
						ChatColor.RED + "The game you were in has ended due to admins setting the arena to edit mode.");
				arena.setEdit(true);
				sender.sendMessage(String.format(ChatColor.GREEN + "You have enabled edit mode for the arena %s.",
						ChatColor.AQUA + arena.getName() + ChatColor.GREEN));
			}
		
		} else if (args[0].equalsIgnoreCase("list")) {
			
			if (!sender.hasPermission("megaarena.arena.list")) {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return false;
			}
			
			sender.sendMessage(ChatColor.DARK_GRAY + "=" + ChatColor.GRAY + "------------" + ChatColor.DARK_GRAY + "[ " + ChatColor.AQUA + 
				"Arenas List" + ChatColor.DARK_GRAY + " ]" + ChatColor.GRAY + "------------" + ChatColor.DARK_GRAY + "=");
			
			for (Arena arena : MegaArena.getInstance().getArenaManager().getArenas())
				sender.sendMessage(ChatColor.GREEN + arena.getName());
		}
		
		return true;
	}
}