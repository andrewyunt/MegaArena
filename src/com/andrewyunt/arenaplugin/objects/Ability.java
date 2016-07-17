package com.andrewyunt.arenaplugin.objects;

/**
 * 
 * @author Andrew Yunt
 *
 */
public enum Ability {
	
	HEAL("Heal") {
	},
	
	SPLIT_ARROW("Split Arrow") {
	},
	
	LIGHTNING("Lightning") {
	},
	
	EXPLODE("Explode") {
	},
	
	HURRICANE("Hurricane") {
	},
	
	MASTERS_ATTACK("Master's Attack") {	
	};
	
	private String name;
	
	Ability(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
}