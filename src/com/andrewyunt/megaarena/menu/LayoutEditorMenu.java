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
package com.andrewyunt.megaarena.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.Utils;

import java.util.ArrayList;

/**
 * The class used to create instances of the layout editor menu.
 * 
 * @author Andrew Yunt
 */
public class LayoutEditorMenu implements Listener {
	
	private Inventory inv;
	private final ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public LayoutEditorMenu() {
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);
	}
	
	public void open(GamePlayer player, Class classType, boolean loadFromDB) {
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> player.getBukkitPlayer().getInventory().clear(), 6L);
		
		inv = Bukkit.createInventory(null, 45, "Layout Editor - " + classType.getName());
		
		ItemStack[] contents = Utils.toChest(classType.getKitInventoryItems(player, loadFromDB)).getContents();
		inv.setContents(contents);
		
		for (int i = 36; i < 40; i++)
			inv.setItem(i, glassPane);
		
		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemMeta goBackMeta = goBack.getItemMeta();
		goBackMeta.setDisplayName("Go Back");
		goBack.setItemMeta(goBackMeta);
		inv.setItem(40, goBack);
		
		ItemStack reset = new ItemStack(Material.STORAGE_MINECART);
		ItemMeta resetMeta = reset.getItemMeta();
		resetMeta.setDisplayName("Reset Layout");
		reset.setItemMeta(resetMeta);
		inv.setItem(41, reset);
		
		for (int i = 42; i < 45; i++)
			inv.setItem(i, glassPane);
		
		player.getBukkitPlayer().openInventory(inv);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onMoveItemBetweenInventory(InventoryClickEvent event){
		
		if (!event.getInventory().getTitle().startsWith("Layout Editor"))
			return;
		
		Inventory clickedInventory = event.getClickedInventory();
		
		if (clickedInventory.getTitle().equals("container.inventory") || clickedInventory.getTitle() == null) {
			event.setCancelled(true);
			return;
		}
		
		if (event.isShiftClick()) {
			event.setCancelled(true);
			return;
		}
		
		ItemStack currentItem = event.getCurrentItem();
		
		if ((currentItem == null || currentItem.getType() == Material.AIR) && event.getCursor() == null) {
			event.setCancelled(true);
			return;
		}
		
		if (currentItem.getType() == Material.AIR && event.getCursor().getType() == Material.AIR) {
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> {
				try  {
					GamePlayer gp = MegaArena.getInstance().getPlayerManager().getPlayer((Player) event.getWhoClicked());
					
					for (ItemStack hotbarItem : gp.getHotbarItems().values()) {
						ItemMeta hotbarMeta = hotbarItem.getItemMeta();
						
						if (hotbarMeta == null)
							continue;
						
						ItemStack targetItem = clickedInventory.getItem(event.getSlot());
						
						if (targetItem == null)
							continue;
						
						if (!targetItem.getItemMeta().getDisplayName().equals(hotbarMeta.getDisplayName()))
							continue;
						
						clickedInventory.setItem(event.getSlot(), new ItemStack(Material.AIR));
						MegaArena.getInstance().getPlayerManager().getPlayer(event.getWhoClicked()
								.getName()).updateHotbar();
						break;
					}
				} catch (IllegalArgumentException | PlayerException e) {
					e.printStackTrace();
				}
			}, 1L);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		String title = event.getInventory().getTitle();
		
		if (!title.startsWith("Layout Editor"))
			return;
		
		ItemStack is = event.getCurrentItem();
		
		if (is == null || is.getType() == Material.AIR)
			return;
		
		if (!is.hasItemMeta())
			return;
		
		Player player = (Player) event.getWhoClicked();
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		String name = is.getItemMeta().getDisplayName();
		
		for (ItemStack hotbarItem : gp.getHotbarItems().values()) {
			if (name.equals(hotbarItem.getItemMeta().getDisplayName())) {
				event.setCancelled(true);
				return;
			}
		}
		
		if (name.equals(ChatColor.RESET + "" + ChatColor.DARK_RED + "Health Potion")
				|| name.equals(ChatColor.RESET + "" + ChatColor.AQUA + "Speed Potion"))
			return;
		
		if (name.equals(" ")) {
			event.setCancelled(true);
			return;
		}
		
		if (name.equals("Close")) {
			player.closeInventory();
			event.setCancelled(true);
			return;
		}
		
		if (name.equals("Go Back")) {
			if (title.startsWith("Layout Editor -")) {
				Class classType = Class.valueOf(title.replace("Layout Editor - ", "").replace(" ", "_").toUpperCase());
				
				MegaArena.getInstance().getUpgradesMenu().openClassUpgradeMenu(gp, classType);
				
				event.setCancelled(true);
			}
			return;
		}
		
		if (name.equals("Reset Layout")) {
			if (title.startsWith("Layout Editor -")) {
				open(gp, Class.valueOf(title.replace("Layout Editor - ", "")
						.replace(" ", "_").toUpperCase()), false);
				event.setCancelled(true);
			}
			return;
		}
		
		event.setCancelled(true);
		
		final GamePlayer finalGP = gp;
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> {
			try  {
				open(finalGP, Class.valueOf(name.replace(" ", "_").toUpperCase()), true);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}, 1L);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		
		Inventory inv = event.getInventory();
		String title = inv.getTitle();
		
		if (!title.startsWith("Layout Editor -"))
			return;
		
		Class classType = Class.valueOf(title.split("\\-", -1)[1].toUpperCase().substring(1).replace(' ', '_'));
		
		try {
			final GamePlayer gp = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
			
			MegaArena.getInstance().getDataSource().saveLayout(gp, classType, Utils.fromChest(inv));
			
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), gp::updateHotbar, 5L);
		} catch (PlayerException e) {
			e.printStackTrace();
		}
	}
}