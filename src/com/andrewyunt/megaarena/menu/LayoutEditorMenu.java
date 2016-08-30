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

import java.util.ArrayList;

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

/**
 * The class used to create instances of the layout editor menu.
 * 
 * @author Andrew Yunt
 */
public class LayoutEditorMenu implements Listener {
	
	private Inventory inv;
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public LayoutEditorMenu() {
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);
	}
	
	public void openMainMenu(GamePlayer player) {
		
		inv = Bukkit.createInventory(null, 27, "Layout Editor");

		ItemStack zombie = new ItemStack(Material.ROTTEN_FLESH);
		ItemStack skeleton = new ItemStack(Material.BONE);
		ItemStack creeper = new ItemStack(Material.TNT);
		ItemStack herobrine = new ItemStack(Material.ENDER_PEARL);
		ItemStack witherMinion = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
		ItemStack spiritWarrior = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemStack close = new ItemStack(Material.ARROW);
		
		ItemMeta zombieMeta = zombie.getItemMeta();
		ItemMeta skeletonMeta = skeleton.getItemMeta();
		ItemMeta creeperMeta = creeper.getItemMeta();
		ItemMeta herobrineMeta = herobrine.getItemMeta();
		ItemMeta witherMinionMeta = witherMinion.getItemMeta();
		ItemMeta spiritWarriorMeta = spiritWarrior.getItemMeta();
		ItemMeta closeMeta = close.getItemMeta();
		
		zombieMeta.setDisplayName("Zombie");
		skeletonMeta.setDisplayName("Skeleton");
		creeperMeta.setDisplayName("Creeper");
		herobrineMeta.setDisplayName("Herobrine");
		witherMinionMeta.setDisplayName("Wither Minion");
		spiritWarriorMeta.setDisplayName("Spirit Warrior");
		closeMeta.setDisplayName("Close");
		
		zombie.setItemMeta(zombieMeta);
		skeleton.setItemMeta(skeletonMeta);
		creeper.setItemMeta(creeperMeta);
		herobrine.setItemMeta(herobrineMeta);
		witherMinion.setItemMeta(witherMinionMeta);
		spiritWarrior.setItemMeta(spiritWarriorMeta);
		close.setItemMeta(closeMeta);
		
		for (int i = 0; i < 10; i++)
			inv.setItem(i, glassPane);

		inv.setItem(10, zombie);
		inv.setItem(11, skeleton);
		inv.setItem(12, creeper);
		inv.setItem(14, herobrine);
		inv.setItem(13, glassPane);
		inv.setItem(15, witherMinion);
		inv.setItem(16, spiritWarrior);

		for (int i = 17; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, close);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.getBukkitPlayer().openInventory(inv);
	}
	
	public void openClassMenu(GamePlayer player, Class classType) {
		
		inv = Bukkit.createInventory(null, 45, "Layout Editor - " + classType.getName());
		
		ItemStack[] contents = Utils.toChest(classType.getKitInventoryItems(player)).getContents();
		inv.setContents(contents);
		
		for (int i = 36; i < 40; i++)
			inv.setItem(i, glassPane);
		
		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemMeta goBackMeta = goBack.getItemMeta();
		goBackMeta.setDisplayName("Go Back");
		goBack.setItemMeta(goBackMeta);
		inv.setItem(40, goBack);
		
		for (int i = 41; i < 45; i++)
			inv.setItem(i, glassPane);
		
		player.getBukkitPlayer().openInventory(inv);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onMoveItemBetweenInventory(InventoryClickEvent event){
		
		if (!event.getInventory().getTitle().startsWith("Layout Editor"))
			return;
		
		Inventory clickedInventory = event.getClickedInventory();
		
		if (clickedInventory.getTitle().equals("container.inventory")) {
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
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
				@Override
				public void run() {
					
					try  {
						for (ItemStack hotbarItem : MegaArena.getInstance().getHotbarItems().values()) {
							
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
					}
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
		
		for (ItemStack hotbarItem : MegaArena.getInstance().getHotbarItems().values()) {
			if (!is.equals(hotbarItem.getItemMeta().getDisplayName()))
				continue;
			
			event.setCancelled(true);
			return;
		}
		
		String name = is.getItemMeta().getDisplayName();
		
		if (name.equals(ChatColor.RESET + "" + ChatColor.DARK_RED + "Health Potion")
				|| name.equals(ChatColor.RESET + "" + ChatColor.AQUA + "Speed Potion"))
			return;
		
		if (name.equals(" ")) {
			event.setCancelled(true);
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		if (name.equals("Close")) {
			player.closeInventory();
			event.setCancelled(true);
			return;
		}
		
		try {
			final GamePlayer gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
			
			if (name.equals("Go Back")) {
				if (title.startsWith("Layout Editor -")) {
					openMainMenu(gp);
					event.setCancelled(true);
				}
				return;
			}
			
			event.setCancelled(true);
			
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
				@Override
				public void run() {
					
					try  {
						openClassMenu(gp, Class.valueOf(name.replace(" ", "_").toUpperCase()));
					} catch (IllegalArgumentException e) {
					}
				}
			}, 1L);
		} catch (PlayerException e) {
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		
		Inventory inv = event.getInventory();
		String title = inv.getTitle();
		
		if (!title.startsWith("Layout Editor -"))
			return;
		
		Class classType = Class.valueOf(title.split("\\-", -1)[1].toUpperCase().substring(1).replace(' ', '_'));
		
		GamePlayer gp = null; 
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
		} catch (PlayerException e) {
		}
		
		MegaArena.getInstance().getDataSource().saveLayout(gp, classType, Utils.fromChest(inv));
	}
}