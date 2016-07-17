package com.andrewyunt.arenaplugin.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.ClassType;
import com.andrewyunt.arenaplugin.objects.IconMenu;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class UpgradesMenu {
	
	private IconMenu menu;
	
	public UpgradesMenu(Player player) {
		
		menu = new IconMenu("Class Upgrades", 27, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
            	String name = event.getName();
            	
            	int level = Integer.parseInt(name.replaceAll("[\\D]", ""));
            	
            	event.setWillClose(false);
            }
		}, ArenaPlugin.getInstance());
		
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		for (ClassType type : ClassType.values()) {
			int classLevel = ap.getClassLevel(type);
			
			int rowStart = type.getUpgradeRowStart();
			int i = rowStart;
			
			while (i <= rowStart + 9) {
				int rowNum = i - rowStart;
				
				ItemStack is = null;
				String name = type.getName() + " - Level " + String.valueOf(rowNum + 1);
				String description = null;
				
				if (classLevel >= rowNum) {
					is = new ItemStack(Material.HARD_CLAY, 1, (short) 5);
					name = ChatColor.GREEN + name;
					description = ChatColor.GREEN + "Purchased";
				} else {
					if (ArenaPlugin.getInstance().getEconomy().getBalance(player) < 
							ArenaPlugin.getInstance().getConfig().getInt("tier-" + String.valueOf(rowNum) + "-upgrade-cost")) {
						is = new ItemStack(Material.HARD_CLAY, 1, (short) 14);
						name = ChatColor.RED + name;
						description = ChatColor.RED + "You cannot afford to purchase this class upgrade.";
					} else {
						is = new ItemStack(Material.HARD_CLAY, 1, (short) 4);
						name = ChatColor.YELLOW + name;
						description = "";
					}
				}
				
				menu.setOption(i, is, name, description);
				
				i++;
			}
		}
		
		menu.open(player);
	}
	
	public void destroy() {
		
		menu.destroy();
	}
}