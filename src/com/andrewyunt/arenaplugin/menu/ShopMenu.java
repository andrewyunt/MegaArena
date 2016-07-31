package com.andrewyunt.arenaplugin.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.Ability;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.IconMenu;
import com.andrewyunt.arenaplugin.objects.Skill;
import com.andrewyunt.arenaplugin.utilities.Utils;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ShopMenu {

	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
	private IconMenu menu;
	private Player player;
	private ArenaPlayer ap;

	public ShopMenu(Player player) {

		this.player = player;

		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		openClassUpgradesMenu();
	}
	
	private void openClassUpgradesMenu() {
		
		if (menu != null)
			menu.destroy();
		
		menu = new IconMenu("Class Upgrades", 27, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {

				String name = event.getName();

				switch (name) {
				case "Close":
					event.setWillClose(true);
					break;
				case "":
					event.setWillClose(false);
					return;
				case "Zombie":
				case "Skeleton":
				case "Creeper":
				case "Herobrine":
				case "Wither Minion":
				case "Spirit Warrior":
					openClassUpgradeMenu(Class.valueOf(name.toUpperCase().replace(' ', '_')));
					event.setWillClose(false);
					return;
				}
			}
		}, ArenaPlugin.getInstance());

		for (int i = 0; i < 10; i++)
			menu.setOption(i, glassPane, "", "");

		menu.setOption(10, new ItemStack(Material.ROTTEN_FLESH), "Zombie", "");
		menu.setOption(11, new ItemStack(Material.BONE), "Skeleton", "");
		menu.setOption(12, new ItemStack(Material.TNT), "Creeper", "");
		menu.setOption(14, new ItemStack(Material.ENDER_PEARL), "Herobrine", "");
		menu.setOption(13, glassPane, "", "");
		menu.setOption(15, new ItemStack(Material.ENCHANTMENT_TABLE), "Wither Minion", "");
		menu.setOption(16, new ItemStack(Material.SKULL_ITEM), "Spirit Warrior", "");

		for (int i = 17; i < 22; i++)
			menu.setOption(i, glassPane, "", "");

		menu.setOption(22, new ItemStack(Material.ARROW), "Close", "");

		for (int i = 23; i < 27; i++)
			menu.setOption(i, glassPane, "", "");

		menu.setSpecificTo(player);
		menu.open(player);
	}

	private void openClassUpgradeMenu(Class classType) {
		
		menu.destroy();

		menu = new IconMenu("Class Upgrades - " + classType.getName(), 45, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {

				ItemStack item = event.getItem();
				
				if (item.getType() == Material.ARROW) {
					openClassUpgradesMenu();
					event.setWillClose(false);
					return;
				}

				if (item.getType() != Material.STAINED_CLAY)
					return;
				
				if (item.getDurability() == 14) {
					player.sendMessage(ChatColor.RED + "You must unlock the preceding upgrades or you cannot afford that upgrade.");
					return;
				} else if (item.getDurability() == 5) {
					player.sendMessage(ChatColor.RED + "You have already puchased that class upgrade.");
					return;
				}
				
				Inventory inv = event.getInventory();
				
				Class classType = Class.valueOf(inv.getName()
						.split("\\-", -1)[1].toUpperCase().substring(1).replace(' ', '_'));
				
				int position = event.getPosition();
				
				if (position < 9) {
					
					position++;
					Ability ability = classType.getAbility();
					
					ability.setLevel(ap, position);
					ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
							ability.getName() + ChatColor.GREEN));
				
				} else if (9 <= position && position < 18) {
					
					position = position - 8;
					Skill skillOne = classType.getSkillTwo();
					
					skillOne.setLevel(ap, position);
					ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
							skillOne.getName() + ChatColor.GREEN));
			
				} else if (18 <= position && position < 27) {
				
					position = position - 17;		
					Skill skillTwo = classType.getSkillTwo();
					
					skillTwo.setLevel(ap, position);
					ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
							skillTwo.getName() + ChatColor.GREEN));
			
				} else if (27 <= position && position < 36) {
				
					position = position - 26;
					
					classType.setKitLevel(ap, position);
					ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s kit upgrade purchased successfully.",
							classType.getName() + ChatColor.GREEN));
				}
				
				int cost = ArenaPlugin.getInstance().getConfig().getInt("tier-" + String.valueOf(position) + "-upgrade-cost");
				ap.removeCoins(cost);
				
				event.setWillDestroy(true);
			}
		}, ArenaPlugin.getInstance());

		Ability ability = classType.getAbility();
		Skill skillOne = classType.getSkillOne();
		Skill skillTwo = classType.getSkillTwo();

		int abilityLevel = ability.getLevel(ap);
		int skillOneLevel = skillOne.getLevel(ap);
		int skillTwoLevel = skillTwo.getLevel(ap);
		int kitLevel = classType.getKitLevel(ap);
		
		FileConfiguration config = ArenaPlugin.getInstance().getConfig();

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
				List<String> description = null;
				
				if (section.contains("title") && section.contains("description")) {
					name = section.getString("title");
					description = new ArrayList<String>(Arrays.asList(section.getString("description").split("\\r?\\n")));
				}
				
				description.add("");

				ChatColor color = null;
				int cost = config.getInt("tier-" + String.valueOf(curLevel) + "-upgrade-cost");
				
				if (available == true) {
					is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
					color = ChatColor.RED;
				} else
					if (level >= curLevel) {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
						color = ChatColor.GREEN;
						description.add("Purchased");
					} else {
						description.add("Cost: " + String.valueOf(cost));
						
						if (ap.getCoins() < cost) {
							is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
							color = ChatColor.RED;
						} else {
							is = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
							color = ChatColor.YELLOW;
							available = true;
						}
					}
				
				name = color + name;
				
				String[] descriptionArray = description.toArray(new String[0]);
				descriptionArray = Utils.colorizeArray(descriptionArray, color);

				menu.setOption(i, is, name, descriptionArray);
				
				curLevel++;
				i++;
			}
			
			pass++;
		}
		
		for (int i = 36; i < 40; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(40, new ItemStack(Material.ARROW), "Go Back", "");
		
		for (int i = 41; i < 45; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setSpecificTo(player);
		menu.open(player);
	}

	public void destroy() {

		menu.destroy();
	}
}