package com.andrewyunt.arenaplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

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
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		ap.getGame().removePlayer(ap);
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
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		ArenaPlayer player = null; 
		
		try {
			player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}
		
		String message = event.getMessage();
		
		if (message.startsWith("/tp")) {
		
			if (!(player.isStaffMode())) {
				player.getBukkitPlayer().sendMessage(ChatColor.RED + "You must enter staff mode before using that command.");
				player.getBukkitPlayer().sendMessage(ChatColor.RED + "Usage: /staff");
				event.setCancelled(true);
			}
			
		} else if (message.startsWith("/staff")) {
		
			if (player.isInGame()) {
				player.getBukkitPlayer().sendMessage(ChatColor.RED + "You cannot use that command while in-game.");
				event.setCancelled(true);
			}
		}
	}
}