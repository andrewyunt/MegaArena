package com.andrewyunt.arenaplugin;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.arenaplugin.command.ArenaCommand;
import com.andrewyunt.arenaplugin.command.DuelAcceptCommand;
import com.andrewyunt.arenaplugin.command.DuelCommand;
import com.andrewyunt.arenaplugin.command.DuelDenyCommand;
import com.andrewyunt.arenaplugin.configuration.ArenaConfiguration;
import com.andrewyunt.arenaplugin.configuration.PlayerConfiguration;
import com.andrewyunt.arenaplugin.listeners.ArenaPluginPlayerListener;
import com.andrewyunt.arenaplugin.managers.ArenaManager;
import com.andrewyunt.arenaplugin.managers.GameManager;
import com.andrewyunt.arenaplugin.managers.PlayerManager;
import com.andrewyunt.arenaplugin.menu.ClassSelectorMenu;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPlugin extends JavaPlugin {
	
	public Logger logger = getLogger();
	
	private PluginDescriptionFile pdf = getDescription();
	
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	private final PlayerConfiguration playerConfiguration = new PlayerConfiguration();
	
	private static ArenaPlugin instance = null;
	
	@Override
	public void onEnable() {
		
		logger.info("Enabling " + pdf.getName() + " v" + pdf.getVersion() + "... Please wait.");
		
		instance = this;
		
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		getCommand("dueldeny").setExecutor(new DuelDenyCommand());
		
		getServer().getPluginManager().registerEvents(new ArenaPluginPlayerListener(), this);
	}
	
	@Override
	public void onDisable() {
		
		logger.info("Disabling " + pdf.getName() + " v" + pdf.getVersion() + "... Please wait.");
	}
	
	public static ArenaPlugin getInstance() {
		
		return instance;
	}
	
	public ArenaManager getArenaManager() {
		
		return arenaManager;
	}
	
	public GameManager getGameManager() {
		
		return gameManager;
	}
	
	public PlayerManager getPlayerManager() {
		
		return playerManager;
	}
	
	public ArenaConfiguration getArenaConfig() {
		
		return arenaConfiguration;
	}
	
	public PlayerConfiguration getPlayerConfig() {
		
		return playerConfiguration;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(cmd.getName().equalsIgnoreCase("test")))
			return false;
		
		if(!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		new ClassSelectorMenu(player);
		
		return true;
	}
}