package com.andrewyunt.arenaplugin.objects;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.andrewyunt.arenaplugin.objects.Ability.*;

import com.andrewyunt.arenaplugin.ArenaPlugin;

/**
 * 
 * @author Andrew Yunt
 *
 */
public enum ClassType {
	
	ZOMBIE("Zombie", HEAL, 0, 4) {
	},
	
	SKELETON("Skeleton", SPLIT_ARROW, 9, 15) {
	},
	
	HEROBRINE("Herobrine", LIGHTNING, 18, 10) {
	},
	
	CREEPER("Creeper", EXPLODE, 27, 10) {
	},
	
	SPIRIT_WARRIOR("Spirit Warrior", HURRICANE, 36, 5) {
	},
	
	WITHER_MINION("Wither Minion", MASTERS_ATTACK, 45, 5) {
	};
	
	private String name;
	private int upgradeRowStart;
	private int energyPerClick;
	private Ability ability;
	
	ClassType(String name, Ability ability, int upgradeRowStart, int energyPerClick) {
		
		this.name = name;
		this.ability = ability;
		this.upgradeRowStart = upgradeRowStart;
		this.energyPerClick = energyPerClick;
	}
	
	public String getName() {
		
		return name;
	}
	
	public Ability getAbility() {
		
		return ability;
	}
	
	public int getUpgradeRowStart() {
		
		return upgradeRowStart;
	}
	
	public int getEnergyPerClick() {
		
		return energyPerClick;
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