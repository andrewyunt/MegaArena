/**
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
package com.andrewyunt.megaarena;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.megaarena.command.ArenaCommand;
import com.andrewyunt.megaarena.command.DuelAcceptCommand;
import com.andrewyunt.megaarena.command.DuelCommand;
import com.andrewyunt.megaarena.command.DuelDenyCommand;
import com.andrewyunt.megaarena.configuration.ArenaConfiguration;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.listeners.MegaArenaPlayerAbilityListener;
import com.andrewyunt.megaarena.listeners.MegaArenaPlayerListener;
import com.andrewyunt.megaarena.listeners.MegaArenaPlayerSkillListener;
import com.andrewyunt.megaarena.managers.ArenaManager;
import com.andrewyunt.megaarena.managers.GameManager;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Game;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class MegaArena extends JavaPlugin {
	
	private Logger logger = getLogger();
	
	private Server server = getServer();
	private PluginManager pm = server.getPluginManager();
	private ServicesManager sm = server.getServicesManager();
    private Economy economy = null;
    private Permission permissions = null;
	
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final EffectManager effectManager = new EffectManager(EffectLib.instance());
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	
	private static MegaArena instance = null;
	
	@Override
	public void onEnable() {
		
		/* Check for dependencies */
		if (pm.getPlugin("StaffPlus") == null || !(setupEconomy() || !(setupPermissions()))) {
			logger.severe("MegaArena is missing one or more dependencies, shutting down...");
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
		pm.registerEvents(new MegaArenaPlayerListener(), this);
		pm.registerEvents(new MegaArenaPlayerAbilityListener(), this);
		pm.registerEvents(new MegaArenaPlayerSkillListener(), this);
		
		/* Load all arenas from arenas.yml */
		arenaManager.loadArenas();
		
		/* Create games for FFA and TDM arenas */
		for (Arena arena : arenaManager.getArenas(Arena.Type.TDM))
			try {
				arena.setGame(gameManager.createGame(arena));
			} catch (GameException e) {
				logger.warning(e.getMessage());
			}
		
		for (Arena arena : arenaManager.getArenas(Arena.Type.FFA))
			try {
				arena.setGame(gameManager.createGame(arena));
			} catch (GameException e) {
				logger.warning(e.getMessage());
			}
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
		
		for (Game game : getGameManager().getGames())
			gameManager.deleteGame(game, "Server shutting down.");
	}
	
	public static MegaArena getInstance() {
		
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
	
	public EffectManager getEffectManager() {
		
		return effectManager;
	}
	
	public ArenaConfiguration getArenaConfig() {
		
		return arenaConfiguration;
	}
}