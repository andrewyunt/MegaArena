/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megaarena.objects;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * The enumeration for abilities, their names, and the method to use them.
 * 
 * @author Andrew Yunt
 */
public enum Ability implements Upgradable {
	
	HEAL("Heal"),
	EXPLOSIVE_ARROW("Explosive Arrow"),
	LIGHTNING("Lightning"),
	EXPLODE("Explode"),
	TORNADO("Tornado"),
	WITHER_HEADS("Master's Attack");
	
	private String name;
	private int particleTaskID;
	private int damageTaskID;
	
	Ability(String name) {
		
		this.name = name;
	}
	
	@Override
	public String getName() {
		
		return name;
	}
	
	/**
	 * Uses the specified ability for the given player.
	 * 
	 * @param player
	 * 		The player to use the specified ability for.
	 */
	public void use(GamePlayer player) {
		
		if (player.getEnergy() < 100)
			return;
		
		Player bp = player.getBukkitPlayer();
		int level = player.getLevel(this);
		
		if (this == HEAL) {

			double hearts = 2.0 + 0.5 * (level - 1);
			Set<Player> effectPlayers = new HashSet<Player>();
			
			effectPlayers.add(bp);
			
			if (player.getGame().getArena().getType() != Arena.Type.FFA && player.getGame().getArena().getType() != Arena.Type.DUEL)
				for (Entity entity : bp.getNearbyEntities(5, 5, 5)) {
					if (!(entity instanceof Player))
						continue;
					
					Player ep = (Player) entity;
					GamePlayer entityAP = null;
					
					try {
						entityAP = MegaArena.getInstance().getPlayerManager().getPlayer(ep.getName());
					} catch (PlayerException e) {
					}
					
					if (!entityAP.isInGame())
						continue;
					
					if (entityAP.getGame().getArena().getType() == Arena.Type.TDM && entityAP.getSide() != player.getSide())
						continue;
					
					double newHealth = ((Damageable) ep).getHealth() + hearts;
					
					if (newHealth < 40)
						((Damageable) ep).setHealth(newHealth);
					else
						((Damageable) ep).setHealth(40D);
					
					effectPlayers.add((Player) ep);
					
					ep.sendMessage(String.format(
							Utils.getFormattedMessage(Utils.getFormattedMessage("messsages.healed-by-team")),
							player.getName()));
			}
			
			double newHealth = ((Damageable) bp).getHealth() + hearts;
			
			if (newHealth < 40)
				((Damageable) bp).setHealth(newHealth);
			else
				((Damageable) bp).setHealth(40D);
			
			for (Player effectPlayer : effectPlayers) {
				Location loc = effectPlayer.getEyeLocation();
				Vector vector = new Vector();
				
				for (int i = 0; i < 50; i++) {
					float alpha = ((3.1415927F / 2F) / 50) * i;
					double phi = Math.pow(Math.abs(Math.sin(2 * 2F * alpha)) + 0.8
							* Math.abs(Math.sin(2F * alpha)), 1 / 2D);
					
					vector.setY(phi * (Math.sin(alpha) + Math.cos(alpha)) * 1);
					vector.setZ(phi * (Math.cos(alpha) - Math.sin(alpha)) * 1);
					
					Location newLoc = loc.clone();
					Utils.rotateYAxis(vector, 50);
					newLoc.add(vector);
					newLoc.getWorld().playEffect(newLoc, Effect.HEART, 1);
				}
			}
			
			bp.sendMessage(String.format(Utils.getFormattedMessage("messages.heal-ability-used")));
			
		} else if (this == EXPLOSIVE_ARROW) {
			
			Projectile arrow = bp.launchProjectile(Arrow.class);
			arrow.setVelocity(arrow.getVelocity().multiply(2.0));
			arrow.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
			arrow.setShooter(bp);
			
		} else if (this == LIGHTNING) {
			
			int count = 0;
			
			for (Entity entity : player.getBukkitPlayer().getNearbyEntities(3, 3, 3)){
				if (!(entity instanceof Player))
					continue;
				
				Player entityPlayer = (Player) entity;
				GamePlayer entityAP = null;
				
				try {
					entityAP = MegaArena.getInstance().getPlayerManager().getPlayer(entityPlayer.getName());
				} catch (PlayerException e) {
				}
				
				if (!entityAP.isInGame())
					continue;
				
				if (entityAP.getGame().getArena().getType() == Arena.Type.TDM && entityAP.getSide() == player.getSide())
					continue;
				
				double dmg = 1.0 + 0.5 * (level - 1);
				
				entityPlayer.getWorld().strikeLightningEffect(entityPlayer.getLocation());
				Damageable dmgVictim = (Damageable) entityPlayer;
				dmgVictim.damage(0.00001D); // So the player will get the red damage
				
				if (dmgVictim.getHealth() <= dmg)
					dmgVictim.setHealth(0D);
				else
					dmgVictim.setHealth(dmgVictim.getHealth() - dmg);
				
				count++;
			}
			
			if (count == 0) {
				player.getBukkitPlayer().sendMessage(Utils.getFormattedMessage("messages.no-targets-in-range"));
				return;
			}
			
			player.getBukkitPlayer().sendMessage(String.format(
					Utils.getFormattedMessage("messages.ability-used"),
					name));
			
		} else if (this == EXPLODE) {
			
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> {
				Location loc = bp.getLocation().clone();
				
				loc.getWorld().spigot().playEffect(
						loc.add(0.0D, 0.8D, 0.0D),
						Effect.EXPLOSION_HUGE);
				
				for (Entity entity : bp.getNearbyEntities(5, 3, 5)) {
					if (!(entity instanceof Player))
						continue;
					
					Player entityPlayer = (Player) entity;
					GamePlayer entityAP = null;
					
					try {
						entityAP = MegaArena.getInstance().getPlayerManager().getPlayer(entityPlayer.getName());
					} catch (PlayerException e) {
					}
					
					if (!entityAP.isInGame())
						continue;
					
					if (entityAP.getGame().getArena().getType() == Arena.Type.TDM && entityAP.getSide() == player.getSide())
						continue;
					
					Damageable dmgVictim = (Damageable) entity;
					double dmg = 3.0 + 0.5 * (level - 1);
					
					((Damageable) entity).damage(0.00001D); // So the player will get the red damage
					
					if (dmgVictim.getHealth() <= dmg)
						dmgVictim.setHealth(0D);
					else
						dmgVictim.setHealth(dmgVictim.getHealth() - dmg);
				}
			}, 60L);
			
		} else if (this == TORNADO) {
			
			Location location = bp.getLocation();
			
			float duration = (float) (1.5 + (0.5 * level));
			double radius = 2;
			double maxHeight = 5;
			
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			particleTaskID = scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
				float elapsedTime = 0;
				
				@Override
				public void run() {
					
					if (elapsedTime >= duration)
						scheduler.cancelTask(particleTaskID);
					
					for (double y = 0; y < maxHeight; y+= 0.05) {
						double x = Math.sin(y * radius);
						double z = Math.cos(y * radius);
						
						Location newLoc = new Location(location.getWorld(), location.getX() + x,
								location.getY() + y, location.getZ() + z);
						
						for (Entity entity : Utils.getNearbyEntities(bp.getLocation(), 50)) {
							if (!(entity instanceof Player))
								continue;
							
							MegaArena.getInstance().getNMSUtils().playParticle((Player) entity, newLoc, "snowshovel");
						}
					}
					
					for (int i = 0; i < 5; i++) {
						float xRand = new Random().nextInt(2) - 1;
						float zRand = new Random().nextInt(2) - 1;
						
						Location newLoc = new Location(location.getWorld(), location.getX() + xRand,
								location.getY(), location.getZ() + zRand);
						
						for (Entity entity : Utils.getNearbyEntities(bp.getLocation(), 50)) {
							if (!(entity instanceof Player))
								continue;
							
							MegaArena.getInstance().getNMSUtils().playParticle((Player) entity, newLoc, "largesmoke");
						}
					}
					
					elapsedTime = elapsedTime + 0.5F;
				}
			}, 0L, (long) 10L);
			
			damageTaskID = scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
				int elapsedTime = 0;
				
				@Override
				public void run() {
					
					if (elapsedTime >= duration)
						scheduler.cancelTask(damageTaskID);
					
					for (Entity entity : Utils.getNearbyEntities(location, 5)) {
						if (!(entity instanceof Player))
							continue;
						
						Player entityPlayer = (Player) entity;
						
						if (entityPlayer.getLocation().getY() - location.getY() > 3)
							continue;
						
						GamePlayer entityGP = null;
						
						try {
							entityGP = MegaArena.getInstance().getPlayerManager().getPlayer(entityPlayer.getName());
						} catch (PlayerException e) {
						}
						
						if (!entityGP.isInGame())
							continue;
						
						if (entityGP.getGame().getArena().getType() == Arena.Type.TDM && entityGP.getSide() == player.getSide())
							continue;
						
						if (((Player) entity) == bp)
							continue;
						
						entityGP.setLastDamageCause(DamageCause.CUSTOM);
						((Damageable) entity).damage(0.00001D); // So the player will get the red damage
						entityGP.setLastDamageCause(DamageCause.CONTACT);
						
						double health = ((Damageable) entity).getHealth() - 2.0D;
						
						if (health > 0)
							((Damageable) entity).setHealth(health);
						else
							((Damageable) entity).setHealth(0D);
					}
					
					elapsedTime++;
				}
			}, 0L, 20L);
			
		} else if (this == WITHER_HEADS) {
			
			Vector originalVector = bp.getEyeLocation().getDirection();
			Vector rightVector = originalVector.clone();
			Vector leftVector =originalVector.clone();
			
			Utils.rotateYAxis(rightVector, 25);
			Utils.rotateYAxis(leftVector, -25);
			
			WitherSkull originalSkull = bp.launchProjectile(WitherSkull.class, originalVector);
			originalSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
			
			WitherSkull rightSkull = bp.launchProjectile(WitherSkull.class, rightVector);
			rightSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
			
			WitherSkull leftSkull = (WitherSkull) bp.launchProjectile(WitherSkull.class, leftVector);
			leftSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
		}
		
		player.setEnergy(0);	
	}
}