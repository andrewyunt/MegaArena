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
package com.andrewyunt.megaarena;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.andrewyunt.megaarena.command.ArenaCommand;
import com.andrewyunt.megaarena.command.DuelAcceptCommand;
import com.andrewyunt.megaarena.command.DuelCommand;
import com.andrewyunt.megaarena.command.DuelDenyCommand;
import com.andrewyunt.megaarena.command.DuelsToggleCommand;
import com.andrewyunt.megaarena.configuration.ArenaConfiguration;
import com.andrewyunt.megaarena.db.DataSource;
import com.andrewyunt.megaarena.db.MongoDBSource;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.listeners.MegaArenaPlayerAbilityListener;
import com.andrewyunt.megaarena.listeners.MegaArenaPlayerListener;
import com.andrewyunt.megaarena.listeners.MegaArenaPlayerSkillListener;
import com.andrewyunt.megaarena.managers.ArenaManager;
import com.andrewyunt.megaarena.managers.EventManager;
import com.andrewyunt.megaarena.managers.GameManager;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.menu.ClassSelectorMenu;
import com.andrewyunt.megaarena.menu.ShopMenu;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.GamePlayer;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import net.milkbowl.vault.permission.Permission;

/**
 * The main class in the MegaArena plugin.
 * 
 * <p>
 * You can get the instance of this class using the static getInstance() method.
 * </p>
 * 
 * @author Andrew Yunt
 */
public class MegaArena extends JavaPlugin {
	
	private Logger logger = getLogger();
	private Server server = getServer();
	private PluginManager pm = server.getPluginManager();
	private ServicesManager sm = server.getServicesManager();
    private Permission permissions = null;
    private ClassSelectorMenu classSelectorMenu = new ClassSelectorMenu();
    private ShopMenu shopMenu = new ShopMenu();
	
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final EffectManager effectManager = new EffectManager(EffectLib.instance());
	private final EventManager eventManager = new EventManager();
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	private final DataSource dataSource = new MongoDBSource();
	
	private static MegaArena instance = null;
	
	/**
	 * Method is executed while the plugin is being enabled.
	 * 
	 * <p>
	 * Checks for dependencies, sets the static instance of the class, saves default
	 * configuration files, sets command executors, registers events, loads arenas from
	 * arenas.yml, creates FFA and TDM games, and creates default scoreboard.
	 * </p>
	 */
	@Override
	public void onEnable() {
		
		/* Check for dependencies */
		if (pm.getPlugin("StaffPlus") == null || pm.getPlugin("EffectLib") == null || !(setupPermissions())) {
			logger.severe("MegaArena is missing one or more dependencies, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		
		/* Set static instance to this */
		instance = this;
		
		/* Save default configs to plugin folder */
		saveDefaultConfig();
		arenaConfiguration.saveDefaultConfig();
		
		/* Connect to the database */
		if (!dataSource.connect()) {
			logger.severe("Could not connect to the database, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		
		/* Set command executors */
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		getCommand("dueldeny").setExecutor(new DuelDenyCommand());
		getCommand("duelstoggle").setExecutor(new DuelsToggleCommand());
		
		/* Register events */
		eventManager.registerEffectApplyEvent();
		
		pm.registerEvents(new MegaArenaPlayerListener(), this);
		pm.registerEvents(new MegaArenaPlayerAbilityListener(), this);
		pm.registerEvents(new MegaArenaPlayerSkillListener(), this);
		pm.registerEvents(classSelectorMenu, this);
		pm.registerEvents(shopMenu, this);
		
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
    
	/**
	 * Sets up the Vault permissions.
	 */
    private boolean setupPermissions() {
    	
        RegisteredServiceProvider<Permission> permissionProvider = sm.getRegistration(Permission.class);
        
        if (permissionProvider != null)
            permissions = permissionProvider.getProvider();
            
        return (permissions != null);
    }
    
    /**
     * Gets the Vault permissions.
     * 
     * @return
     * 		Instance of the Vault Permission class.
     */
    public Permission getPermissions() {
    	
    	return permissions;
    }
	
	/**
	 * Method is executed while the plugin is being disabled.
	 * 
	 * <p>
	 * Removes all active games and sends the shutdown message to all players
	 * kicked from the games.
	 * <p>
	 */
	@Override
	public void onDisable() {
		
		/* Remove active games */
		Set<Game> toRemove = new HashSet<Game>();
		
		for (Game game : gameManager.getGames())
			toRemove.add(game);
		
		for (Game game : toRemove)
			gameManager.deleteGame(game, "Server is shutting down...");
		
		/* Save players to the database */
		Set<GamePlayer> toSave = new HashSet<GamePlayer>();
		
		for (GamePlayer gp : playerManager.getPlayers())
			toSave.add(gp);
		
		for (GamePlayer gp : toSave) {
			Bukkit.getServer().broadcastMessage(gp.getName());
			MegaArena.getInstance().getDataSource().savePlayer(gp);
		}
	}
	
	/**
	 * Gets the instance of the MegaArena class.
	 * 
	 * @return
	 * 		Instance of the MegaArena class.
	 */
	public static MegaArena getInstance() {
		
		return instance;
	}
	
	/**
	 * Gets the instance of the ArenaManager class.
	 * 
	 * @return
	 * 		Instance of the ArenaManager class.
	 */
	public ArenaManager getArenaManager() {
		
		return arenaManager;
	}
	
	/**
	 * Gets the instance of the GameManager class.
	 * 
	 * @return
	 * 		Instance of the GameManager class.
	 */
	public GameManager getGameManager() {
		
		return gameManager;
	}
	
	/**
	 * Gets the instance of the PlayerManager class.
	 * 
	 * @return
	 * 		Instance of the PlayerManager class.
	 */
	public PlayerManager getPlayerManager() {
		
		return playerManager;
	}
	
	/**
	 * Gets the instance of the EffectManager class.
	 * 
	 * @return
	 * 		Instance of the EffectManager class.
	 */
	public EffectManager getEffectManager() {
		
		return effectManager;
	}
	
	/**
	 * Gets the instance of the ArenaConfiguration class.
	 * 
	 * @return
	 * 		Instance of the ArenaConfiguration class.
	 */
	public ArenaConfiguration getArenaConfig() {
		
		return arenaConfiguration;
	}
	
	/**
	 * Gets the plugin's data source.
	 * 
	 * @return
	 * 		An instance that extends the DataSource class.
	 */
	public DataSource getDataSource() {
		
		return dataSource;
	}
	
	/**
	 * Gets the stored instance of the class selector menu.
	 * 
	 * @return
	 * 		The instance of the class selector menu.
	 */
	public ClassSelectorMenu getClassSelectorMenu() {
		
		return classSelectorMenu;
	}
	
	/**
	 * Gets the stored instance of the shop menu.
	 * 
	 * @return
	 * 		The instance of the shop menu.
	 */
	public ShopMenu getShopMenu() {
		
		return shopMenu;
	}
}