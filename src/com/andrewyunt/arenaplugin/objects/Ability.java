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
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;

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
		
		if (player.getEnergy() < 100)
			return;
		
		Player bp = player.getBukkitPlayer();
		
		if (this == HEAL) {
			
			double hearts = 2.0 + 0.5 * (getLevel(player) -1);
			
			if (player.getGame().getArena().getType() != ArenaType.FFA && player.getGame().getArena().getType() != ArenaType.DUEL)
				for (Entity entity : bp.getNearbyEntities(5, 5, 5)) {
					if (!(entity instanceof Player))
						continue;
					
					Player ep = (Player) entity;
					
					try {
						if (!ArenaPlugin.getInstance().getPlayerManager().getPlayer(ep.getName()).isInGame())
							return;
					} catch (PlayerException e) {
					}
					
					double newHealth = ((Damageable) ep).getHealth() + hearts;
					
					if (newHealth < 40)
						((Damageable) ep).setHealth(newHealth);
					else
						((Damageable) ep).setHealth(40D);
					
					ep.sendMessage(String.format(ChatColor.GOLD + "You have been healed by %s.", player.getName()));
				}
			
			double newHealth = ((Damageable) bp).getHealth() + hearts;
			
			if (newHealth < 40)
				((Damageable) bp).setHealth(newHealth);
			else
				((Damageable) bp).setHealth(40D);
			
			bp.sendMessage(String.format(ChatColor.GOLD + "You have used the heal ability" + 
			(((Damageable) bp).getHealth() < 40 ? String.format(" and have restored %s hearts.", hearts / 2) : ".")));
			
		} else if (this == SPLIT_ARROW) {
			
			Projectile arrow = bp.launchProjectile(Arrow.class);
			arrow.setMetadata("ArenaPlugin", new FixedMetadataValue(ArenaPlugin.getInstance(), true));
			arrow.setShooter(bp);
			
		} else if (this == LIGHTNING) {
			
			LightningStrike strike = bp.getWorld().strikeLightning(bp.getTargetBlock(null, 10).getLocation());
			strike.setMetadata("ArenaPlugin", new FixedMetadataValue(ArenaPlugin.getInstance(), true));
			
		} else if (this == EXPLODE) {
			
		} else if (this == HURRICANE) {
			
		} else if (this == WITHER_HEADS) {
			
		}
		
		player.setEnergy(0);
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