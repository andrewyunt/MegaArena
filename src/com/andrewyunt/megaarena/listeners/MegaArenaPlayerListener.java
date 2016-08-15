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
package com.andrewyunt.megaarena.listeners;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.menu.ClassSelectorMenu;
import com.andrewyunt.megaarena.menu.ShopMenu;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.GamePlayer;

/**
 * The listener class used for general event handling within the plugin
 * which holds methods to listen on events.
 * 
 * @author Andrew Yunt
 */
public class MegaArenaPlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		GamePlayer player = null;

		try {
			player = MegaArena.getInstance().getPlayerManager().createPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
			try {
				player = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
			} catch (PlayerException e1) {
			}
		}

		player.updateHotBar();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		GamePlayer gp = null;

		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		if (!gp.isInGame())
			return;
		
		Game game = gp.getGame();
		
		if (game.getArena().getType() == Arena.Type.DUEL) {
			MegaArena.getInstance().getGameManager().deleteGame(game, 
					String.format("%s has left the game and has left you victorious!",
					ChatColor.AQUA + gp.getName() + ChatColor.GREEN));
			return;
		}
		
		try {
			game.removePlayer(gp);
		} catch (PlayerException e) {
		}
		
		try {
			MegaArena.getInstance().getPlayerManager().deletePlayer(gp);
		} catch (PlayerException e) {
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK))
			return;

		ItemStack item = event.getItem();

		if (item == null || !item.hasItemMeta())
			return;

		Material type = item.getType();

		if (!(type == Material.EMERALD || type == Material.COMMAND || type == Material.CHEST
				|| type == Material.DIAMOND_SWORD || type == Material.IRON_SWORD))
			return;

		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName();
		Player player = event.getPlayer();
		GamePlayer gp = null;

		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		if (name.equals(ChatColor.GREEN + "Shop")) {

			new ShopMenu(player);

		} else if (name.equals(ChatColor.YELLOW + "Layout Editor")) {

			player.sendMessage(ChatColor.RED + "The layout editor is coming soon!");

		} else if (name.equals(ChatColor.RED + "Class Selector")) {

			new ClassSelectorMenu(player);

		} else if (name.equals("Play : Team-deathmatch")) {

			try {
				MegaArena.getInstance().getGameManager().matchMake(gp, Arena.Type.TDM);
			} catch (GameException e) {
			}

		} else if (name.equals("Play : Free-for-all")) {

			try {
				MegaArena.getInstance().getGameManager().matchMake(gp, Arena.Type.FFA);
			} catch (GameException e) {
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		GamePlayer player = null;

		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}

		String message = event.getMessage();

		if (message.startsWith("/tp") && !(message.equalsIgnoreCase("/tps"))) {

			if (!(player.isStaffMode())) {
				player.getBukkitPlayer()
						.sendMessage(ChatColor.RED + "You must enter staff mode before using that command.");
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
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(((Player) event.getEntity()).getName());
		} catch (PlayerException e) {
		}
		
		if (!gp.isInGame())
			return;
		
		if (gp.getGame().getArena().getType() == Arena.Type.DUEL)
			return;
		
		if (event.getCause() != DamageCause.FALL)
			return;
		
		if (gp.hasFallen())
			return;
		
		gp.setHasFallen(true);
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();

		if (!(damaged instanceof Player) || (!(damager instanceof Player) && !(damager instanceof Projectile)))
			return;

		PlayerManager playerManager = MegaArena.getInstance().getPlayerManager();
		Player damagerPlayer = null;

		if (damager instanceof Projectile) {
			if (!(((Projectile) damager).getShooter() instanceof Player))
				return;

			damagerPlayer = (Player) ((Projectile) damager).getShooter();
		} else
			damagerPlayer = (Player) damager;

		Player damagedPlayer = (Player) damaged;
		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = playerManager.getPlayer(damagerPlayer.getName());
			damagedGP = playerManager.getPlayer(damagedPlayer.getName());
		} catch (PlayerException e) {
		}

		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagedGP.getGame() != damagerGP.getGame())
			return;

		if (damagedGP.getGame().getArena().getType() != Arena.Type.TDM)
			return;

		if (damagedGP.getSide() != damagerGP.getSide())
			damagedGP.addAssistPlayer(damagerGP);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		GamePlayer player = null;

		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}

		if (!player.isInGame())
			return;

		Block block = event.getBlock();

		if (block.getType() != Material.COBBLESTONE) {
			event.setCancelled(true);
			return;
		}

		player.getGame().addPlacedBlock(block);

		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
			@Override
			public void run() {

				block.setType(Material.AIR);
			}
		}, 200L);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		GamePlayer player = null;

		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}

		if (!player.isInGame())
			return;

		if (player.getGame().getPlacedBlocks().contains(event.getBlock()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerKill(PlayerDeathEvent event) {
		
		Player player = event.getEntity();

		if (player.getKiller() == null || !(player.getKiller() instanceof Player))
			return;

		Player killer = player.getKiller();
		GamePlayer playerGP = null;
		GamePlayer killerGP = null;

		try {
			playerGP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
			killerGP = MegaArena.getInstance().getPlayerManager().getPlayer(killer.getName());
		} catch (PlayerException e) {
		}
		
		if (!(playerGP.isInGame()))
			return;
		
		Game game = playerGP.getGame();
		
		if (game.getArena().getType() == Arena.Type.DUEL)
			return;

		if (killerGP.isInGame()) {
			int killCoins = 50;

			if (killer.hasPermission("megaarena.coins.double"))
				killCoins = 100;
			else if (killer.hasPermission("megaarena.coins.triple"))
				killCoins = 150;
			
			killerGP.addCoins(killCoins);
			killer.sendMessage(ChatColor.GREEN + String.format("You killed %s and received %s coins.",
					ChatColor.AQUA + player.getName() + ChatColor.GREEN,
					ChatColor.AQUA + String.valueOf(killCoins) + ChatColor.GREEN));
		}

		if (game.getArena().getType() == Arena.Type.TDM)
			for (GamePlayer assistAP : playerGP.getAssistPlayers()) {
				if (!assistAP.isInGame())
					continue;

				Player assistPlayer = assistAP.getBukkitPlayer();
				int assistCoins = 15;

				if (assistPlayer.hasPermission("megaarena.coins.double"))
					assistCoins = 30;
				else if (assistPlayer.hasPermission("megaarena.coins.triple"))
					assistCoins = 45;

				assistAP.addCoins(assistCoins);
				assistPlayer.sendMessage(
						ChatColor.GREEN + String.format("You earned %s coins for assisting the kill of %s.",
								ChatColor.AQUA + String.valueOf(assistCoins) + ChatColor.GREEN,
								ChatColor.AQUA + player.getName() + ChatColor.GREEN));
			}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		event.setKeepInventory(true);
		
		Player player = event.getEntity();
		GamePlayer playerGP = null;

		try {
			playerGP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (!playerGP.isInGame())
			return;
		
		Game game = playerGP.getGame();
		
		if (playerGP.getGame().getArena().getType() == Arena.Type.DUEL) {
			GamePlayer opponentGP = null;
			
			for (GamePlayer curGP : game.getPlayers())
				if (curGP != playerGP)
					opponentGP = curGP;
			
			MegaArena.getInstance().getGameManager().deleteGame(game,
					String.format(ChatColor.AQUA + "%s suffered a bitter defeat to %s.",
							player.getName() + ChatColor.GREEN, ChatColor.AQUA + opponentGP.getName() + ChatColor.GREEN));
			return;
		}
		
		try {
			playerGP.getGame().removePlayer(playerGP);
		} catch (PlayerException e) {
		}
	}
	
	@EventHandler
	public void onDeathMessage(PlayerDeathEvent event) {
		
		Player killed = event.getEntity();
		Player killer = null;
		GamePlayer killedGP = null;
		
		try {
			killedGP = MegaArena.getInstance().getPlayerManager().getPlayer(killed.getName());
		} catch (PlayerException e) {
		}
		
		ChatColor killedColor = ChatColor.GRAY;
		
		if (!killedGP.isInGame()) {
			event.setDeathMessage("");
			return;
		} else
			killedColor = killedGP.getSide().getSideType().getNameColor();
		
		EntityDamageEvent entityDamageEvent = killed.getLastDamageCause();
		ConfigurationSection deathMessagesSection = MegaArena.getInstance().getConfig()
				.getConfigurationSection("death-messages");
		List<String> msgList = null;
		String msg = null;
		ChatColor killerColor = ChatColor.GRAY;
		
		if (entityDamageEvent.getEntityType() == EntityType.PLAYER) {
			killer = event.getEntity().getKiller();
			GamePlayer killerGP = null;
			
			try {
				killerGP = MegaArena.getInstance().getPlayerManager().getPlayer(killer.getName());
			} catch (PlayerException e) {
			}
			
			if (killerGP.isInGame())
				killerColor = killerGP.getSide().getSideType().getNameColor();
			
			Material tool = killer.getItemInHand().getType();
			
			if (tool == Material.IRON_SWORD || tool == Material.DIAMOND_SWORD ||
					tool == Material.STONE_SWORD || tool == Material.BOW) {
				msgList = deathMessagesSection.getStringList(tool.toString().toLowerCase());
				Collections.shuffle(msgList);
				msg = msgList.get(0);
			} else {
				msgList = deathMessagesSection.getStringList("melee");
				Collections.shuffle(msgList);
				msg = msgList.get(0);
			}
		} else {	
			killed.sendMessage(ChatColor.GRAY + event.getDeathMessage());
			return;
		}
		
		event.setDeathMessage("");
		
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		msg = String.format(msg, killerColor + killer.getName(), killedColor + killed.getName());
		
		killer.sendMessage(msg);
		killed.sendMessage(msg);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {

		event.setCancelled(true);
	}
}