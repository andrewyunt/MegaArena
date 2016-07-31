package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.zencode.shortninja.staffplus.StaffPlus;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.ArenaException;
import com.andrewyunt.arenaplugin.objects.Game.Side;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPlayer {
	
	private String name;
	private Game game;
	private ArenaPlayer requestingPlayer;
	private Class classType;
	private boolean hasFallen;
	private Arena selectedArena;
	private double previousHealth;
	private float previousExp;
	private int previousLevel;
	private int previousFoodLevel;
	private GameMode previousGameMode;
	private ItemStack[] previousContents;
	private Side side;
	private int energy;
	private Location location;
	
	public ArenaPlayer(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public Player getBukkitPlayer() {
		
		return Bukkit.getServer().getPlayer(name);
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
	
	public ArenaPlayer getRequestingPlayer() {
		
		return requestingPlayer;
	}
	
	public void setRequestingPlayer(ArenaPlayer requestingPlayer) {
		
		this.requestingPlayer = requestingPlayer;
	}
	
	public boolean hasDuelRequest() {
		
		return requestingPlayer != null;
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
		
		return StaffPlus.get().mode.isActive(name);
	}
	
	public void updateHotBar() {
		
		Player player = getBukkitPlayer();
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
	
	public double getPreviousHealth() {
		
		return previousHealth;
	}
	
	public void setPreviousHealth(double previousHealth) {
		
		this.previousHealth = previousHealth;
	}
	
	public float getPreviousExp() {
		
		return previousExp;
	}
	
	public void setPreviousExp(float previousExp) {
		
		this.previousExp = previousExp;
	}
	
	public int getPreviousLevel() {
		
		return previousLevel;
	}
	
	public void setPreviousLevel(int previousLevel) {
		
		this.previousLevel = previousLevel;
	}
	
	public Side getSide() {
		
		return side;
	}
	
	public void setSide(Side side) {
		
		this.side = side;
		
		getBukkitPlayer().sendMessage(String.format(ChatColor.GREEN + "You have joined the %s side.",
				ChatColor.AQUA + side.getName() + ChatColor.GREEN));
	}
	
	public void spawn(Spawn spawn) {
		
		Player player = getBukkitPlayer();
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
	
	public void setEnergy(int energy) {
		
		this.energy = energy;
		
		if (this.energy > 100)
			this.energy = 100;
		
		getBukkitPlayer().setLevel(this.energy);
		getBukkitPlayer().setExp(this.energy / 100.0F);
	}
	
	public int getEnergy() {
		
		return this.energy;
	}

	public void setPreviousLocation(Location location) {
		
		this.location = location;
	}
	
	public Location getPreviousLocation() {
		
		return location;
	}
	
	public void setPreviousFoodLevel(int previousFoodLevel) {
		
		this.previousFoodLevel = previousFoodLevel;
	}
	
	public int getPreviousFoodLevel() {
		
		return previousFoodLevel;
	}
	
	public void setPreviousGameMode(GameMode previousGameMode) {
		
		this.previousGameMode = previousGameMode;
	}
	
	public GameMode getPreviousGameMode() {
		
		return previousGameMode;
	}
	
	public void setPreviousContents(ItemStack[] previousContents) {
		
		this.previousContents = previousContents;
	}
	
	public ItemStack[] getPreviousContents() {
		
		return previousContents;
	}
	
	public void addCoins(double coins) {
		
		ArenaPlugin.getInstance().getEconomy().depositPlayer(getBukkitPlayer(), coins);
	}
	
	public void removeCoins(double coins) {
		
		ArenaPlugin.getInstance().getEconomy().withdrawPlayer(getBukkitPlayer(), coins);
	}
	
	public double getCoins() {
		
		return ArenaPlugin.getInstance().getEconomy().getBalance(getBukkitPlayer());
	}
}