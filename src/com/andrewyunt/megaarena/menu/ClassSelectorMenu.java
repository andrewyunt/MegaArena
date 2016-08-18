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
package com.andrewyunt.megaarena.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Class;

/**
 * The class used to create instances of the class selector menu.
 * 
 * @author Andrew Yunt
 */
public class ClassSelectorMenu implements Listener {

	private Player player;
	private Inventory inv;
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public ClassSelectorMenu(Player player) {

		this.player = player;
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);
		
		openMainMenu();
	}

	private void openMainMenu() {
		
		MegaArena.getInstance().getServer().getPluginManager().registerEvents(this, MegaArena.getInstance());

		inv = Bukkit.createInventory(null, 27, "Class Selector");

		ItemStack normalClasses = new ItemStack(Material.IRON_SWORD);
		ItemStack heroClasses = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack close = new ItemStack(Material.ARROW);
		
		ItemMeta normalClassesMeta = normalClasses.getItemMeta();
		ItemMeta heroClassesMeta = heroClasses.getItemMeta();
		ItemMeta closeMeta = close.getItemMeta();
		
		normalClassesMeta.setDisplayName("NORMAL CLASSES");
		heroClassesMeta.setDisplayName("HERO CLASSES");
		closeMeta.setDisplayName("Close");
		
		normalClasses.setItemMeta(normalClassesMeta);
		heroClasses.setItemMeta(heroClassesMeta);
		close.setItemMeta(closeMeta);
		
		for (int i = 0; i < 12; i++)
			inv.setItem(i, glassPane);

		inv.setItem(12, normalClasses);
		inv.setItem(13, glassPane);
		inv.setItem(14, heroClasses);

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, close);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.openInventory(inv);
	}

	private void openNormalClassSelector() {
		
		MegaArena.getInstance().getServer().getPluginManager().registerEvents(this, MegaArena.getInstance());

		inv = Bukkit.createInventory(null, 27, "Normal Classes");
		
		ItemStack zombie = new ItemStack(Material.ROTTEN_FLESH);
		ItemStack skeleton = new ItemStack(Material.BONE);
		ItemStack creeper = new ItemStack(Material.TNT);
		ItemStack herobrine = new ItemStack(Material.ENDER_PEARL);
		ItemStack goBack = new ItemStack(Material.ARROW);
		
		ItemMeta zombieMeta = zombie.getItemMeta();
		ItemMeta skeletonMeta = skeleton.getItemMeta();
		ItemMeta creeperMeta = creeper.getItemMeta();
		ItemMeta herobrineMeta = herobrine.getItemMeta();
		ItemMeta goBackMeta = goBack.getItemMeta();
		
		zombieMeta.setDisplayName("Zombie");
		skeletonMeta.setDisplayName("Skeleton");
		creeperMeta.setDisplayName("Creeper");
		herobrineMeta.setDisplayName("Herobrine");
		goBackMeta.setDisplayName("Go Back");
		
		zombie.setItemMeta(zombieMeta);
		skeleton.setItemMeta(skeletonMeta);
		creeper.setItemMeta(creeperMeta);
		herobrine.setItemMeta(herobrineMeta);
		goBack.setItemMeta(goBackMeta);
		
		for (int i = 0; i < 11; i++)
			inv.setItem(i, glassPane);

		inv.setItem(11, zombie);
		inv.setItem(12, skeleton);
		inv.setItem(13, glassPane);
		inv.setItem(14, creeper);
		inv.setItem(15, herobrine);

		for (int i = 16; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, goBack);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.openInventory(inv);
	}

	private void openHeroClassSelector() {
		
		MegaArena.getInstance().getServer().getPluginManager().registerEvents(this, MegaArena.getInstance());

		inv = Bukkit.createInventory(null, 27, "Hero Classes");

		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemStack spiritWarrior = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemStack witherMinion = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);

		ItemMeta goBackMeta = goBack.getItemMeta();
		ItemMeta spiritWarriorMeta = spiritWarrior.getItemMeta();
		ItemMeta witherMinionMeta = witherMinion.getItemMeta();

		goBackMeta.setDisplayName("Go Back");
		spiritWarriorMeta.setDisplayName("Spirit Warrior");
		witherMinionMeta.setDisplayName("Wither Minion");

		goBack.setItemMeta(goBackMeta);
		spiritWarrior.setItemMeta(spiritWarriorMeta);
		witherMinion.setItemMeta(witherMinionMeta);

		for (int i = 0; i < 12; i++)
			inv.setItem(i, glassPane);

		inv.setItem(12, spiritWarrior);
		inv.setItem(13, glassPane);
		inv.setItem(14, witherMinion);

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, goBack);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (event.getClickedInventory() == null)
			return;
		
		if (event.getWhoClicked() != player)
			return;
		
		String title = event.getClickedInventory().getTitle();
		
		if (title == null)
			return;
		
		if (!(title.equals("Hero Classes") || title.equals("Normal Classes") || title.equals("Class Selector")))
			return;
		
		event.setCancelled(true);

		GamePlayer ap = null;

		try {
			ap = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		ItemStack is = event.getCurrentItem();
		
		if(is == null || is.getType() == Material.AIR)
			return;
		
		if (!is.hasItemMeta())
			return;

		String name = is.getItemMeta().getDisplayName();

		if (title.equals("Normal Classes") || title.equals("Hero Classes")) {
			
			if (name == null || name == " ")
				return;

			if (name.equals("Go Back")) {
				close();
				openMainMenu();
				return;
			}
			
			if (!player.hasPermission("megaarena.class." + name.toLowerCase()))  {
				player.sendMessage(ChatColor.RED + "You do not have permission to select that class.");
				return;
			}
			
			ap.setClassType(Class.valueOf(name.toUpperCase()));
			player.sendMessage(String.format(ChatColor.GREEN + "You selected the %s class.",
					ChatColor.AQUA + name + ChatColor.GREEN));
		
			close();
			
		} else if (title.equals("Class Selector")) {
			
			close();
			
			if (name.equals("NORMAL CLASSES"))
				openNormalClassSelector();
			else if (name.equals("HERO CLASSES"))
				openHeroClassSelector();
		}
	}

	public void close() {

		HandlerList.unregisterAll(this);

		player.closeInventory();
	}
}