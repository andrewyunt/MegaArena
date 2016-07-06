package com.andrewyunt.arenaplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DuelDenyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("dueldeny"))
			return false;
		
		return false;
	}
}
