package com.andrewyunt.megaarena.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Arena.ArenaType;
import com.andrewyunt.megaarena.utilities.Utils;

import de.slikey.effectlib.effect.ExplodeEffect;
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
	
	HEAL("Heal"),
	SPLIT_ARROW("Split Arrow"),
	LIGHTNING("Lightning"),
	EXPLODE("Explode"),
	HURRICANE("Hurricane"),
	WITHER_HEADS("Master's Attack");
	
	private String name;
	
	Ability(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void use(GamePlayer player) {
		
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
					GamePlayer entityAP = null;
					
					try {
						entityAP = MegaArena.getInstance().getPlayerManager().getPlayer(ep.getName());
					} catch (PlayerException e) {
					}
					
					if (!entityAP.isInGame())
						continue;
					
					if (entityAP.getGame().getArena().getType() == ArenaType.TDM && entityAP.getSide() == player.getSide())
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
			
		} else if (this == SPLIT_ARROW) {
			
			Projectile arrow = bp.launchProjectile(Arrow.class);
			arrow.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
			arrow.setShooter(bp);
			
		} else if (this == LIGHTNING) {
			
			int count = 0;
			for (Entity entity : player.getBukkitPlayer().getNearbyEntities(5, 3, 5)){
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
				
				if (entityAP.getGame().getArena().getType() == ArenaType.TDM && entityAP.getSide() == player.getSide())
					continue;
				
				double dmg = 1.0 + 0.5 * (getLevel(player) - 1);
				
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
				
				if (entityAP.getGame().getArena().getType() == ArenaType.TDM && entityAP.getSide() == player.getSide())
					continue;
				
				Damageable dmgVictim = (Damageable) entity;
				double dmg = 3.0 + 0.5 * (getLevel(player) - 1);
				
				((Damageable) entity).damage(0.00001D, bp);
				
				if (dmgVictim.getHealth() <= dmg)
					dmgVictim.setHealth(0D);
				else
					dmgVictim.setHealth(dmgVictim.getHealth() - dmg);
			}
			
		} else if (this == HURRICANE) {
			
	        TornadoEffect tornadoEffect = new TornadoEffect(MegaArena.getInstance().getEffectManager());
	        
	        tornadoEffect.tornadoParticle = ParticleEffect.SMOKE_LARGE;
	        tornadoEffect.particleCount = 25;
	        tornadoEffect.iterations = 5;
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
	            		
	            		Player entityPlayer = (Player) entity;
	    				GamePlayer entityAP = null;
	    				
	    				try {
	    					entityAP = MegaArena.getInstance().getPlayerManager().getPlayer(entityPlayer.getName());
	    				} catch (PlayerException e) {
	    				}
	    				
	    				if (!entityAP.isInGame())
	    					continue;
	    				
						if (entityAP.getGame().getArena().getType() == ArenaType.TDM && entityAP.getSide() == player.getSide())
							continue;
	            		
	            		if (((Player) entity) == bp)
	            			continue;
	            		((Damageable)entity).damage(0.00001D, player.getBukkitPlayer()); // So the player will get the kill. and the red invisibility period 
	            		double health = ((Damageable) entity).getHealth() - 3.0D;
	            		
	            		if (health > 0)
	            			((Damageable) entity).setHealth(health);
	            		else
	            			((Damageable) entity).setHealth(0D);
	            		
	            		Vector tornadoVector = tornadoEffect.getLocation().toVector();
	            		Vector entityVector = entity.getLocation().add(0, 3, 0).toVector();
	            		Vector answer = entityVector.subtract(tornadoVector);
	            		entity.setVelocity(answer.multiply(0.12));
	            		
	            		player.addEnergy(Class.SPIRIT_WARRIOR.getEnergyPerClick());
	            	}
	            }
	        }.runTaskTimer(MegaArena.getInstance(), 0L, 20L);
			
		} else if (this == WITHER_HEADS) {
			
			Vector middleVector = bp.getEyeLocation().getDirection();
			Vector leftVector = new Vector(middleVector.getZ(), middleVector.getY(), -middleVector.getX());
			Vector rightVector = new Vector(-middleVector.getZ(), middleVector.getY(), middleVector.getX());
			
            WitherSkull leftSkull = bp.launchProjectile(WitherSkull.class);
            leftSkull.setShooter(bp);
            leftSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
 
            WitherSkull middleSkull = bp.launchProjectile(WitherSkull.class);
            middleSkull.setShooter(bp);
            middleSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
     
            WitherSkull rightSkull = bp.launchProjectile(WitherSkull.class);
            rightSkull.setShooter(bp);
            rightSkull.setMetadata("MegaArena", new FixedMetadataValue(MegaArena.getInstance(), true));
		}
		
		player.setEnergy(0);	
	}
	
	public int getLevel(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("megaarena.%s.%s", this.toString().toLowerCase(), i)))
				return i;
		
		return 1; 
	}
	
	public void setLevel(GamePlayer player, int level) {
		
		MegaArena.getInstance().getServer().dispatchCommand(MegaArena.getInstance().getServer().getConsoleSender(),
				String.format("pex user %s add megaarena.%s.%s", player.getName(), this.toString().toLowerCase(), level));
	}
}