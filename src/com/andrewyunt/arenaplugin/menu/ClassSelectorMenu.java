package com.andrewyunt.arenaplugin.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.IconMenu;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ClassSelectorMenu {
	
	private Player player;
	private IconMenu menu;
	
	public ClassSelectorMenu(Player player) {
		
		this.player = player;
		
		menu = new IconMenu("Class Selector", 9, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                event.setWillClose(true);
            }
		}, ArenaPlugin.getInstance())
		.setOption(1, new ItemStack(Material.STAINED_GLASS_PANE, 1), "", "")
		.setOption(1, new ItemStack(Material.STAINED_GLASS_PANE, 1), "", "")
		.setOption(1, new ItemStack(Material.STAINED_GLASS_PANE, 1), "", "");
		
		menu.open(player);
	}
	
	private void openHeroClassSelector() {
		
	}
	
	public void destroy() {
		
		menu.destroy();
	}
}