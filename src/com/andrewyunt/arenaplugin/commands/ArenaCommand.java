package com.andrewyunt.arenaplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ArenaCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!cmd.getName().equalsIgnoreCase("arena"))
			return false;
		
		// TODO Auto-generated method stub
		return false;
	}
}