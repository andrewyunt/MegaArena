package com.andrewyunt.arenaplugin;

import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.arenaplugin.command.ArenaCommand;
import com.andrewyunt.arenaplugin.command.DuelAcceptCommand;
import com.andrewyunt.arenaplugin.command.DuelCommand;
import com.andrewyunt.arenaplugin.configuration.ArenaConfiguration;
import com.andrewyunt.arenaplugin.configuration.PlayerConfiguration;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.listeners.ArenaPluginPlayerListener;
import com.andrewyunt.arenaplugin.managers.ArenaManager;
import com.andrewyunt.arenaplugin.managers.GameManager;
import com.andrewyunt.arenaplugin.managers.PlayerManager;
import com.andrewyunt.arenaplugin.menu.ClassSelectorMenu;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.ClassType;
import com.andrewyunt.arenaplugin.utilities.Utils;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPlugin extends JavaPlugin {
	
	private Logger logger = getLogger();
	
	private PluginDescriptionFile pdf = getDescription();
	private Server server = getServer();
	private PluginManager pm = server.getPluginManager();
    private Economy economy = null;
	
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	private final PlayerConfiguration playerConfiguration = new PlayerConfiguration();
	
	private static ArenaPlugin instance = null;
	
	@Override
	public void onEnable() {
		
		logger.info("Enabling " + pdf.getName() + " v" + pdf.getVersion() + "... Please wait.");
		
		/* Check for dependencies */
		if (pm.getPlugin("StaffPlus") == null || !(setupEconomy())) {
			logger.severe("ArenaPlugin is missing one or more dependencies, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		/* Set static instance to this */
		instance = this;
		
		/* Set command executors */
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		//getCommand("dueldeny").setExecutor(new DuelDenyCommand());
		
		/* Register events */
		pm.registerEvents(new ArenaPluginPlayerListener(), this);
		
		/* Load all arenas from arenas.yml */
		arenaManager.loadArenas();
		
		/* Create games for FFA and TDM arenas */
		for (Arena arena : arenaManager.getArenas(ArenaType.TDM))
			arena.setGame(gameManager.createGame(arena, new HashSet<ArenaPlayer>()));
		
		for (Arena arena : arenaManager.getArenas(ArenaType.FFA))
			arena.setGame(gameManager.createGame(arena, new HashSet<ArenaPlayer>()));
	}

    private boolean setupEconomy() {
    	
        RegisteredServiceProvider<Economy> economyProvider = server.getServicesManager().getRegistration(Economy.class);
        
        if (economyProvider != null)
            economy = economyProvider.getProvider();

        return (economy != null);
    }
    
    public Economy getEconomy() {
    	
    	return economy;
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
		
		ArenaPlayer ap = null;
		
		try {
			ap = playerManager.getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		sender.sendMessage(ChatColor.YELLOW + String.valueOf(Utils.getClassLevel(ap, ClassType.CREEPER)));
		
		return true;
	}
}