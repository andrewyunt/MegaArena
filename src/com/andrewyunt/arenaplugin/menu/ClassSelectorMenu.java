package com.andrewyunt.arenaplugin.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.IconMenu;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ClassSelectorMenu {
	
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
	private Player player;
	private IconMenu menu;
	private boolean clicked = false;
	
	public ClassSelectorMenu(Player player) {
		
		this.player = player;
		
		openMainMenu();
	}
	
	private void openMainMenu() {
		
		menu = new IconMenu("Class Selector", 27, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
            	String name = event.getName();
            	
                if (name.equals("NORMAL CLASSES"))
                	openNormalClassSelector();
                else if (name.equals("HERO CLASSES"))
                	openHeroClassSelector();
                else if (name.equals("Close")) {
                	event.setWillClose(true);
                	return;
                }
           
   
            	event.setWillClose(false);
            }
		}, ArenaPlugin.getInstance());
		
		for (int i = 0; i < 12; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(12, new ItemStack(Material.IRON_SWORD), "NORMAL CLASSES", "");
		menu.setOption(13, glassPane, "", "");
		menu.setOption(14, new ItemStack(Material.DIAMOND_SWORD), "HERO CLASSES", "");
		
		for (int i = 15; i < 22; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(22, new ItemStack(Material.ARROW), "Close", "");
		
		for (int i = 23; i < 27; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setSpecificTo(player);
		menu.open(player);
	}
	
	private void openNormalClassSelector() {
		
		menu.destroy();
		
		menu = new IconMenu("Normal Classes", 27, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
    			if (clicked == true)
    				return;
            	
            	String name = event.getName();
            	
            	ArenaPlayer player = null;
            	
				try {
					player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
				} catch (PlayerException e) {
				}
            	
				switch (name) {
					case "Go Back":
						openMainMenu();
						event.setWillClose(false);
						break;
					case "":
						event.setWillClose(false);
						return;
					case "Zombie":
					case "Skeleton":
					case "Creeper":
					case "Herobrine":
						player.setClassType(Class.valueOf(name.toUpperCase()));
						player.getBukkitPlayer().sendMessage(String.format(ChatColor.GREEN + "You selected the %s class.",
								ChatColor.AQUA + name + ChatColor.GREEN));
						clicked = true;
						event.setWillClose(true);
						return;
				}
            	
            	event.setWillDestroy(true);
            }
		}, ArenaPlugin.getInstance());
		
		for (int i = 0; i < 11; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(11, new ItemStack(Material.ROTTEN_FLESH), "Zombie", "");
		menu.setOption(12, new ItemStack(Material.BONE), "Skeleton", "");
		menu.setOption(13, glassPane, "", "");
		menu.setOption(14, new ItemStack(Material.TNT), "Creeper", "");
		menu.setOption(15, new ItemStack(Material.ENDER_PEARL), "Herobrine", "");
		
		for (int i = 16; i < 22; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(22, new ItemStack(Material.ARROW), "Go Back", "");
		
		for (int i = 23; i < 27; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setSpecificTo(player);
		menu.open(player);
	}
	
	private void openHeroClassSelector() {
		
		menu.destroy();
		
		menu = new IconMenu("Hero Classes", 27, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
    			if (clicked == true)
    				return;
            	
            	String name = event.getName();
            	
            	ArenaPlayer player = null;
            	
				try {
					player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
				} catch (PlayerException e) {
				}
            	
            	if (name.equals("Wither Minion")) {
            		player.setClassType(Class.WITHER_MINION);
            		clicked = true;
            	} else if (name.equals("Spirit Warrior")) {
            		player.setClassType(Class.SPIRIT_WARRIOR);
            		clicked = true;
            	} else if (name.equals("Go Back")) {
            		openMainMenu();
            		event.setWillClose(false);
            		return;
            	} else {
            		event.setWillClose(false);
            		return;
            	}
            	
            	player.getBukkitPlayer().sendMessage(String.format(ChatColor.GREEN + "You selected the %s class.",
            			ChatColor.AQUA + name + ChatColor.GREEN));
            	
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
		}, ArenaPlugin.getInstance());
		
		for (int i = 0; i < 12; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(12, new ItemStack(Material.ENCHANTMENT_TABLE), "Spirit Warrior", "");
		menu.setOption(13, glassPane, "", "");
		menu.setOption(14, new ItemStack(Material.SKULL_ITEM, 1, (short) 1), "Wither Minion", "");
		
		for (int i = 15; i < 22; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(22, new ItemStack(Material.ARROW), "Go Back", "");
		
		for (int i = 23; i < 27; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setSpecificTo(player);
		menu.open(player);
	}
	
	public void destroy() {
		
		menu.destroy();
	}
}