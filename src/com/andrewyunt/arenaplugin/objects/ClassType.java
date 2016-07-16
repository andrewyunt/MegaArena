package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;

/**
 * 
 * @author Andrew Yunt
 *
 */
public enum ClassType {
	
	ZOMBIE("Zombie", 0) {
	},
	
	SKELETON("Skeleton", 9) {
	},
	
	HEROBRINE("Herobrine", 18) {
	},
	
	CREEPER("Creeper", 27) {
	},
	
	SPIRIT_WARRIOR("Spirit Warrior", 36) {
	},
	
	WITHER_MINION("Wither Minion", 45) {	
	};
	
	private String name;
	private int upgradeRowStart;
	
	ClassType(String name, int upgradeRowStart) {
		
		this.name = name;
		this.upgradeRowStart = upgradeRowStart;
	}
	
	public String getName() {
		
		return name;
	}
	
	public int getUpgradeRowStart() {
		
		return upgradeRowStart;
	}
	
	public ItemStack[] getItems() {
		
		Inventory inv = ArenaPlugin.getInstance().getServer().createInventory(null, 54);
		
		if (this == ZOMBIE) {
			 inv.setItem(0, new ItemStack(Material.DIRT, 64)); // test item
		} else if (this == SKELETON) {
			
		} else if (this == HEROBRINE) {
			
		} else if (this == CREEPER) {
			
		} else if (this == SPIRIT_WARRIOR) {
			
		} else if (this == WITHER_MINION) {
			
		}
		return inv.getContents();
 	}
}