package com.andrewyunt.arenaplugin.objects;

/**
 * 
 * @author Andrew Yunt
 *
 */
public enum ClassType {
	
	ZOMBIE("Zombie") {
	},
	
	SKELETON("Skeleton") {
	},
	
	HEROBRINE("Herobrine") {
	},
	
	CREEPER("Creeper") {
	},
	
	SPIRIT_WARRIOR("Spirit Warrior") {
	},
	
	WITHER_MINION("Wither Minion") {	
	};
	
	private String name;
	
	ClassType(String name) {
		
		this.name = name;
	}
	
	@Override
	public String toString() {
		
		return name;
	}
}