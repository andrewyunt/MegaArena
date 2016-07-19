package com.andrewyunt.arenaplugin;

import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.arenaplugin.command.ArenaCommand;
import com.andrewyunt.arenaplugin.command.DuelAcceptCommand;
import com.andrewyunt.arenaplugin.command.DuelCommand;
import com.andrewyunt.arenaplugin.command.DuelDenyCommand;
import com.andrewyunt.arenaplugin.configuration.ArenaConfiguration;
import com.andrewyunt.arenaplugin.listeners.ArenaPluginPlayerListener;
import com.andrewyunt.arenaplugin.managers.ArenaManager;
import com.andrewyunt.arenaplugin.managers.GameManager;
import com.andrewyunt.arenaplugin.managers.PlayerManager;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

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
	private ServicesManager sm = server.getServicesManager();
    private Economy economy = null;
    private Permission permissions = null;
	
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	
	private static ArenaPlugin instance = null;
	
	@Override
	public void onEnable() {
		
		logger.info("Enabling " + pdf.getName() + " v" + pdf.getVersion() + "... Please wait.");
		
		/* Check for dependencies */
		if (pm.getPlugin("StaffPlus") == null || !(setupEconomy() || !(setupPermissions()))) {
			logger.severe("ArenaPlugin is missing one or more dependencies, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		/* Set static instance to this */
		instance = this;
		
		/* Save default configs to plugin folder */
		saveDefaultConfig();
		arenaConfiguration.saveDefaultConfig();
		
		/* Set command executors */
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		getCommand("dueldeny").setExecutor(new DuelDenyCommand());
		
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
    	
        RegisteredServiceProvider<Economy> economyProvider = sm.getRegistration(Economy.class);
        
        if (economyProvider != null)
            economy = economyProvider.getProvider();

        return (economy != null);
    }
    
    private boolean setupPermissions() {
    	
        RegisteredServiceProvider<Permission> permissionProvider = sm.getRegistration(Permission.class);
        
        if (permissionProvider != null)
            permissions = permissionProvider.getProvider();
            
        return (permissions != null);
    }
    
    public Economy getEconomy() {
    	
    	return economy;
    }
    
    public Permission getPermissions() {
    	
    	return permissions;
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
}