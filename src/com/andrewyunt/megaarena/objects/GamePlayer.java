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
package com.andrewyunt.megaarena.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.zencode.shortninja.staffplus.StaffPlus;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.ArenaException;

/**
 * The class used to store player's information.
 * 
 * @author Andrew Yunt
 */
public class GamePlayer {
	
	private String name;
	private Player player;
	private Game game;
	private GamePlayer requestingPlayer;
	private Class classType;
	private boolean hasFallen, sentActivate = false;
	private Arena selectedArena;
	private Location previousLocation;
	private GameMode previousGameMode;
	private GameSide side;
	private int energy;
	private Set<GamePlayer> assistPlayers = new HashSet<GamePlayer>();
	private boolean hasSpeed;
	private int coins = 0;
	
	public GamePlayer(String name) {
		
		this.name = name;
		player = MegaArena.getInstance().getServer().getPlayer(name);
	}
	
	public String getName() {
		
		return name;
	}
	
	public Player getBukkitPlayer() {
		
		return player;
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
	
	public GamePlayer getRequestingPlayer() {
		
		return requestingPlayer;
	}
	
	public void setRequestingPlayer(GamePlayer requestingPlayer) {
		
		this.requestingPlayer = requestingPlayer;
	}
	
	public boolean hasDuelRequest() {
		
		return requestingPlayer != null;
	}
	
	public void setClassType(Class classType) {
		
		this.classType = classType;
		
		MegaArena.getInstance().getDataSource().saveClassType(this);
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
		
		return StaffPlus.get().mode.isActive(name);
	}
	
	public void updateHotBar() {
		
		PlayerInventory inv = player.getInventory();
		
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
		player.getInventory().setItem(0, shop);
		player.getInventory().setItem(1, layoutEditor);
		player.getInventory().setItem(2, classSelector);
		player.getInventory().setItem(7, playFFA);
		player.getInventory().setItem(8, playTDM);
	}
	
	public GameSide getSide() {
		
		return side;
	}
	
	public void setSide(GameSide side) {
		
		this.side = side;
	}
	
	public void spawn(Spawn spawn) {
		
		Location loc = spawn.getLocation();
		
		player.setMaxHealth(40D);
		player.setHealth(40D);
		player.setFoodLevel(20);
		setEnergy(0);
		player.setGameMode(GameMode.SURVIVAL);
		
		player.getInventory().clear();
		classType.giveKitItems(this);
		
		loc.setY(loc.getY() + 1);
		player.teleport(loc);
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
					player.sendMessage(ChatColor.AQUA + "Left click " + ChatColor.GREEN +
							"using your bow to activate your ability!");
				else
					player.sendMessage(ChatColor.AQUA + "Right click " + ChatColor.GREEN +
						"using your sword to activate your ability!");
			}
		
		player.setLevel(this.energy);
		player.setExp(this.energy / 100.0F);
	}
	
	public int getEnergy() {
		
		return this.energy;
	}

	public void setPreviousLocation(Location previousLocation) {
		
		this.previousLocation = previousLocation;
	}
	
	public Location getPreviousLocation() {
		
		return previousLocation;
	}
	
	public void setPreviousGameMode(GameMode previousGameMode) {
		
		this.previousGameMode = previousGameMode;
	}
	
	public GameMode getPreviousGameMode() {
		
		return previousGameMode;
	}
	
	public void addCoins(int coins) {
		
		setCoins(this.coins + coins);
	}
	
	public void removeCoins(int coins) {
		
		setCoins(this.coins - coins);
	}
	
	public void setCoins(int coins) {
		
		this.coins = coins;
		
		MegaArena.getInstance().getDataSource().saveCoins(this);
	}
	
	public double getCoins() {
		
		return MegaArena.getInstance().getEconomy().getBalance(player);
	}
	
	public void addAssistPlayer(GamePlayer player) {
		
		assistPlayers.add(player);
		
        BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
            @Override
            public void run() {
            	
            	assistPlayers.remove(player);
            }
        }, 400L);
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
		
		Player bp = player;
		
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
				String.format("pex user %s add megaarena.%s.%s", player.getName(), upgradable.toString().toLowerCase(), level));
	}
}