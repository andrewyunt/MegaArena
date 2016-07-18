package com.andrewyunt.arenaplugin.menu;

import org.bukkit.Bukkit;
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
		
		menu = new IconMenu("Class Upgrades", 54, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
            	String name = event.getName().substring(0, event.getName().indexOf(' '));
            	int level = Integer.parseInt(event.getName().replaceAll("[\\D]", ""));
            	int cumulativeCost = ArenaPlugin.getInstance().getConfig().getInt("tier-" + String.valueOf(level) + "-upgrade-cost");
            	int i = level;
            	
            	while (i > 1) {
            		i--;
            		
            		Bukkit.getServer().broadcastMessage(String.valueOf(i));
            		
            		cumulativeCost = cumulativeCost + ArenaPlugin.getInstance().getConfig().getInt("tier-" + String.valueOf(i) + "-upgrade-cost");
            	}
            	
            	try {
					ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName()).setClassLevel(name, level);
				} catch (PlayerException e) {
				}
            	
            	player.sendMessage(String.format(ChatColor.GOLD + "You have upgraded the %s class to level %s.", name, level));
            	
            	event.setWillClose(false);
            	
            	// add check if player can afford to buy the rank and check if the player has the rank
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
			
			while (i <= rowStart + 8) {
				int rowNum = i - rowStart;
				
				ItemStack is = null;
				String name = type.getName() + " - Level " + String.valueOf(rowNum + 1);
				String description = null;
				
				if (classLevel - 1 >= rowNum) {
					is = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
					name = ChatColor.GREEN + name;
					description = ChatColor.GREEN + "Purchased";
				} else {
					int cumulativeCost = ArenaPlugin.getInstance().getConfig().getInt("tier-" + String.valueOf(rowNum) + "-upgrade-cost");
					
					while (rowNum > 0) {
						rowNum--;
						
						cumulativeCost = cumulativeCost + ArenaPlugin.getInstance().getConfig().getInt("tier-" + String.valueOf(rowNum) + "-upgrade-cost");
					}
					
					if (ArenaPlugin.getInstance().getEconomy().getBalance(player) < cumulativeCost) {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
						name = ChatColor.RED + name;
						description = ChatColor.RED + "You cannot afford to purchase this class upgrade.";
					} else {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
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