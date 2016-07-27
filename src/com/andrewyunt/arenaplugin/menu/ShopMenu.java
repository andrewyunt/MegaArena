package com.andrewyunt.arenaplugin.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.Ability;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.IconMenu;
import com.andrewyunt.arenaplugin.objects.Skill;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ShopMenu {

	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
	private IconMenu menu;
	private Player player;

	public ShopMenu(Player player) {

		this.player = player;

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
		menu.setOption(13, glassPane, "", "");
		menu.setOption(14, new ItemStack(Material.ENDER_PEARL), "Herobrine", "");
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

	public void openClassUpgradeMenu(Class classType) {
		
		menu.destroy();

		ArenaPlayer ap = null;

		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		menu = new IconMenu("Class Upgrades - " + classType.getName(), 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {

				String name = event.getName();

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
			
			switch (pass) {
				case 0:
					level = abilityLevel;
					break;
				case 1:
					level = skillOneLevel;
					i = 9;
					break;
				case 2:
					level = skillTwoLevel;
					i = 18;
					break;
				case 3:
					level = kitLevel;
					i = 27;
					break;
			}
			
			int stop = i + 9;
			
			while (i < stop) {
				ItemStack is = null;
				ConfigurationSection section = config.getConfigurationSection("classes." + classType.toString() 
				+ "." + line + "." + String.valueOf(i + 1));
				
				String name = null;
				String description = null;
				
				if (section.contains("title") && section.contains("description")) {
					Bukkit.getServer().broadcastMessage("test");
					name = section.getString("title");
					description = section.getString("description");
				}

				if (level >= i + 1) {
					is = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
					name = ChatColor.GREEN + name;
				} else {
					if (ap.getCoins() < config.getInt("tier-" + String.valueOf(i) + "-upgrade-cost")) {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
						name = ChatColor.RED + name;
					} else {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
						name = ChatColor.YELLOW + name;
					}
				}

				menu.setOption(i, is, name, description);
				
				i++;
			}
			
			pass++;
		}
		
		menu.open(player);
	}

	public void destroy() {

		menu.destroy();
	}
}