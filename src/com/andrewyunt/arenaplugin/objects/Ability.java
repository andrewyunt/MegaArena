package com.andrewyunt.arenaplugin.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.utilities.Utils;

import de.slikey.effectlib.effect.HeartEffect;
import de.slikey.effectlib.effect.TornadoEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;

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

			double hearts = 2.0 + 0.5*(getLevel(player)-1);
			Set<Player> effectPlayers = new HashSet<Player>();
			
			effectPlayers.add(bp);
			
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
					
					effectPlayers.add((Player) ep);
					
					ep.sendMessage(String.format(ChatColor.GOLD + "You have been healed by %s.", player.getName()));
				}
			
			double newHealth = ((Damageable) bp).getHealth() + hearts;
			
			if (newHealth < 40)
				((Damageable) bp).setHealth(newHealth);
			else
				((Damageable) bp).setHealth(40D);
			
			for (Player effectPlayer : effectPlayers) {
		        HeartEffect heartEffect = new HeartEffect(ArenaPlugin.getInstance().getEffectManager());
		        heartEffect.particle = ParticleEffect.HEART;
		        heartEffect.setDynamicOrigin(new DynamicLocation(effectPlayer.getEyeLocation()));
		        heartEffect.start();
			}
			
			bp.sendMessage(String.format(ChatColor.GOLD + "You have used the heal ability" + 
			(((Damageable) bp).getHealth() < 40 ? String.format(" and have restored %s hearts.", hearts / 2) : ".")));
			
		} else if (this == SPLIT_ARROW) {
			
			Projectile arrow = bp.launchProjectile(Arrow.class);
			arrow.setMetadata("ArenaPlugin", new FixedMetadataValue(ArenaPlugin.getInstance(), true));
			arrow.setShooter(bp);
			
		} else if (this == LIGHTNING) {
			int count=0;
			for (Entity e : player.getBukkitPlayer().getNearbyEntities(5, 3, 5)){
				if (!(e instanceof Player))
					continue;
				try {
					ArenaPlugin.getInstance().getPlayerManager().getPlayer(((Player)e).getName());
				} catch (PlayerException e1) {
					continue; // Player isn't playing
				}
				double dmg = 1.0 + 0.5*(getLevel(player)-1);
				Player victim = (Player) e;
				count++;
				victim.getWorld().strikeLightningEffect(victim.getLocation());
				Damageable dmgVictim = (Damageable) victim;
				dmgVictim.damage(0.00001D, player.getBukkitPlayer()); // Just so an actual hit will register 
				if (dmgVictim.getHealth() <= dmg)
					dmgVictim.setHealth(0D);
				else
					dmgVictim.setHealth(dmgVictim.getHealth()-dmg);
			}
			if (count==0){
			player.getBukkitPlayer().sendMessage(ChatColor.RED+"No targets within range found!");
			return;
			}
			player.getBukkitPlayer().sendMessage(ChatColor.GOLD+"You have used your Lightning Strike ability.");
			
		} else if (this == EXPLODE) {
			
		} else if (this == HURRICANE) {
			
	        TornadoEffect tornadoEffect = new TornadoEffect(ArenaPlugin.getInstance().getEffectManager());
	        
	        tornadoEffect.tornadoParticle = ParticleEffect.SMOKE_LARGE;
	        tornadoEffect.duration = 1500 + (500 * getLevel(player));
	        tornadoEffect.setDynamicOrigin(new DynamicLocation(bp.getLocation()));
	        
	        tornadoEffect.start();
	        new BukkitRunnable() {
	            public void run() {
	            	
	            	if (tornadoEffect.isDone())
	                    this.cancel();
	            	for (Entity entity : Utils.getNearbyEntities(tornadoEffect.getLocation(), 5)) {
	            		if (!(entity instanceof Player))
	            			continue;
	            		
	            		if (((Player) entity) == bp)
	            			continue;
	            		((Damageable)entity).damage(0.00001D, player.getBukkitPlayer()); // So the player will get the kill. and the red invisibility period 
	            		double health = ((Damageable) entity).getHealth() - 3.0D;
	            		
	            		if (health > 0)
	            			((Damageable) entity).setHealth(health);
	            		else
	            			((Damageable) entity).setHealth(0D);
	            		
	            		player.addEnergy(Class.SPIRIT_WARRIOR.getEnergyPerClick());
	            		Vector tornadoVector = tornadoEffect.getLocation().toVector();
	            		Vector entityVector = entity.getLocation().add(0, 3, 0).toVector();
	            		Vector answer = entityVector.subtract(tornadoVector);
	            		entity.setVelocity(answer.multiply(0.12));
	            	}
	            }
	        }.runTaskTimer(ArenaPlugin.getInstance(), 0L, 20L);
			
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