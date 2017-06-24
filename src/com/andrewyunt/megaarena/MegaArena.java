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

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.command.ArenaCommand;
import com.andrewyunt.megaarena.command.BloodToggleCommand;
import com.andrewyunt.megaarena.command.DuelAcceptCommand;
import com.andrewyunt.megaarena.command.DuelCommand;
import com.andrewyunt.megaarena.command.DuelDenyCommand;
import com.andrewyunt.megaarena.command.DuelsToggleCommand;
import com.andrewyunt.megaarena.command.SpawnCommand;
import com.andrewyunt.megaarena.configuration.ArenaConfiguration;
import com.andrewyunt.megaarena.configuration.SignConfiguration;
import com.andrewyunt.megaarena.db.DataSource;
import com.andrewyunt.megaarena.db.MySQLSource;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.listeners.PlayerAbilityListener;
import com.andrewyunt.megaarena.listeners.PlayerListener;
import com.andrewyunt.megaarena.listeners.PlayerSkillListener;
import com.andrewyunt.megaarena.listeners.SpectatorsInteractionsListener;
import com.andrewyunt.megaarena.managers.ArenaManager;
import com.andrewyunt.megaarena.managers.GameManager;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.managers.SignManager;
import com.andrewyunt.megaarena.menu.ClassSelectorMenu;
import com.andrewyunt.megaarena.menu.LayoutEditorMenu;
import com.andrewyunt.megaarena.menu.PlayMenu;
import com.andrewyunt.megaarena.menu.ShopMenu;
import com.andrewyunt.megaarena.menu.SpectateMenu;
import com.andrewyunt.megaarena.menu.UpgradesMenu;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.NMSUtils;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_7_R1;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_7_R2;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_7_R3;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_7_R4;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_8_R1;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_8_R2;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_8_R3;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_9_R1;
import com.andrewyunt.megaarena.utilities.NMSUtilsv1_9_R2;

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
	
	private final ClassSelectorMenu classSelectorMenu = new ClassSelectorMenu();
	private final UpgradesMenu upgradesMenu = new UpgradesMenu();
	private final LayoutEditorMenu layoutEditorMenu = new LayoutEditorMenu();
	private final ShopMenu shopMenu = new ShopMenu();
	private final PlayMenu playMenu = new PlayMenu();
	private final SpectateMenu spectateMenu = new SpectateMenu();
	private final ArenaManager arenaManager = new ArenaManager();
	private final GameManager gameManager = new GameManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final SignManager signManager = new SignManager();
	private final ArenaConfiguration arenaConfiguration = new ArenaConfiguration();
	private final SignConfiguration signConfiguration = new SignConfiguration();
	private final DataSource dataSource = new MySQLSource();
	
	private static MegaArena instance = null;
	
	private NMSUtils nmsUtils;
	private int nextTournamentCountdownTime = 3600;
	
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
		
		// Set static instance to this
		instance = this;
		
		// Save default configs to plugin folder
		saveDefaultConfig();
		arenaConfiguration.saveDefaultConfig();
		signConfiguration.saveDefaultConfig();
		
		PluginManager pm = getServer().getPluginManager();
		
		// Connect to the database
		if (!dataSource.connect()) {
			getLogger().severe("Could not connect to the database, shutting down...");
			pm.disablePlugin(MegaArena.getInstance());
			return;
		}
		
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		
		switch (version) {
		case "v1_7_R1":
			nmsUtils = new NMSUtilsv1_7_R1();
			break;
		case "v1_7_R2":
			nmsUtils = new NMSUtilsv1_7_R2();
			break;
		case "v1_7_R3":
			nmsUtils = new NMSUtilsv1_7_R3();
			break;
		case "v1_7_R4":
			nmsUtils = new NMSUtilsv1_7_R4();
			break;
		case "v1_8_R1":
			nmsUtils = new NMSUtilsv1_8_R1();
			break;
		case "v1_8_R2":
			nmsUtils = new NMSUtilsv1_8_R2();
			break;
		case "v1_8_R3":
			nmsUtils = new NMSUtilsv1_8_R3();
			break;
		case "v1_9_R1":
			nmsUtils = new NMSUtilsv1_9_R1();
			break;
		case "v1_9_R2":
			nmsUtils = new NMSUtilsv1_9_R2();
			break;
		default:
			getLogger().severe("MegaArena is not supported for your Minecraft server version.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		dataSource.updateDB();
		
		// Set command executors
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("duel").setExecutor(new DuelCommand());
		getCommand("duelaccept").setExecutor(new DuelAcceptCommand());
		getCommand("dueldeny").setExecutor(new DuelDenyCommand());
		getCommand("duelstoggle").setExecutor(new DuelsToggleCommand());
		getCommand("bloodtoggle").setExecutor(new BloodToggleCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		
		// Register events
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new PlayerAbilityListener(), this);
		pm.registerEvents(new PlayerSkillListener(), this);
		pm.registerEvents(new SpectatorsInteractionsListener(), this);
		pm.registerEvents(classSelectorMenu, this);
		pm.registerEvents(layoutEditorMenu, this);
		pm.registerEvents(upgradesMenu, this);
		pm.registerEvents(shopMenu, this);
		pm.registerEvents(playMenu, this);
		pm.registerEvents(spectateMenu, this);
		
		// Load all arenas from arenas.yml
		arenaManager.loadArenas();
		
		// Load all signs from signs.yml
		signManager.loadSigns();
		
		// Create games for FFA and TDM arenas
		for (Arena arena : arenaManager.getArenas()) {
			if (arena.getType() != Arena.Type.DUEL && !arena.isTournament()) {
				try {
					arena.setGame(gameManager.createGame(arena));
				} catch (GameException e) {
					getLogger().warning(e.getMessage());
				}
			}
		}
		
		// Start next tournament countdown
		runNextTournamentCountdown();
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
		
		// Save players to the database
		Set<GamePlayer> toSave = new HashSet<>();

		toSave.addAll(playerManager.getPlayers());
		
		for (GamePlayer gp : toSave) {
			dataSource.savePlayer(gp);
		}
		
		dataSource.disconnect();
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
	
	public SignManager getSignManager() {
		
		return signManager;
	}
	
	public ArenaConfiguration getArenaConfig() {
		
		return arenaConfiguration;
	}
	
	public SignConfiguration getSignConfig() {
		
		return signConfiguration;
	}
	
	public DataSource getDataSource() {
		
		return dataSource;
	}
	
	public ClassSelectorMenu getClassSelectorMenu() {
		
		return classSelectorMenu;
	}
	
	public UpgradesMenu getUpgradesMenu() {
		
		return upgradesMenu;
	}
	
	public LayoutEditorMenu getLayoutEditorMenu() {
		
		return layoutEditorMenu;
	}
	
	public ShopMenu getShopMenu() {
		
		return shopMenu;
	}
	
	public PlayMenu getPlayMenu() {
		
		return playMenu;
	}
	
	public SpectateMenu getSpectateMenu() {
		
		return spectateMenu;
	}
	
	public NMSUtils getNMSUtils() {
		
		return nmsUtils;
	}
	
	public int getNextTournamentCountdownTime() {
		
		return nextTournamentCountdownTime;
	}
	
	public void runNextTournamentCountdown() {
		
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, () -> {

            nextTournamentCountdownTime--;

            if (nextTournamentCountdownTime == 0) {
                for (Arena arena : arenaManager.getArenas()) {
                    if (arena.isTournament()) {
                        try {
                            arena.setGame(gameManager.createGame(arena));
                        } catch (GameException e) {
                            getLogger().warning(e.getMessage());
                        }

                        break;
                    }
                }
            }
        }, 0L, 20L);
	}
}