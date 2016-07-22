package com.andrewyunt.arenaplugin.listeners;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.menu.ClassSelectorMenu;
import com.andrewyunt.arenaplugin.objects.Arena;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.Game;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ArenaPluginPlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		ArenaPlayer player = null;
		
		try {
			player = ArenaPlugin.getInstance().getPlayerManager().createPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
			// player is already in the plugin's records, so do nothing
		}
		
		player.setHotBar();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (ap.isInGame())
			ap.getGame().removePlayer(ap);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK))
			return;
		
		ItemStack item = event.getItem();
		
		if(item == null || !item.hasItemMeta())
			return;
		
		Material type = item.getType();
		
		if (!(type == Material.EMERALD || type == Material.COMMAND || type == Material.CHEST))
			return;
		
		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName();
		Player player = event.getPlayer();
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (name.equals(ChatColor.GREEN + "Class Upgrades")) {
			
			//new UpgradesMenu(player);
		
		} else if (name.equals(ChatColor.YELLOW + "Layout Editor")) {
			
			player.sendMessage(ChatColor.RED + "The layout editor is coming soon!");
			
		} else if (name.equals(ChatColor.RED + "Class Selector")) {
			
			new ClassSelectorMenu(player);
		
		} else if (name.equals("Play : Team-deathmatch")) {
			
			List<Arena> arenas = (List<Arena>) ArenaPlugin.getInstance().getArenaManager().getArenas(ArenaType.TDM);
			Collections.shuffle(arenas);
			Arena arena = arenas.get(0);
			
			arena.getGame().addPlayer(ap);
			
		} else if (name.equals("Play : Free-for-all")) {
			
			List<Arena> arenas = (List<Arena>) ArenaPlugin.getInstance().getArenaManager().getArenas(ArenaType.FFA);
			Collections.shuffle(arenas);
			Arena arena = arenas.get(0);
			
			arena.getGame().addPlayer(ap);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		// to be used for layout editor
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
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		
		ArenaPlayer playerAP = null;
		ArenaPlayer damagerAP = null;
		
		try {
			playerAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
			
			if (event.getDamager() instanceof Player) {
				damagerAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
			}
		} catch (PlayerException e) {
		}
		
		if (event.getDamager() instanceof Arrow) {
			if (!(((Projectile) event.getDamager()).getShooter() instanceof Player))
				return;
			
			try {
				damagerAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(((Player) ((Projectile) event.getDamager()).getShooter()).getName());
				Class type = damagerAP.getClassType();
				
				switch (type) {
					case SKELETON:
						break;
					case ZOMBIE:
					case HEROBRINE:
					case CREEPER:
					case SPIRIT_WARRIOR:
					case WITHER_MINION:
						damagerAP.addEnergy(type.getEnergyPerClick());
				}
			} catch (PlayerException e) {
			}
		}
		
		if (!playerAP.isInGame() || !damagerAP.isInGame())
			return;
		
		if (playerAP.getGame() != damagerAP.getGame())
			return;
		
		if (playerAP.getGame().getArena().getType() == ArenaType.DUEL || playerAP.getGame().getArena().getType() == ArenaType.FFA) {
			if (damagerAP.getClassType() == Class.SKELETON)
				damagerAP.addEnergy(Class.SKELETON.getEnergyPerClick());
			return;
		}
		
		if (playerAP.getSide() != damagerAP.getSide()) {
			if (damagerAP.getClassType() == Class.SKELETON)
				damagerAP.addEnergy(Class.SKELETON.getEnergyPerClick());
			return;
		}
		
		event.setCancelled(true);
		player.sendMessage(ChatColor.RED + "You may not damage your teammates!");
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		
		ArenaPlayer player = null; 
		
		try {
			player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}
		
		if (!player.isInGame())
			return;
		
		Block block = event.getBlock();
		
		if (block.getType() != Material.COBBLESTONE) {
			event.setCancelled(true);
			return;
		}
		
        BukkitScheduler scheduler = ArenaPlugin.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(ArenaPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
            	
            	block.setType(Material.AIR);
            }
        }, 200L);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		ArenaPlayer player = null; 
		
		try {
			player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}
		
		if (!player.isInGame())
			return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
		Player player = (Player) event.getPlayer();
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (ap.isInGame()) {
			ap.getGame().spawnPlayer(ap, ap.getSide());
			return;
		}
		
		ap.getClassType().giveKitItems(ap);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (ap.isInGame()) {
			Game game = ap.getGame();
			
			if (ap.getGame().getArena().getType() == ArenaType.DUEL)
				game.end();
		}
	}
}