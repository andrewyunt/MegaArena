package com.andrewyunt.megaarena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.menu.ClassSelectorMenu;
import com.andrewyunt.megaarena.menu.ShopMenu;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Game;
import com.andrewyunt.megaarena.objects.Arena.ArenaType;

/**
 * 
 * @author Andrew Yunt
 *
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

		if (gp.isInGame())
			gp.getGame().removePlayer(gp);
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

			player.sendMessage(ChatColor.RED + "Team-deathmatch is coming soon!");

			/*
			 * try { ArenaPlugin.getInstance().getGameManager().matchMake(gp,
			 * ArenaType.TDM); } catch (GameException e) { }
			 */

		} else if (name.equals("Play : Free-for-all")) {

			try {
				MegaArena.getInstance().getGameManager().matchMake(gp, ArenaType.FFA);
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

		if (message.startsWith("/tp")) {

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

		if (damagedGP.getGame().getArena().getType() != ArenaType.TDM)
			return;

		if (damagedGP.getSide() != damagerGP.getSide()) {
			damagedGP.addAssistPlayer(damagerGP);
			return;
		}

		event.setCancelled(true);
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
	public void onPlayerRespawn(PlayerRespawnEvent event) {

		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
			@Override
			public void run() {
				Player player = (Player) event.getPlayer();
				GamePlayer gp = null;

				try {
					gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
				} catch (PlayerException e) {
				}

				if (gp.isInGame())
					gp.getGame().removePlayer(gp);

				player.sendMessage(ChatColor.RED + "You have died and have been returned to the lobby.");
			}
		}, 1L);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();

		if (player.getKiller() == null || !(player.getKiller() instanceof Player))
			return;

		Player killer = player.getKiller();
		GamePlayer playerAP = null;
		GamePlayer killerAP = null;

		try {
			playerAP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
			killerAP = MegaArena.getInstance().getPlayerManager().getPlayer(killer.getName());
		} catch (PlayerException e) {
		}

		if (!(playerAP.isInGame()))
			return;

		int killCoins = 50;

		if (killer.hasPermission("megaarena.coins.double"))
			killCoins = 100;
		else if (killer.hasPermission("megaarena.coins.triple"))
			killCoins = 150;

		if (killerAP.isInGame()) {
			killerAP.addCoins(killCoins);
			killer.sendMessage(ChatColor.GREEN + String.format("You killed %s and received %s coins.",
					ChatColor.AQUA + player.getName() + ChatColor.GREEN,
					ChatColor.AQUA + String.valueOf(killCoins) + ChatColor.GREEN));
		}

		if (playerAP.getGame().getArena().getType() == ArenaType.TDM)
			for (GamePlayer assistAP : playerAP.getAssistPlayers()) {
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
								ChatColor.AQUA + player.getName() + ChatColor.GREEN,
								ChatColor.AQUA + String.valueOf(killCoins) + ChatColor.GREEN));
			}

		event.setKeepInventory(true);

		Game game = playerAP.getGame();

		if (game.getArena().getType() == ArenaType.DUEL) {
			MegaArena.getInstance().getGameManager().deleteGame(game,
					String.format(ChatColor.AQUA + "%s suffered a bitter defeat to %s.",
							player.getName() + ChatColor.GREEN, ChatColor.AQUA + killer.getName() + ChatColor.GREEN));
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {

		event.setCancelled(true);
	}
}