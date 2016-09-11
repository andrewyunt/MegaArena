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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrewyunt.megaarena.command.ArenaCommand;
import com.andrewyunt.megaarena.command.BloodToggleCommand;
import com.andrewyunt.megaarena.command.DuelAcceptCommand;
import com.andrewyunt.megaarena.command.DuelCommand;
import com.andrewyunt.megaarena.command.DuelDenyCommand;
import com.andrewyunt.megaarena.command.DuelsToggleCommand;
import com.andrewyunt.megaarena.configuration.ArenaConfiguration;
import com.andrewyunt.megaarena.configuration.SignConfiguration;
import com.andrewyunt.megaarena.db.DataSource;
import com.andrewyunt.megaarena.db.MySQLSource;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.listeners.PlayerAbilityListener;
import com.andrewyunt.megaarena.listeners.PlayerListener;
import com.andrewyunt.megaarena.listeners.PlayerSkillListener;
import com.andrewyunt.megaarena.managers.ArenaManager;
import com.andrewyunt.megaarena.managers.EventManager;
import com.andrewyunt.megaarena.managers.GameManager;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.managers.SignManager;
import com.andrewyunt.megaarena.menu.ClassSelectorMenu;
import com.andrewyunt.megaarena.menu.GeneralMenu;
import com.andrewyunt.megaarena.menu.LayoutEditorMenu;
import com.andrewyunt.megaarena.menu.ShopMenu;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.Utils;

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
	
	private final Logger logger = getLogger();
	private final Server server = getServer();
	private final PluginManager pm = server.getPluginManager();
    private final ClassSelectorMenu classSelectorMenu = new ClassSelectorMenu();
    private final ShopMenu shopMenu = new ShopMenu();
    private final LayoutEditorMenu layoutEditorMenu = new LayoutEditorMenu();
    private final GeneralMenu generalMenu = new GeneralMenu();
    private final Map<Integer, ItemStack> hotbarItems = new HashMap<Integer, ItemStack>();
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final EventManager eventManager = new EventManager();
	private final SignManager signManager = new SignManager();
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	private final SignConfiguration signConfiguration = new SignConfiguration();
	private final DataSource dataSource = new MySQLSource();
	
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
		if (pm.getPlugin("StaffPlus") == null || pm.getPlugin("ProtocolLib") == null) {
			logger.severe("MegaArena is missing one or more dependencies, shutting down...");
			pm.disablePlugin(this);
			return;
		}
		
		/* Set static instance to this */
		instance = this;
		
		/* Save default configs to plugin folder */
		saveDefaultConfig();
		arenaConfiguration.saveDefaultConfig();
		signConfiguration.saveDefaultConfig();
		
		/* Connect to the database */
		if (!dataSource.connect()) {
			logger.severe("Could not connect to the database, shutting down...");
			pm.disablePlugin(MegaArena.getInstance());
			return;
		}
		
		dataSource.updateDB();
		
		/* Set command executors */
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		getCommand("dueldeny").setExecutor(new DuelDenyCommand());
		getCommand("duelstoggle").setExecutor(new DuelsToggleCommand());
		getCommand("bloodtoggle").setExecutor(new BloodToggleCommand());
		
		/* Register events */
		eventManager.registerEffectApplyEvent();
		
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new PlayerAbilityListener(), this);
		pm.registerEvents(new PlayerSkillListener(), this);
		pm.registerEvents(classSelectorMenu, this);
		pm.registerEvents(shopMenu, this);
		pm.registerEvents(layoutEditorMenu, this);
		pm.registerEvents(generalMenu, this);
		
		/* Load all arenas from arenas.yml */
		arenaManager.loadArenas();
		
		/* Load all signs from signs.yml */
		signManager.loadSigns();
		
		/* Create hotbar items and add them to the map */
		createHotbarItems();
		
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
	 * Method is executed while the plugin is being disabled.
	 * 
	 * <p>
	 * Removes all active games and sends the shutdown message to all players
	 * kicked from the games.
	 * </p>
	 */
	@Override
	public void onDisable() {
		
		/* Remove active games */
		Set<Game> toRemove = new HashSet<Game>();

		toRemove.addAll(gameManager.getGames());
		
		for (Game game : toRemove)
			gameManager.deleteGame(game, "Server is shutting down...");
		
		/* Save players to the database */
		Set<GamePlayer> toSave = new HashSet<GamePlayer>();

		toSave.addAll(playerManager.getPlayers());
		
		for (GamePlayer gp : toSave)
			dataSource.savePlayer(gp);
		
		dataSource.disconnect();
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
	 * Gets the instance of the SignManager class.
	 * 
	 * @return
	 * 		Instance of the SignManager class.
	 */
	public SignManager getSignManager() {
		
		return signManager;
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
	 * Gets the instance of the SignConfiguration class.
	 * 
	 * @return
	 * 		Instance of the SignConfiguration class.
	 */
	public SignConfiguration getSignConfig() {
		
		return signConfiguration;
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
	
	/**
	 * Gets the stored instance of the general menu.
	 * 
	 * @return
	 * 		The instance of the genu menu.
	 */
	public GeneralMenu getGeneralMenu() {
		
		return generalMenu;
	}
	
	/**
	 * Gets the stored instance of the layout editor menu.
	 * 
	 * @return
	 * 		The instance of the layout editor menu.
	 */
	public LayoutEditorMenu getLayoutEditorMenu() {
		
		return layoutEditorMenu;
	}
	
	public void createHotbarItems() {
		
		/* Create items */
		ItemStack general = new ItemStack(Material.BOOK);
		ItemStack classSelector = new ItemStack(Material.COMMAND);
		ItemStack playFFA = new ItemStack(Material.IRON_SWORD);
		ItemStack playTDM = new ItemStack(Material.DIAMOND_SWORD);
		
		/* Get item metas */
		ItemMeta generalMeta = general.getItemMeta();
		ItemMeta classSelectorMeta = classSelector.getItemMeta();
		ItemMeta playFFAMeta = playFFA.getItemMeta();
		ItemMeta playTDMMeta = playTDM.getItemMeta();
		
		/* Set meta display names */
		generalMeta.setDisplayName(ChatColor.AQUA + "General");
		classSelectorMeta.setDisplayName(ChatColor.GREEN + "Class Selector");
		playFFAMeta.setDisplayName("Play : Free-for-all");
		playTDMMeta.setDisplayName("Play : Team-deathmatch");
		
		/* Set item metas */
		general.setItemMeta(generalMeta);
		classSelector.setItemMeta(classSelectorMeta);
		playFFA.setItemMeta(playFFAMeta);
		playTDM.setItemMeta(playTDMMeta);
		
		/* Set hotbar items in map */
		hotbarItems.put(0, general);
		hotbarItems.put(1, classSelector);
		hotbarItems.put(7, Utils.addGlow(playFFA));
		hotbarItems.put(8, Utils.addGlow(playTDM));
	}
	
	public Map<Integer, ItemStack> getHotbarItems() {
		
		return hotbarItems;
	}
}