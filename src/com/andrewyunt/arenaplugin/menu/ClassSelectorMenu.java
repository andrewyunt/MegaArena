package com.andrewyunt.arenaplugin.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.ClassType;
import com.andrewyunt.arenaplugin.objects.IconMenu;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ClassSelectorMenu {
	
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
	private Player player;
	private IconMenu menu;
	
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
                
            	event.setWillClose(false);
            }
		}, ArenaPlugin.getInstance());
		
		for (int i = 0; i < 12; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.setOption(12, new ItemStack(Material.IRON_SWORD), "NORMAL CLASSES", "");
		menu.setOption(13, glassPane, "", "");
		menu.setOption(14, new ItemStack(Material.DIAMOND_SWORD), "HERO CLASSES", "");
		
		for (int i = 15; i < 27; i++)
			menu.setOption(i, glassPane, "", "");
		
		menu.open(player);
	}
	
	private void openNormalClassSelector() {
		
		menu = new IconMenu("Normal Classes", 27, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
            	String name = event.getName();
            	
            	ArenaPlayer player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
            	
            	if (name.equals("Zombie")) {
            		player.setClassType(ClassType.ZOMBIE);
            	} else if (name.equals("Skeleton")) {
            		player.setClassType(ClassType.SKELETON);
            	} else if (name.equals("Creeper")) {	
            		player.setClassType(ClassType.CREEPER);
            	} else if (name.equals("Herobrine")) {
            		player.setClassType(ClassType.HEROBRINE);
            	} else if (name.equals("Go Back")) {
            		openMainMenu();
            		event.setWillClose(false);
            		return;
            	} else {
            		event.setWillClose(false);
            		return;
            	}
            	
            	player.getBukkitPlayer().sendMessage(String.format(ChatColor.GOLD + "You selected the %s class.", name));
            	
                event.setWillClose(true);
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
		
		menu.open(player);
	}
	
	private void openHeroClassSelector() {
		
		menu = new IconMenu("Hero Classes", 27, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
            	String name = event.getName();
            	
            	ArenaPlayer player = ArenaPlugin.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName());
            	
            	if (name.equals("Wither Minion")) {
            		player.setClassType(ClassType.WITHER_MINION);
            	} else if (name.equals("Spirit Warrior")) {
            		player.setClassType(ClassType.SPIRIT_WARRIOR);
            	} else if (name.equals("Go Back")) {
            		openMainMenu();
            		event.setWillClose(false);
            		return;
            	} else {
            		event.setWillClose(false);
            		return;
            	}
            	
            	player.getBukkitPlayer().sendMessage(String.format(ChatColor.GOLD + "You selected the %s class.", name));
            	
                event.setWillClose(true);
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
		
		menu.open(player);
	}
	
	public void destroy() {
		
		menu.destroy();
	}
}