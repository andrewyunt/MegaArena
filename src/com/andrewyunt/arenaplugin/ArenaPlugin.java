package com.andrewyunt.arenaplugin;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.arenaplugin.commands.ArenaCommand;
import com.andrewyunt.arenaplugin.commands.DuelAcceptCommand;
import com.andrewyunt.arenaplugin.commands.DuelCommand;
import com.andrewyunt.arenaplugin.commands.DuelDenyCommand;
import com.andrewyunt.arenaplugin.managers.ArenaManager;
import com.andrewyunt.arenaplugin.managers.GameManager;
import com.andrewyunt.arenaplugin.managers.PlayerManager;

public class ArenaPlugin extends JavaPlugin {
	
	public Logger logger = getLogger();
	
	private PluginDescriptionFile pdf = getDescription();
	
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	
	private static ArenaPlugin instance = null;
	
	@Override
	public void onEnable() {
		
		logger.info("Enabling " + pdf.getName() + " v" + pdf.getVersion() + "... Please wait.");
		
		instance = this;
		
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		getCommand("dueldeny").setExecutor(new DuelDenyCommand());
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
}