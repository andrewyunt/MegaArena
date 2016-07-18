package com.andrewyunt.arenaplugin.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
	
	public void use(ArenaPlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		if (this == HEAL) {
			
			double hearts = 3 + (1 * player.getClassLevel(player.getClassType()));
			
			for (Entity entity : bp.getNearbyEntities(5, 5, 5)) {
				if (!(entity instanceof Player))
					continue;
				
				((Player) entity).setHealth(((Player) entity).getHealth() + hearts);
				
				entity.sendMessage(String.format(ChatColor.GOLD + "You have been healed by %s.", player.getName()));
			}
			
			bp.setHealth(bp.getHealth() + hearts);
			
			bp.sendMessage(String.format(ChatColor.GOLD + "You have used the heal ability and have restored %s hearts.", hearts / 2));
			
		} else if (this == SPLIT_ARROW) {
			
		} else if (this == LIGHTNING) {
			
		} else if (this == EXPLODE) {
			
		} else if (this == HURRICANE) {
			
		} else if (this == MASTERS_ATTACK) {
			
		}
	}
}