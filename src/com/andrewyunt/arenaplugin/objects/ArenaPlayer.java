package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zencode.shortninja.staffplus.StaffPlus;

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
	
	public void setHotBar() {
		
		Player player = getBukkitPlayer();
		
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
		shopMeta.setDisplayName(ChatColor.GREEN + "Class Upgrades");
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
		
		getBukkitPlayer().sendMessage(String.format(ChatColor.GOLD + "You have joined the %s side.", side.toString()));
	}
	
	public void spawn(Spawn spawn) {
		
		Player player = getBukkitPlayer();
		Location loc = spawn.getLocation();
		
		player.setHealth(40);
		setEnergy(0);
		
		giveKitItems();
		
		player.teleport(loc);
		
		player.sendMessage(String.format(ChatColor.GOLD + "You have spawned at %s.", 
				String.format("X:%s Y:%s Z:%s world: %s", loc.getX(), loc.getY(), loc.getZ(), loc.getWorld())));
	}
	
	public void giveKitItems() {
		
		getBukkitPlayer().getInventory().setContents(classType.getKitItems(this));
	}
	
	public void addEnergy(int energy) {
		
		setEnergy(this.energy + energy);
	}
	
	public void setEnergy(int energy) {
		
		this.energy = energy;
		
		if (this.energy > 100)
			this.energy = 100;
		
		getBukkitPlayer().setExp(this.energy);
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
}