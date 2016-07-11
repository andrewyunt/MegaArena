package com.andrewyunt.arenaplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPluginPlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		try {
			ArenaPlugin.getInstance().getPlayerManager().createPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
			// player is already in the plugin's records, so do nothing
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		ItemStack item = event.getItem();
		
		if(item == null || !item.hasItemMeta())
			return;
		
		Material type = item.getType();
		
		if (!(type == Material.EMERALD || type == Material.COMMAND || type == Material.CHEST))
			return;
		
		ItemMeta meta = item.getItemMeta();
		
		String name = meta.getDisplayName();
		
		if (name.equals(ChatColor.GREEN + "Shop")) {
			
		} else if (name.equals(ChatColor.YELLOW + "Layout Editor")) {
			
		} else if (name.equals(ChatColor.RED + "Class Selector")) {
			
		} else if (name.equals("Play : Team-deathmatch")) {
			
		} else if (name.equals("Play : Free-for-all")) {
			
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
	}
}