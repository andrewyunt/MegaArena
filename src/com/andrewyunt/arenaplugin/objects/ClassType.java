package com.andrewyunt.arenaplugin.objects;

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
}