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
	private ClassType classType;
	private boolean hasFallen;
	private Arena selectedArena;
	private double previousHealth;
	private Side side;
	private int energy;
	
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
	
	public void setClassType(ClassType classType) {
		
		this.classType = classType;
	}
	
	public ClassType getClassType() {
		
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
		
		getBukkitPlayer().sendMessage(String.format(ChatColor.GOLD + "You have selected the arena %s", selectedArena.getName()));
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
	
	public int getClassLevel(ClassType type) {
		
		Player bp = getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("arenaplugin.%s.%f", type.toString().toLowerCase(), i)))
				return i;
		
		/* Somehow the player doesn't have permissions for any class level including 1,
		so set their class level to 1 as a default */
		return 1; 
	}
	
	public double getPreviousHealth() {
		
		return previousHealth;
	}
	
	public void setPreviousHealth(double previousHealth) {
		
		this.previousHealth = previousHealth;
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
		
		giveItems();
		
		player.teleport(loc);
		player.sendMessage(String.format(ChatColor.GOLD + "You have spawned at %s.", 
				String.format("X:%s Y:%s Z:%s world: %s", loc.getX(), loc.getY(), loc.getZ(), loc.getWorld())));
	}
	
	public void giveItems() {
		
		getBukkitPlayer().getInventory().setContents(classType.getItems());
	}
	
	public void addEnergy(int energy) {
		
		this.energy = this.energy + energy;
	}
	
	public int getEnergy() {
		
		return this.energy;
	}
}