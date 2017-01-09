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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
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
	
	private final String name;
	private final Set<GamePlayer> assistPlayers = new HashSet<GamePlayer>();
	private final List<GamePlayer> requestingPlayers = new ArrayList<GamePlayer>();
	private final List<GamePlayer> skullHitPlayers = new ArrayList<GamePlayer>();
	private final Map<Upgradable, Integer> upgradeLevels = new HashMap<Upgradable, Integer>();
	
	private Game game;
	private Class classType;
	private DamageCause lastDamageCause;
	private Arena selectedArena;
	private GameMode previousGameMode;
	private GameSide side;
	private DisplayBoard displayBoard = null;
	private GamePlayer lastDamager = null;
	private boolean hasSpeed = false, sentActivate = false, loaded = false, hasBloodEffect = false,
			hasFallen = false, acceptingDuels = true, epcCooldown = false, bowCooldown = false;
	private int coins = 0, earnedCoins = 0, kills = 0, killStreak = 0, energy = 0;
	
	public GamePlayer(String name) {
		
		/* Set variables */
		this.name = name;
		
		/* Load upgradable levels */
		for (Class classType : Class.values()) {
			int level = MegaArena.getInstance().getDataSource().getLevel(this, classType);
			upgradeLevels.put(classType, level);
		}
		
		for (Skill skillType : Skill.values()) {
			int level = MegaArena.getInstance().getDataSource().getLevel(this, skillType);
			upgradeLevels.put(skillType, level);
		}
		
		for (Ability abilityType : Ability.values()) {
			int level = MegaArena.getInstance().getDataSource().getLevel(this, abilityType);
			upgradeLevels.put(abilityType, level);
		}
		
		/* Get the scheduler */
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		
		/* Repeating task to remove withering */
		OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(name);
		
		scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
			@Override
			public void run() {
				
				if (!op.isOnline())
					return;
				
				getBukkitPlayer().removePotionEffect(PotionEffectType.WITHER);
			}
		}, 0L, 20L);
		
		/* Set up scoreboard */
		String title = ChatColor.AQUA + "" + ChatColor.BOLD + "MegaArena";
		
		displayBoard = new DisplayBoard(getBukkitPlayer(), title);
		
		scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
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
	
	public void setFallen(boolean hasFallen) {
		
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
	
	public void updateHotbar() {
		
		PlayerInventory inv = getBukkitPlayer().getInventory();
		
		inv.setHelmet(new ItemStack(Material.AIR));
		inv.setChestplate(new ItemStack(Material.AIR));
		inv.setLeggings(new ItemStack(Material.AIR));
		inv.setBoots(new ItemStack(Material.AIR));
		inv.clear();
		
		for (Map.Entry<Integer, ItemStack> entry : MegaArena.getInstance().getHotbarItems().entrySet()) {
			int pos = entry.getKey();
			ItemStack is = entry.getValue();
			inv.setItem(pos, is);
		}
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
	
	public void setCoins(int coins) {
		
		this.coins = coins;
		
		updateScoreboard();
	}
	
	public int getCoins() {
		
		return coins;
	}
	
	public void setEarnedCoins(int earnedCoins) {
		
		this.earnedCoins = earnedCoins;
	}
	
	public int getEarnedCoins() {
		
		return earnedCoins;
	}
	
	public void addAssistPlayer(GamePlayer player) {
		
		assistPlayers.add(player);
		
        BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> assistPlayers.remove(player), 200L);
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
	
	public void addKill() {
		
		setKills(kills + 1);
		
		setKillStreak(killStreak + 1);
	}
	
	public void setKills(int kills) {
		
		this.kills = kills;
		
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
        scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> skullHitPlayers.remove(player), 20L);
	}
	
	public void setLoaded(boolean loaded) {
		
		this.loaded = loaded;
	}
	
	public boolean isLoaded() {
		
		return loaded;
	}
	
	public void setBloodEffect(boolean hasBloodEffect) {
		
		this.hasBloodEffect = hasBloodEffect;
	}
	
	public boolean hasBloodEffect() {
		
		return hasBloodEffect;
	}
	
	public void setClassLevel(Upgradable upgradable, int level) {
		
		if (upgradeLevels.containsKey(upgradable))
			upgradeLevels.remove(upgradable);
		
		upgradeLevels.put(upgradable, level);
	}
	
	public int getLevel(Upgradable upgradable) {
		
		if (upgradeLevels.containsKey(upgradable))
			return upgradeLevels.get(upgradable);
		
		return 1;
	}
	
	public Map<Upgradable, Integer> getUpgradeLevels() {
		
		return upgradeLevels;
	}
	
	public void kill() {
		
		if (lastDamager == null)
			return;
		
		if (lastDamager.getName().equals(name))
			return;
			
		if (!(lastDamager.isInGame()))
			return;
		
		lastDamager.addKill();
		
		Game game = lastDamager.getGame();
		
		if (game.getArena().getType() == Arena.Type.DUEL)
			return;
		
		Player killerBP = lastDamager.getBukkitPlayer();
		Damageable kLD = ((Damageable) killerBP);
		double previousHealth = kLD.getHealth();
		
		if (previousHealth + 4 > kLD.getMaxHealth())
			kLD.setHealth(kLD.getMaxHealth());
		else
			kLD.setHealth(kLD.getHealth() + 4);
		
		killerBP.sendMessage(ChatColor.LIGHT_PURPLE + String.format("You restored 2 hearts for killing %s.",
				getBukkitPlayer().getDisplayName()));
		
		int killCoins = 12;
		
		if (killerBP.hasPermission("megaarena.coins.double"))
			killCoins = 24;
		
		if (killerBP.hasPermission("megaarena.coins.triple"))
			killCoins = 36;
			
		lastDamager.addCoins(killCoins);
		killerBP.sendMessage(ChatColor.GREEN + String.format("You killed %s and received %s coins.",
				ChatColor.AQUA + name + ChatColor.GREEN,
				ChatColor.AQUA + String.valueOf(killCoins) + ChatColor.GREEN));
		
		if (lastDamager.getKillStreak() > 1) {
			int killStreakCoins = lastDamager.getKillStreak();
			
			if (killerBP.hasPermission("megaarena.coins.triple"))
				killStreakCoins = killStreakCoins * 3;
			else if (killerBP.hasPermission("megaarena.coins.double"))
				killStreakCoins = killStreakCoins * 2;
			
			lastDamager.addCoins(killStreakCoins);
			killerBP.sendMessage(ChatColor.GREEN + String.format("You earned %s coins for a killstreak of %s.",
					ChatColor.AQUA + String.valueOf(killStreakCoins) + ChatColor.GREEN,
					ChatColor.AQUA + String.valueOf(lastDamager.getKillStreak()) + ChatColor.GREEN));
		}
		
		for (GamePlayer assistGP : assistPlayers) {
			if (assistGP.getName().equals(lastDamager.getName()))
				continue;
			
			if (!assistGP.isInGame())
				continue;

			Player assistPlayer = assistGP.getBukkitPlayer();
			int assistCoins = 6;

			if (assistPlayer.hasPermission("megaarena.coins.double"))
				assistCoins = 12;
			
			if (assistPlayer.hasPermission("megaarena.coins.triple"))
				assistCoins = 18;

			assistGP.addCoins(assistCoins);
			assistPlayer.sendMessage(
					ChatColor.GREEN + String.format("You earned %s coins for assisting the kill of %s.",
							ChatColor.AQUA + String.valueOf(assistCoins) + ChatColor.GREEN,
							ChatColor.AQUA + name + ChatColor.GREEN));
		}
	}
	
	public void setLastDamager(GamePlayer lastDamager) {
		
		this.lastDamager = lastDamager;
	}
	
	public GamePlayer getLastDamager() {
		
		return lastDamager;
	}
	
	public void setEPCCooldown(boolean cooldown) {
		
		this.epcCooldown = cooldown;
	}
	
	public boolean isEPCCooldown() {
		
		return epcCooldown;
	}
	
	public void setBowCooldown(boolean bowCooldown) {
		
		this.bowCooldown = bowCooldown;
	}
	
	public boolean isBowCooldown() {
		
		return bowCooldown;
	}
}