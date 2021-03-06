/*
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

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.exception.SignException;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.Utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;
import java.util.List;

/**
 * The listener class used for general event handling within the plugin
 * which holds methods to listen on events.
 * 
 * @author Andrew Yunt
 */
public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		final Player bp = event.getPlayer();
		
		bp.setMaximumNoDamageTicks(0); // Part of the EPC
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> {
			GamePlayer player = null;
			
			// Get the player's GamePlayer object and if it doesn't exist, add it
			try {
				player = MegaArena.getInstance().getPlayerManager().createPlayer(bp.getName());
			} catch (PlayerException e) {
				e.printStackTrace();
			}
			
			// Set player's scoreboard to default scoreboard
			player.updateScoreboard();
			
			// Update player hotbar
			player.updateHotbar();
			
			// Teleport the player to the spawn location
			Location loc = bp.getWorld().getSpawnLocation().clone();
			
			Chunk chunk = loc.getChunk();
			
			if (!chunk.isLoaded()) {
                chunk.load();
            }
			
			loc.setY(loc.getY() + 1);
			
			bp.teleport(loc, TeleportCause.COMMAND);
		}, 1L);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (gp.isInGame()) {
			Game game = gp.getGame();
			
			if (game.getArena().getType() == Arena.Type.DUEL) {
                MegaArena.getInstance().getGameManager().deleteGame(game,
                        String.format("%s has left the game and has left you victorious!",
                                ChatColor.AQUA + gp.getName() + ChatColor.GREEN));
            }
			
			gp.kill();
			
			try {
				game.removePlayer(gp);
			} catch (PlayerException e) {
				e.printStackTrace();
			}
		}
		
		try {
			MegaArena.getInstance().getPlayerManager().deletePlayer(gp);
		} catch (PlayerException e) {
			e.printStackTrace();
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
		
		ItemStack is = event.getItem();
		
		if (is == null || !is.hasItemMeta()) {
            return;
        }
		
		if (!is.hasItemMeta()) {
            return;
        }
		
		ItemMeta meta = is.getItemMeta();
		
		if (!meta.hasDisplayName()) {
            return;
        }
		
		String name = meta.getDisplayName();
		Player player = event.getPlayer();
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (name.equals(Utils.getFormattedMessage("hotbar-items.lobby-items.play.title"))) {
            MegaArena.getInstance().getPlayMenu().open(gp);
        } else if (name.equals(Utils.getFormattedMessage("hotbar-items.lobby-items.spectate.title"))) {
            MegaArena.getInstance().getSpectateMenu().open(gp);
        } else if (name.equals(Utils.getFormattedMessage("hotbar-items.lobby-items.shop.title"))) {
            MegaArena.getInstance().getShopMenu().openMainMenu(gp);
        } else if (name.equals(Utils.getFormattedMessage("hotbar-items.lobby-items.class-selector.title"))) {
            MegaArena.getInstance().getClassSelectorMenu().openMainMenu(gp);
        } else if (name.equals(Utils.getFormattedMessage("hotbar-items.spectator-items.teleporter.title"))) {
            MegaArena.getInstance().getSpectateMenu().open(gp);
        } else if (name.equals(Utils.getFormattedMessage("hotbar-items.spectator-items.exit-spectator-mode.title"))) {
            gp.setSpectating(false);
        }
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerFall(EntityDamageEvent event) {
		
		if (event.getCause() != DamageCause.FALL) {
            return;
        }
		
		if (!(event.getEntity() instanceof Player)) {
            return;
        }
		
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(((Player) event.getEntity()).getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!gp.isInGame()) {
            return;
        }
		
		if (gp.getGame().getArena().getType() != Arena.Type.FFA) {
            return;
        }
		
		if (!gp.hasFallen()) {
            event.setCancelled(true);
        }
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!gp.isInGame()) {
            return;
        }
		
		Game game = gp.getGame();
		
		if (game.getArena().isTournament() && game.getTournamentCountdownTime() <= 10 && !game.hasStarted()) {
            event.setCancelled(true);
        }
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		
		if (damager instanceof Wolf) {
            damager = (Player) ((Wolf) damager).getOwner();
        } else if (damager instanceof Projectile) {
            damager = (Player) ((Projectile) damager).getShooter();
        }
		
		if (!(damager instanceof Player) || !(damaged instanceof Player)) {
            return;
        }
		
		GamePlayer damagedGP = null;
		GamePlayer damagerGP = null;
		
		try {
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(((Player) damaged).getName());
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(((Player) damager).getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!damagedGP.isInGame()) {
            return;
        }
		
		if (!damagedGP.getGame().hasStarted()) {
			event.setCancelled(true);
			return;
		}
		
		if (damagedGP.getGame() != damagerGP.getGame()) {
            return;
        }
		
		if (damagedGP.getGame().getArena().getType() == Arena.Type.TDM && damagedGP.getSide() == damagerGP.getSide()) {
			event.setCancelled(true);
			return;
		}
		
		damagedGP.setLastDamager(damagerGP);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerAssist(EntityDamageByEntityEvent event) {
		
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		
		if (!(damaged instanceof Player) || (!(damager instanceof Player) && !(damager instanceof Projectile))) {
            return;
        }
		
		PlayerManager playerManager = MegaArena.getInstance().getPlayerManager();
		Player damagerPlayer = null;
		
		if (damager instanceof Projectile) {
			if (!(((Projectile) damager).getShooter() instanceof Player)) {
                return;
            }
			
			damagerPlayer = (Player) ((Projectile) damager).getShooter();
		} else {
            damagerPlayer = (Player) damager;
        }
		
		Player damagedPlayer = (Player) damaged;
		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;
		
		try {
			damagerGP = playerManager.getPlayer(damagerPlayer.getName());
			damagedGP = playerManager.getPlayer(damagedPlayer.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!damagerGP.isInGame() || !damagedGP.isInGame()) {
            return;
        }
		
		if (damagedGP.getGame() != damagerGP.getGame()) {
            return;
        }
		
		if (damagedGP.getLastDamageCause() != DamageCause.CUSTOM) {
            damagedGP.setLastDamageCause(event.getCause());
        }
		
		if (damagedGP.getGame().getArena().getType() == Arena.Type.DUEL) {
            return;
        }
		
		if (damagedGP.getGame().getArena().getType() == Arena.Type.TDM && damagedGP.getSide() == damagerGP.getSide()) {
			event.setCancelled(true);
			return;
		}			
		
		damagedGP.addAssistPlayer(damagerGP);
	}

	@EventHandler
	public void onWolfDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (!(event.getEntity() instanceof Wolf)) {
            return;
        }
		
		Wolf wolf = (Wolf) event.getEntity();
		
		AnimalTamer owner = wolf.getOwner();
		
		if (!(owner instanceof Player)) {
            return;
        }
		
		GamePlayer ownerGP = null;
		
		try {
			ownerGP = MegaArena.getInstance().getPlayerManager().getPlayer(owner.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!ownerGP.isInGame()) {
            return;
        }
		
		Entity damager = event.getDamager();
		
		if (!(damager instanceof Player)) {
            return;
        }
		
		Player damagerPlayer = (Player) damager;
		
		if (owner.getName().equals(damagerPlayer.getName())) {
			event.setCancelled(true);
			return;
		}
		
		GamePlayer damagerGP = null;
		
		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damagerPlayer.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!damagerGP.isInGame()) {
			event.setCancelled(true);
		}
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == ownerGP.getSide()) {
            event.setCancelled(true);
        }
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		GamePlayer player = null;
		
		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!player.isInGame()) {
            return;
        }
		
		Block block = event.getBlock();
		
		if (!player.getGame().getArena().isTournament() && block.getType() != Material.COBBLESTONE) {
			event.setCancelled(true);
			return;
		}
		
		if (event.isCancelled()) {
            return;
        }
		
		player.getGame().addPlacedBlock(block);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		GamePlayer player = null;
		
		try {
			player = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!player.isInGame()) {
            return;
        }
		
		if (player.getGame().getPlacedBlocks().contains(event.getBlock())) {
            return;
        }
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		event.setKeepInventory(true);
		
		Player player = event.getEntity();
		GamePlayer playerGP = null;
		
		try {
			playerGP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!playerGP.isInGame()) {
            return;
        }
		
		Game game = playerGP.getGame();
		
		if (playerGP.getGame().getArena().getType() == Arena.Type.DUEL) {
			GamePlayer opponentGP = null;
			
			for (GamePlayer curGP : game.getPlayers()) {
                if (curGP != playerGP) {
                    opponentGP = curGP;
                }
            }
			
			MegaArena.getInstance().getGameManager().deleteGame(game,
					String.format(ChatColor.AQUA + "%s suffered a bitter defeat to %s.",
							player.getName() + ChatColor.GREEN, ChatColor.AQUA + opponentGP.getName() + ChatColor.GREEN));
			return;
		}
		
		playerGP.kill();
		
		try {
			playerGP.getGame().removePlayer(playerGP);
		} catch (PlayerException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		ChatColor killedColor = ChatColor.GRAY;
		
		if (!killedGP.isInGame()) {
			event.setDeathMessage("");
			return;
		} else {
            killedColor = killedGP.getSide().getSideType().getNameColor();
        }
		
		EntityDamageEvent entityDamageEvent = killed.getLastDamageCause();
		
		if (entityDamageEvent.getEntityType() == null) {
            return;
        }
		
		String msg = null;
		ChatColor killerColor = ChatColor.GRAY;
		
		if (entityDamageEvent.getEntityType() == EntityType.PLAYER) {
			killer = event.getEntity().getKiller();
			GamePlayer killerGP = null;
			
			if (!(killer instanceof Player)) {
                return;
            }
			
			try {
				killerGP = MegaArena.getInstance().getPlayerManager().getPlayer(killer.getName());
			} catch (PlayerException e) {
			}
			
			if (killerGP.isInGame()) {
                killerColor = killerGP.getSide().getSideType().getNameColor();
            }
			
			ConfigurationSection deathMessagesSection = MegaArena.getInstance().getConfig()
					.getConfigurationSection("death-messages");
			Material tool = killer.getItemInHand().getType();
			List<String> msgList = null;
			
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
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.getEntity().remove();
        }
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		
		if (event.getLine(0) == null || event.getLine(1) == null) {
            return;
        }
		
		if (!event.getLine(0).equalsIgnoreCase("[Leaderboard]")) {
            return;
        }
		
		Player player = event.getPlayer();
		
		if (!player.hasPermission("megaarena.sign.create")) {
			player.sendMessage(ChatColor.RED + "You do not have permission to create a leaderboard sign.");
			return;
		}
		
		int place = 0;
		
		try {
			place = Integer.valueOf(event.getLine(1));
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "You did not enter an integer for the sign place.");
			return;
		}
		
		if (place > 5) {
			player.sendMessage(ChatColor.RED + "You may not enter a place over 5.");
			return;
		}
		
		try {
			MegaArena.getInstance().getSignManager().createSign(
					event.getBlock().getLocation(),
					place,
					6000L);
		} catch (SignException e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + e.getMessage());
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!gp.isInGame()) {
            return;
        }
		
		if (event.getSlotType() == SlotType.ARMOR) {
            event.setCancelled(true);
        }
	}
	
	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
            return;
        }
		
		Player player = (Player) entity;
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (gp.isBowCooldown()) {
			player.sendMessage(ChatColor.RED + "You must wait before using your bow again.");
			event.setCancelled(true);
			return;
		}
		
		gp.setBowCooldown(true);
		
		final GamePlayer finalGP = gp;
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> finalGP.setBowCooldown(false), 25L);
	}
}