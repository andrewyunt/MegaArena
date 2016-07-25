package com.andrewyunt.arenaplugin.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

import com.andrewyunt.arenaplugin.ArenaPlugin;

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
	
	WITHER_HEADS("Master's Attack") {	
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
			
			double hearts = .5 + (.5 * getLevel(player));
			
			for (Entity entity : bp.getNearbyEntities(5, 5, 5)) {
				if (!(entity instanceof Player))
					continue;
				
				Player ep = (Player) entity;
				
				((Damageable) ep).setHealth(((Damageable) ep).getHealth() + hearts);
				
				ep.sendMessage(String.format(ChatColor.GOLD + "You have been healed by %s.", player.getName()));
			}
			
			bp.setHealth(((Damageable) bp).getHealth() + hearts);
			
			bp.sendMessage(String.format(ChatColor.GOLD + "You have used the heal ability and have restored %s hearts.", hearts * 2));
			
		} else if (this == SPLIT_ARROW) {
			
			Projectile arrow = bp.launchProjectile(Arrow.class);
			arrow.setMetadata("ArenaPlugin", new FixedMetadataValue(ArenaPlugin.getInstance(), true));
			
		} else if (this == LIGHTNING) {
			
			LightningStrike strike = bp.getWorld().strikeLightning(bp.getTargetBlock(null, 10).getLocation());
			strike.setMetadata("ArenaPlugin", new FixedMetadataValue(ArenaPlugin.getInstance(), true));
			
		} else if (this == EXPLODE) {
			
		} else if (this == HURRICANE) {
			
		} else if (this == WITHER_HEADS) {
			
		}
	}
	
	public int getLevel(ArenaPlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("arenaplugin.%s.%s", this.toString().toLowerCase(), i)))
				return i;
		
		return 1; 
	}
	
	public void setLevel(ArenaPlayer player, int level) {
		
		ArenaPlugin.getInstance().getPermissions().playerAdd(player.getBukkitPlayer(),
				String.format("arenaplugin.%s.%s", this.toString().toLowerCase(), level));
	}
}