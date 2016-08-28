/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reerved.
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
package com.andrewyunt.megaarena.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.ArenaException;

import net.shortninja.staffplus.StaffPlus;

/**
 * The class used to store player's information.
 * 
 * @author Andrew Yunt
 */
public class GamePlayer {
	
	private String name;
	private Game game;
	private List<GamePlayer> requestingPlayers = new ArrayList<GamePlayer>();
	private Class classType;
	private boolean hasFallen, sentActivate = false;
	private Arena selectedArena;
	private GameMode previousGameMode;
	private GameSide side;
	private int energy;
	private Set<GamePlayer> assistPlayers = new HashSet<GamePlayer>();
	private boolean hasSpeed;
	private int coins = 0;
	private int earnedCoins = 0;
	private int kills = 0;
	private int killStreak = 0;
	private DamageCause lastDamageCause;
	private boolean acceptingDuels = true;
	private DisplayBoard displayBoard = null;
	private List<GamePlayer> skullHitPlayers = new ArrayList<GamePlayer>();
	
	public GamePlayer(String name) {
		
		/* Set variables */
		this.name = name;
		
		Server server = MegaArena.getInstance().getServer();
		
		/* Set player's name prefix */
		server.dispatchCommand(server.getConsoleSender(), 
				String.format("nte player %s prefix %s", name, "&f"));
		
		/* Set up scoreboard */
		String title = ChatColor.AQUA + "" + ChatColor.BOLD + "MegaArena";
		
		displayBoard = new DisplayBoard(getBukkitPlayer(), title);
		
        server.getScheduler().scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
        	ChatColor curTitleColor = ChatColor.AQUA;
        	
            @Override
            public void run() {
            	
            	ChatColor newTitleColor = curTitleColor == ChatColor.AQUA ? ChatColor.WHITE : ChatColor.AQUA;
            	
            	displayBoard.setTitle(newTitleColor + "" + ChatColor.BOLD + "MegaArena");
            	
            	curTitleColor = newTitleColor;
            }
        }, 0L, 20L);
	}
	
	public String getName() {
		
		return name;
	}
	
	public Player getBukkitPlayer() {
		
		return MegaArena.getInstance().getServer().getPlayer(name);
	}
	
	public Game getGame() {
		
		return game;
	}
	
	public void setGame(Game game) {
		
		this.game = game;
	}
	
	public boolean isInGame() {
		
		return game != null;
	}
	
	public GamePlayer getLastRequestingPlayer() {
		
		int size = requestingPlayers.size();
		
		if (size < 1)
			return null;
		
		return requestingPlayers.get(size - 1);
	}
	
	public List<GamePlayer> getRequestingPlayers() {
		
		return requestingPlayers;
	}
	
	public void addRequestingPlayer(GamePlayer player) {
		
		requestingPlayers.add(player);
	}
	
	public void removeRequestingPlayer(GamePlayer player) {
		
		requestingPlayers.remove(player);
	}
	
	public boolean hasRequestingPlayer() {
		
		return requestingPlayers.size() > 0;
	}
	
	public void setClassType(Class classType) {
		
		this.classType = classType;
	}
	
	public Class getClassType() {
		
		return classType;
	}
	
	public boolean hasSelectedClass() {
		
		return classType != null;
	}
	
	public void setHasFallen(boolean hasFallen) {
		
		this.hasFallen = hasFallen;
	}
	
	public boolean hasFallen() {
		
		return hasFallen;
	}
	
	public void selectArena(Arena selectedArena) {
		
		this.selectedArena = selectedArena;
	}
	
	public boolean hasSelectedArena() {
		
		return selectedArena != null;
	}
	
	public Arena getSelectedArena() throws ArenaException {
		
		if (selectedArena == null)
			throw new ArenaException("The player has not selected an arena");
		
		return selectedArena;
	}
	
	public boolean isStaffMode() {
		
		return StaffPlus.get().modeCoordinator.isInMode(getBukkitPlayer().getUniqueId());
	}
	
	public void updateHotBar() {
		
		PlayerInventory inv = getBukkitPlayer().getInventory();
		
		inv.setHelmet(new ItemStack(Material.AIR));
		inv.setChestplate(new ItemStack(Material.AIR));
		inv.setLeggings(new ItemStack(Material.AIR));
		inv.setBoots(new ItemStack(Material.AIR));
		inv.clear();
		
		/* Create items */
		ItemStack shop = new ItemStack(Material.EMERALD);
		ItemStack layoutEditor = new ItemStack(Material.CHEST);
		ItemStack classSelector = new ItemStack(Material.COMMAND);
		ItemStack playFFA = new ItemStack(Material.IRON_SWORD);
		ItemStack playTDM = new ItemStack(Material.DIAMOND_SWORD);
		
		/* Get item metas */
		ItemMeta shopMeta = shop.getItemMeta();
		ItemMeta layoutEditorMeta = layoutEditor.getItemMeta();
		ItemMeta classSelectorMeta = classSelector.getItemMeta();
		ItemMeta playFFAMeta = playFFA.getItemMeta();
		ItemMeta playTDMMeta = playTDM.getItemMeta();
		
		/* Set meta display names */
		shopMeta.setDisplayName(ChatColor.GREEN + "Shop");
		layoutEditorMeta.setDisplayName(ChatColor.YELLOW + "Layout Editor");
		classSelectorMeta.setDisplayName(ChatColor.RED + "Class Selector");
		playFFAMeta.setDisplayName("Play : Free-for-all");
		playTDMMeta.setDisplayName("Play : Team-deathmatch");
		
		/* Set item metas */
		shop.setItemMeta(shopMeta);
		layoutEditor.setItemMeta(layoutEditorMeta);
		classSelector.setItemMeta(classSelectorMeta);
		playFFA.setItemMeta(playFFAMeta);
		playTDM.setItemMeta(playTDMMeta);
		
		/* Set items in player's inventory */
		getBukkitPlayer().getInventory().setItem(0, shop);
		getBukkitPlayer().getInventory().setItem(1, layoutEditor);
		getBukkitPlayer().getInventory().setItem(2, classSelector);
		getBukkitPlayer().getInventory().setItem(7, playFFA);
		getBukkitPlayer().getInventory().setItem(8, playTDM);
	}
	
	public GameSide getSide() {
		
		return side;
	}
	
	public void setSide(GameSide side) {
		
		this.side = side;
	}
	
	public void spawn(Spawn spawn) {
		
		Location loc = spawn.getLocation().clone();
		
		getBukkitPlayer().setMaxHealth(40D);
		getBukkitPlayer().setHealth(40D);
		getBukkitPlayer().setFoodLevel(20);
		setEnergy(0);
		getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
		
		getBukkitPlayer().getInventory().clear();
		classType.giveKitItems(this);
		
		Chunk chunk = loc.getChunk();
		
		if (!chunk.isLoaded())
			chunk.load();
		
		loc.setY(loc.getY() + 1);
		
		getBukkitPlayer().teleport(loc, TeleportCause.COMMAND);
	}
	
	public void addEnergy(int energy) {
		
		setEnergy(this.energy + energy);
	}
	
	public void removeEnergy(int energy) {
		
		setEnergy(this.energy - energy);
	}
	
	public void setEnergy(int energy) {
		
		this.energy = energy;
		
		if (this.energy > 100)
			this.energy = 100;
		else
			sentActivate = false;
		
		if (this.energy == 100)
			if (!sentActivate) {
				sentActivate = true;
				if (classType == Class.SKELETON)
					getBukkitPlayer().sendMessage(ChatColor.AQUA + "Left click " + ChatColor.GREEN +
							"using your bow to activate your ability!");
				else
					getBukkitPlayer().sendMessage(ChatColor.AQUA + "Right click " + ChatColor.GREEN +
						"using your sword to activate your ability!");
			}
		
		getBukkitPlayer().setLevel(this.energy);
		getBukkitPlayer().setExp(this.energy / 100.0F);
	}
	
	public int getEnergy() {
		
		return this.energy;
	}
	
	public void setPreviousGameMode(GameMode previousGameMode) {
		
		this.previousGameMode = previousGameMode;
	}
	
	public GameMode getPreviousGameMode() {
		
		return previousGameMode;
	}
	
	public void addCoins(int coins) {
		
		setCoins(this.coins + coins);
		
		setEarnedCoins(this.earnedCoins + coins);
	}
	
	public void removeCoins(int coins) {
		
		setCoins(this.coins - coins);
	}
	
	public void setCoins(double coins) {
		
		this.coins = ((Double) coins).intValue();
		
		updateScoreboard();
	}
	
	public int getCoins() {
		
		return coins;
	}
	
	public void setEarnedCoins(double earnedCoins) {
		
		this.earnedCoins = ((Double) earnedCoins).intValue();
	}
	
	public int getEarnedCoins() {
		
		return earnedCoins;
	}
	
	public void addAssistPlayer(GamePlayer player) {
		
		assistPlayers.add(player);
		
        BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
            @Override
            public void run() {
            	
            	assistPlayers.remove(player);
            }
        }, 200L);
	}
	
	public Set<GamePlayer> getAssistPlayers() {
		
		return assistPlayers;
	}
	
	public void setHasSpeed(boolean hasSpeed) {
		
		this.hasSpeed = hasSpeed;
	}
	
	public boolean hasSpeed() {
		
		return hasSpeed;
	}
	
	/**
	 * Sets the level of an Upgradable for the specified player.
	 * 
	 * @return
	 * 		The Upgradable level (1-9) of the specified player.
	 */
	public int getLevel(Upgradable upgradable) {
		
		Player bp = getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("megaarena.%s.%s", upgradable.toString().toLowerCase(), i)))
				return i;
		
		return 1;
	}
	
	/**
	 * Sets the Upgradable level for the specified player to the specified level.
	 * 
	 * <p>
	 * The level integer must be from 1-9 inclusive.
	 * </p>
	 * 
	 * @param level
	 * 		The level to set the player's Upgradable to.
	 */
	public void setLevel(Upgradable upgradable, int level) {
		
		MegaArena.getInstance().getServer().dispatchCommand(MegaArena.getInstance().getServer().getConsoleSender(),
				String.format("pex user %s add megaarena.%s.%s", getBukkitPlayer().getName(), upgradable.toString().toLowerCase(), level));
	}
	
	public void addKill() {
		
		setKills(kills + 1);
		
		setKillStreak(killStreak + 1);
	}
	
	public void setKills(double kills) {
		
		this.kills = ((Double) kills).intValue();
		
		updateScoreboard();
	}
	
	public int getKills() {
		
		return kills;
	}
	
	public void setKillStreak(int killStreak) {
		
		this.killStreak = killStreak;
		
		updateScoreboard();
	}
	
	public int getKillStreak() {
		
		return killStreak;
	}
	
	public void setLastDamageCause(DamageCause lastDamageCause) {
		
		this.lastDamageCause = lastDamageCause;
	}
	
	public DamageCause getLastDamageCause() {
		
		return lastDamageCause;
	}
	
	public void setAcceptingDuels(boolean acceptingDuels) {
		
		this.acceptingDuels = acceptingDuels;
	}
	
	public boolean isAcceptingDuels() {
		
		return acceptingDuels;
	}
	
	public DisplayBoard getDisplayBoard() {
		
		return displayBoard;
	}
	
	public void updateScoreboard() {
		
		/* Clear current fields */
		displayBoard.clear();
		
		displayBoard.putField(ChatColor.RESET + " ");
		
		/* Display player's coins */
		displayBoard.putField(ChatColor.GOLD + "" + ChatColor.BOLD + "Coins");
		displayBoard.putField(String.valueOf(coins));
		
		/* Display player's kills */
		displayBoard.putField(ChatColor.RED + "" + ChatColor.BOLD + "Kills");
		displayBoard.putField(String.valueOf(kills));
		
		if (isInGame()) {
			displayBoard.putField(ChatColor.RESET + "  ");
			
			/* Display player's killstreak */
			displayBoard.putField(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Killstreak");
			displayBoard.putField(String.valueOf(killStreak));
			
			/* Display player's team */
			displayBoard.putField(ChatColor.GREEN + "" + ChatColor.BOLD + "Team");
			displayBoard.putField(side.getSideType().getName());
		}
		
		displayBoard.putField(ChatColor.RESET + "   ");
		
		/* Display server's IP */
		displayBoard.putField(ChatColor.YELLOW + "mc.amosita.net");
		
		/* Display board to player */
		displayBoard.display();
	}
	
	public List<GamePlayer> getSkullHitPlayers() {
		
		return skullHitPlayers;
	}
	
	public void addSkullHitPlayer(GamePlayer player) {
		
		skullHitPlayers.add(player);
		
        BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
            @Override
            public void run() {
            	
            	skullHitPlayers.remove(player);
            }
        }, 20L);
	}
}