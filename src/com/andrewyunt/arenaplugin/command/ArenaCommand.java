package com.andrewyunt.arenaplugin.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.ArenaException;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.exception.SpawnException;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.Game.Side;
import com.andrewyunt.arenaplugin.objects.Spawn;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaCommand implements CommandExecutor {
	
	private static final List<String> help = new ArrayList<String>();

	static {
		help.add("/arena create [name] [type]");
		help.add("/arena delete [name]");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("arena"))
			return false;
		
		if (!(sender instanceof Player)) {
			System.out.println("You may not execute that command from the console.");
			return false;
		}
		
		if (!(args.length > 0)) {
			for (String line : help)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			
			return false;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			
			for (String line : help)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			
			return false;
			
		} else if (args[0].equalsIgnoreCase("create")) {
			
			if (!(args.length >= 3)) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena create [name] [type]");
				sender.sendMessage(ChatColor.RED + "Possible Arena Types: DUEL, FFA, TDM");
				return false;
			}
			
			
			Arena arena = null;
			
			try {
				arena = ArenaPlugin.getInstance().getArenaManager().createArena(args[1], ArenaType.valueOf(args[2]));
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + "Error: Invalid arena type specified.");
				sender.sendMessage(ChatColor.RED + "Possible Arena Types: DUEL, FFA, TDM");
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			if (arena != null)
				try {
					ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName()).selectArena(arena);
				} catch (PlayerException e) {
				}
			
		} else if (args[0].equalsIgnoreCase("delete")) {

			Arena arena = null;
			
			try {
				arena = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e1) {
			} catch (PlayerException e1) {
			}
			
			if (arena == null) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
				return false;
			}
			
			if (arena.isInUse()) {
				arena.getGame().end();
				
				try {
					ArenaPlugin.getInstance().getArenaManager().deleteArena(arena);
				} catch (ArenaException e) {
				}
			}
			
		} else if (args[0].equalsIgnoreCase("select")) {
			
			if (!(args.length > 2)) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena select [name]");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = ArenaPlugin.getInstance().getArenaManager().getArena(args[1]);
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			try {
				ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName()).selectArena(arena);
			} catch (PlayerException e) {
			}
			
			sender.sendMessage(String.format(ChatColor.GOLD + "You have selected the arena %s.", arena.getName()));
			
		} else if (args[0].equalsIgnoreCase("addspawn")) {
			
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena addspawn [name] [side]");
				return false;
			}
			
			Arena arena = null;
			
			try {
				arena = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
			} catch (PlayerException e) {
			}
			
			Location loc = ((Player) sender).getLocation();
			
			Spawn spawn = arena.addSpawn(args[1], arena, loc, Side.valueOf(args[2]));
		
			sender.sendMessage(String.format(ChatColor.GOLD + "You have created the spawn %s in the arena %s at %s.", 
					spawn.getName(), 
					arena.getName(),
					String.format("X:%s Y:%s Z:%s world: %s", loc.getX(), loc.getY(), loc.getZ(), loc.getWorld())));
			
		} else if (args[0].equalsIgnoreCase("removespawn")) {
			
			Arena arena = null;
			
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /arena removespawn [name]");
				return false;
			}
			
			try {
				arena = ArenaPlugin.getInstance().getPlayerManager().getPlayer(sender.getName()).getSelectedArena();
			} catch (ArenaException e) {
				sender.sendMessage(ChatColor.RED + "You must select an arena using before using that command.");
			} catch (PlayerException e) {
			}
			
			Spawn spawn = null;
			
			try {
				spawn = arena.getSpawn(args[1]);
			} catch (SpawnException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			
			arena.removeSpawn(spawn);
		}
		
		return true;
	}
}