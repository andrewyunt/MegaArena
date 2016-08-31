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
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.db.DataSource;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Ability;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Skill;
import com.andrewyunt.megaarena.objects.Upgradable;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * The class used to create instances of the shop menu.
 * 
 * @author Andrew Yunt
 */
public class ShopMenu implements Listener {

	private Inventory inv;
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public ShopMenu() {
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);
	}
	
	public void openClassUpgradesMenu(GamePlayer player) {
		
		inv = Bukkit.createInventory(null, 27, "Class Upgrades");

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

	private void openClassUpgradeMenu(GamePlayer player, Class classType) {
		
		inv = Bukkit.createInventory(null, 45, "Class Upgrades - " + classType.getName());

		Ability ability = classType.getAbility();
		Skill skillOne = classType.getSkillOne();
		Skill skillTwo = classType.getSkillTwo();

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		int abilityLevel = ds.getLevel(player, ability);
		int skillOneLevel = ds.getLevel(player, skillOne);
		int skillTwoLevel = ds.getLevel(player, skillTwo);
		int kitLevel = ds.getLevel(player, classType);
		
		FileConfiguration config = MegaArena.getInstance().getConfig();

		List<String> lines = new ArrayList<String>();
		lines.add(ability.toString());
		lines.add(skillOne.toString());
		lines.add(skillTwo.toString());
		lines.add(classType.toString());
		
		int pass = 0;
		
		for (String line : lines) {
			int level = 0;
			int i = 0;
			int curLevel = 1;
			
			switch (pass) {
				case 0:
					level = abilityLevel;
					break;
				case 1:
					level = skillOneLevel;
					i = 9;
					curLevel = i - 8;
					break;
				case 2:
					level = skillTwoLevel;
					i = 18;
					curLevel = i - 17;
					break;
				case 3:
					level = kitLevel;
					i = 27;
					curLevel = i - 26;
					break;
			}
			
			int stop = i + 9;
			boolean available = false;
			
			while (i < stop) {
				ItemStack is = null;
				ConfigurationSection section = config.getConfigurationSection("classes." + classType.toString() 
				+ "." + line + "." + String.valueOf(curLevel));
				
				String name = null;
				List<String> lore = null;
				
				if (section.contains("title") && section.contains("description")) {
					name = section.getString("title");
					lore = new ArrayList<String>(Arrays.asList(section.getString("description").split("\\r?\\n")));
				}
				
				lore.add("");

				ChatColor color = null;
				int cost = config.getInt("tier-" + String.valueOf(curLevel) + "-upgrade-cost");
				
				if (available == true) {
					is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
					color = ChatColor.RED;
				} else
					if (level >= curLevel) {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
						color = ChatColor.GREEN;
						lore.add("Purchased");
					} else {
						lore.add("Cost: " + String.valueOf(cost));
						
						if (player.getCoins() < cost) {
							is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
							color = ChatColor.RED;
						} else {
							is = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
							color = ChatColor.YELLOW;
							available = true;
						}
					}
				
				lore = Utils.colorizeList(lore, color);
				ItemMeta meta = is.getItemMeta();
				
				meta.setDisplayName(color + name);
				meta.setLore(lore);
				is.setItemMeta(meta);
				
				inv.setItem(i, is);
				
				curLevel++;
				i++;
			}
			
			pass++;
		}
		
		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemMeta goBackMeta = goBack.getItemMeta();
		
		goBackMeta.setDisplayName("Go Back");
		goBack.setItemMeta(goBackMeta);
		
		for (int i = 36; i < 40; i++)
			inv.setItem(i, glassPane);
		
		inv.setItem(40, goBack);
		
		for (int i = 41; i < 45; i++)
			inv.setItem(i, glassPane);
		
		player.getBukkitPlayer().openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (event.getClickedInventory() == null)
			return;
		
		String title = event.getClickedInventory().getTitle();
		
		if (title == null)
			return;
		
		if (!title.startsWith("Class Upgrades"))
			return;
		
		ItemStack is = event.getCurrentItem();
		
		if(is == null || is.getType() == Material.AIR)
			return;
		
		if (!is.hasItemMeta())
			return;
		
		event.setCancelled(true);
		
		String name = is.getItemMeta().getDisplayName();
		
		Player player = (Player) event.getWhoClicked();
		GamePlayer gp = null;

		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		if (title.equals("Class Upgrades")) {
			
			if (name == null || name == " ")
				return;

			if (name.equals("Close")) {
				player.closeInventory();
				return;
			}
			
			if (!player.hasPermission("megaarena.class." + name.replace(" ", "_").toLowerCase()))  {
				player.sendMessage(ChatColor.RED + "You do not own that class.");
				return;
			}
			
			openClassUpgradeMenu(gp, Class.valueOf(name.toUpperCase().replace(' ', '_')));
			
		} else {
			
			if (is.getType() == Material.ARROW) {
				openClassUpgradesMenu(gp);
				return;
			}

			if (is.getType() != Material.STAINED_CLAY)
				return;

			Class classType = Class.valueOf(title.split("\\-", -1)[1].toUpperCase().substring(1).replace(' ', '_'));
			
			if (is.getDurability() == 14) {
				player.sendMessage(ChatColor.RED + "You must unlock the preceding upgrades or you cannot afford that upgrade.");
				return;
			} else if (is.getDurability() == 5) {
				player.sendMessage(ChatColor.RED + "You have already puchased that class upgrade.");
				return;
			}
			
			Upgradable upgradable = null;
			int slot = event.getSlot();
			
			if (slot < 9) {
				slot++;
				upgradable = classType.getAbility();
			} else if (9 <= slot && slot < 18) {
				slot = slot - 8;
				upgradable = classType.getSkillOne();
			} else if (18 <= slot && slot < 27) {
				slot = slot - 17;		
				upgradable = classType.getSkillTwo();
			} else if (27 <= slot && slot < 36) {
				slot = slot - 26;
				upgradable = classType;
			}
			
			int cost = classType.isHero() ? 
					MegaArena.getInstance().getConfig().getInt("tier-" + String.valueOf(slot) + "-hero-upgrade-cost")
					: MegaArena.getInstance().getConfig().getInt("tier-" + String.valueOf(slot) + "-upgrade-cost");
			
			gp.removeCoins(cost);
			
			MegaArena.getInstance().getDataSource().setLevel(gp, upgradable, slot);
			
			gp.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
					upgradable.getName() + ChatColor.GREEN));
			
			openClassUpgradeMenu(gp, classType);
		}
	}
}