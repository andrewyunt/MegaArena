package com.andrewyunt.arenaplugin.menu;

import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.IconMenu;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class UpgradesMenu {
	
	private IconMenu menu;
	
	public UpgradesMenu(Player player, Class classType) {
		
		FileConfiguration config = ArenaPlugin.getInstance().getConfig();
		Set<String> configKeys = config.getConfigurationSection(classType.toString().toLowerCase()).getKeys(false);
		
		menu = new IconMenu("Class Upgrades", (configKeys.size() * 9) + 9, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
            	
            }
		}, ArenaPlugin.getInstance());
		
		for (String key : configKeys) {
			
		}
		
		menu.setSpecificTo(player);
		menu.open(player);
	}
	
	public void destroy() {
		
		menu.destroy();
	}
}