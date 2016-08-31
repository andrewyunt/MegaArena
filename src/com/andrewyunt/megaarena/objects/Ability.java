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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.utilities.DirectionUtils;
import com.andrewyunt.megaarena.utilities.DirectionUtils.CardinalDirection;
import com.andrewyunt.megaarena.utilities.Utils;

import de.slikey.effectlib.effect.ExplodeEffect;
import de.slikey.effectlib.effect.HeartEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;

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
		int level = MegaArena.getInstance().getDataSource().getLevel(player, this);
		
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
					
					ep.sendMessage(String.format(ChatColor.GREEN + "You have been healed by %s.",
							ChatColor.AQUA + player.getName() + ChatColor.GREEN));
			}
			
			double newHealth = ((Damageable) bp).getHealth() + hearts;
			
			if (newHealth < 40)
				((Damageable) bp).setHealth(newHealth);
			else
				((Damageable) bp).setHealth(40D);
			
			for (Player effectPlayer : effectPlayers) {
		        HeartEffect heartEffect = new HeartEffect(MegaArena.getInstance().getEffectManager());
		        heartEffect.particle = ParticleEffect.HEART;
		        heartEffect.setDynamicOrigin(new DynamicLocation(effectPlayer.getLocation()));
		        heartEffect.start();
			}
			
			bp.sendMessage(String.format(ChatColor.GREEN + "You have used the %s ability" + 
			(((Damageable) bp).getHealth() < 40 ? String.format(" and have restored %s hearts.",
					ChatColor.AQUA + String.valueOf(hearts / 2) + ChatColor.GREEN) : "."),
			ChatColor.AQUA + name + ChatColor.GREEN));
			
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
				dmgVictim.damage(0.00001D, bp); // Just so an actual hit will register 
				
				if (dmgVictim.getHealth() <= dmg)
					dmgVictim.setHealth(0D);
				else
					dmgVictim.setHealth(dmgVictim.getHealth() - dmg);
				
				count++;
			}
			
			if (count == 0) {
				player.getBukkitPlayer().sendMessage(ChatColor.RED + "No targets within range found!");
				return;
			}
			
			player.getBukkitPlayer().sendMessage(ChatColor.GREEN + String.format("You have used the %s ability.",
					ChatColor.AQUA + name + ChatColor.GREEN));
			
		} else if (this == EXPLODE) {
			
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
				@Override
				public void run() {
					
					if (!player.isInGame())
						return;
					
					ExplodeEffect explodeEffect = new ExplodeEffect(MegaArena.getInstance().getEffectManager());
	    	        
	    	        explodeEffect.amount = 10;
	    	        explodeEffect.setDynamicOrigin(new DynamicLocation(bp.getLocation()));
	    	        
	    	        explodeEffect.start();
	    			
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
	    				
	    				((Damageable) entity).damage(0.00001D, bp);
	    				
	    				if (dmgVictim.getHealth() <= dmg)
	    					dmgVictim.setHealth(0D);
	    				else
	    					dmgVictim.setHealth(dmgVictim.getHealth() - dmg);
	    			}
	    		}
			}, 60L);
			
		} else if (this == TORNADO) {
			
			Location location = bp.getLocation();
			
			float duration = (float) (1.5 + (0.5 * level));
			double radius = 2;
			double maxHeight = 5;
			
	        BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
	            float elapsedTime = 0;
	        	
	        	@Override
	            public void run() {
	            	
	            	if (elapsedTime >= duration)
	            		return;
	            	
	    			for (double y = 0; y < maxHeight; y+= 0.05) {
	    				double x = Math.sin(y * radius);
	    				double z = Math.cos(y * radius);
	    				
	    				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
	    						"snowshovel",
	    						((float) (location.getX() + x)),
	    						((float) (location.getY() + y)),
	    						((float) (location.getZ() + z)),
	    						0, 0, 0, 1, 0);
	    				
	    				for (Entity entity : Utils.getNearbyEntities(bp.getLocation(), 50)) {
	    					if (!(entity instanceof Player))
	    						return;
	    					
	    					((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
	    				}
	    			}
	    			
	    			for (int i = 0; i < 5; i++) {
	    				float xRand = new Random().nextInt(2) - 1;
	    				float zRand = new Random().nextInt(2) - 1;
	    				
	    				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
	    						"largesmoke",
	    						((float) (location.getX() + xRand)),
	    						((float) location.getY()),
	    						((float) (location.getZ() + zRand)),
	    						0, 0, 0, 1, 0);
	    				
	    				for (Entity entity : Utils.getNearbyEntities(bp.getLocation(), 50)) {
	    					if (!(entity instanceof Player))
	    						return;
	    					
	    					((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
	    				}
	    			}
	    			
	    			elapsedTime = elapsedTime + 0.5F;
	            }
	        }, 0L, (long) 10L);
	        
	        new BukkitRunnable() {
	        	int elapsedTime = 0;
	        	
	        	public void run() {
	            	
	            	if (elapsedTime >= duration)
	            		return;
	            	
	            	for (Entity entity : Utils.getNearbyEntities(location, 5)) {	
	            		if (!(entity instanceof Player))
	            			continue;
	            		
	            		Player entityPlayer = (Player) entity;
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
	            		((Damageable) entity).damage(0.00001D, player.getBukkitPlayer()); // So the player will get the kill and the red invisibility period 
	            		entityGP.setLastDamageCause(DamageCause.CONTACT);
	            		
	            		double health = ((Damageable) entity).getHealth() - 2.0D;
	            		
	            		if (health > 0)
	            			((Damageable) entity).setHealth(health);
	            		else
	            			((Damageable) entity).setHealth(0D);
	            	}
	            	
	            	elapsedTime++;
	            }
	        }.runTaskTimer(MegaArena.getInstance(), 0L, 20L);
			
		} else if (this == WITHER_HEADS) {
			
			Location eyeLocation = bp.getEyeLocation();
			CardinalDirection playerDir = DirectionUtils.getCardinalDirection(eyeLocation);
			
			Vector originalVector = bp.getEyeLocation().getDirection();
            Vector rightVector = Utils.rotateYAxis(originalVector, 25);
            Vector leftVector = Utils.rotateYAxis(originalVector, -25);
            
            WitherSkull originalSkull = bp.launchProjectile(WitherSkull.class);
            originalSkull.setShooter(bp);
            originalSkull.setVelocity(originalVector.multiply(1));
            originalSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
            
            WitherSkull rightSkull = (WitherSkull) bp.getWorld().spawnEntity(
            		DirectionUtils.getRelativeLocation(eyeLocation, playerDir.rotate(90), 1),
            		EntityType.WITHER_SKULL);
            rightSkull.setShooter(bp);
            rightSkull.setVelocity(rightVector.multiply(1));
            rightSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
            
            WitherSkull leftSkull = (WitherSkull) bp.getWorld().spawnEntity(
            		DirectionUtils.getRelativeLocation(eyeLocation, playerDir.rotate(-90), 1),
            		EntityType.WITHER_SKULL);
            leftSkull.setShooter(bp);
            leftSkull.setVelocity(leftVector.multiply(1));
            leftSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
		}
		
		player.setEnergy(0);	
	}
}