package com.andrewyunt.arenaplugin.listeners;

import static com.andrewyunt.arenaplugin.objects.Class.SKELETON;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.managers.PlayerManager;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.utilities.Utils;

import de.slikey.effectlib.effect.ExplodeEffect;
import de.slikey.effectlib.util.DynamicLocation;

public class ArenaPluginPlayerAbilityListener implements Listener {
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		ItemStack item = event.getItem();
		
		if (item == null)
			return;
		
		Material type = item.getType();
		Action action = event.getAction();
		Player player = event.getPlayer();
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR){
			
			if (!ap.isInGame())
				return;
			
			if (ap.getClassType() == SKELETON)
				return;
			
			if (!type.toString().toLowerCase().contains("sword"))
				return;
			
			ap.getClassType().getAbility().use(ap);
			
		} else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
			
			if (!ap.isInGame())
				return;
			
			if (ap.getClassType() == SKELETON)
				if (type == Material.BOW)
					ap.getClassType().getAbility().use(ap);
		}
	}
	
 	@EventHandler (priority = EventPriority.MONITOR)
	    public void EPH(EntityDamageByEntityEvent e){
	    	if (!(e.getDamager() instanceof Player))
	    		return;
	    	if (!(e.getEntity() instanceof Player))
	    		return;
	    	
	    	Player player = (Player) e.getDamager();
	    	Player target = (Player) e.getEntity();
	    	ArenaPlayer targetAP = null;
			ArenaPlayer playerAP = null;
			
			try {
				playerAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
			} catch (PlayerException e1) {
			}
			try {
				targetAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(target.getName());
			} catch (PlayerException e1) {
			}
			
			if (!playerAP.isInGame())
				return;
			if (playerAP.getClassType() == SKELETON)
				return;
			if (!targetAP.isInGame())
				return;
			if (targetAP.getGame().getArena().getType() == ArenaType.TDM && targetAP.getSide() == playerAP.getSide())
				return;
			if (Utils.getTargetPlayer(player) != null)
				return;
			
	    	playerAP.addEnergy(playerAP.getClassType().getEnergyPerClick());
	    }
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void EPC(PlayerInteractEvent event) {
		
		if (!(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK))
			return;
		
		Player player = event.getPlayer();
		ArenaPlayer playerAP = null;
		
		try {
			playerAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (!playerAP.isInGame())
			return;
		
		if (playerAP.getClassType() == SKELETON)
			return;
		if (Utils.getTargetPlayer(player) == null)
			return;
		playerAP.addEnergy(playerAP.getClassType().getEnergyPerClick());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event){
		
		if (event.getCause() == DamageCause.ENTITY_EXPLOSION && (event.getDamager().getType() != EntityType.PRIMED_TNT))
			event.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity damaged = event.getEntity();
		Entity damager = event.getDamager();
		
		if (!(damaged instanceof Player) || !(damager instanceof Projectile))
			return;
		
		PlayerManager playerManager = ArenaPlugin.getInstance().getPlayerManager();
		
		Player damagedPlayer = (Player) damaged;
		Player damagerPlayer = null;
		
		if (damager instanceof Player)
			damagerPlayer = (Player) damager;
		else
			damagerPlayer = (Player) ((Projectile) damager).getShooter();
		
		ArenaPlayer damagedAP = null;
		ArenaPlayer damagerAP = null;
		
		try {
			damagedAP = playerManager.getPlayer(damagedPlayer.getName());
			damagerAP = playerManager.getPlayer(damagerPlayer.getName());
		} catch (PlayerException e) {
		}
		
		if (damagedAP == damagerAP)
			return;
		
		if (!damagerAP.isInGame() || !damagedAP.isInGame())
			return;
		
		if ((damagerAP.getSide() == damagedAP.getSide()) && damagerAP.getGame().getArena().getType() == ArenaType.TDM)
			return;
		
		if (damager instanceof Projectile) {
			
			if (damagerAP.getClassType() != SKELETON)
				return;
			
			damagerAP.addEnergy(damagerAP.getClassType().getEnergyPerClick());
			
		} else {
			
			if (damagerAP.getClassType() == SKELETON)
				return;
			
			Material type = damagerPlayer.getItemInHand().getType();
			
			if (type != Material.STONE_SWORD && type != Material.IRON_SWORD && type != Material.DIAMOND_SWORD)
				return;
			
			damagerAP.addEnergy(damagerAP.getClassType().getEnergyPerClick());
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		
		Entity entity = event.getEntity();
		
		if (!entity.hasMetadata("ArenaPlugin"))
			return;

		if (entity.getType() != EntityType.WITHER_SKULL)
			return;
		
		event.setCancelled(true);
		
		ArenaPlayer shooter = null;
		
		try {
			shooter = ArenaPlugin.getInstance().getPlayerManager().getPlayer(((Player) ((Projectile) entity).getShooter()).getName());
		} catch (PlayerException e) {
		}
		
		Player bukkitShooter = shooter.getBukkitPlayer();
		
        ExplodeEffect explodeEffect = new ExplodeEffect(ArenaPlugin.getInstance().getEffectManager());
        
        explodeEffect.amount = 10;
        explodeEffect.setDynamicOrigin(new DynamicLocation(entity.getLocation()));
        
        explodeEffect.start();
		
		for (Entity nearby : entity.getNearbyEntities(3D, 3D, 3D)) {
			if (!(nearby instanceof Player))
				continue;
			
			if (nearby == bukkitShooter)
				continue;
			
			Player nearbyPlayer = (Player) nearby;
			
			double dmg = 1.5 + (shooter.getClassType().getAbility().getLevel(shooter) * 0.5);
			Damageable dmgPlayer = (Damageable) nearbyPlayer;
			dmgPlayer.damage(0.00001D, bukkitShooter);// So the player will get the kill as well as red damage and invisibility
		    
			if (dmgPlayer.getHealth() < dmg) {
		    	dmgPlayer.setHealth(0D);
		    	return;
		    } else
		    	nearbyPlayer.setHealth(((Damageable) nearbyPlayer).getHealth() - dmg);
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
	
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Arrow))
			return;
		
		if (!entity.hasMetadata("ArenaPlugin"))
			return;
		
		Player shooter = (Player) ((Projectile) entity).getShooter();
		ArenaPlayer shooterAP = null;
		
		try {
			shooterAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(shooter.getName());
		} catch (PlayerException e) {
		}
		
        ExplodeEffect explodeEffect = new ExplodeEffect(ArenaPlugin.getInstance().getEffectManager());
        
        explodeEffect.amount = 10;
        explodeEffect.setDynamicOrigin(new DynamicLocation(entity.getLocation()));
        
        explodeEffect.start();
		
		for (Entity nearby : entity.getNearbyEntities(5D, 3D, 5D)) {
			if (!(nearby instanceof Player))
				continue;
			
			if (nearby == shooter)
				continue;
			
			Player nearbyPlayer = (Player) nearby;
			
			ArenaPlayer nearbyAP = null;
			
			try {
				nearbyAP = ArenaPlugin.getInstance().getPlayerManager().getPlayer(nearbyPlayer.getName());
			} catch (PlayerException e) {
			}
			
			if (!nearbyAP.isInGame())
				continue;
			
			if (nearbyAP.getGame().getArena().getType() == ArenaType.TDM && nearbyAP.getSide() == shooterAP.getSide())
				continue;
			
			double dmg = 1.5 + (shooterAP.getClassType().getAbility().getLevel(shooterAP) * 0.5);
			Damageable dmgPlayer = (Damageable) nearbyPlayer;
			dmgPlayer.damage(0.00001D, shooter);// So the player will get the kill as well as red damage and invisibility
		    
			if (dmgPlayer.getHealth() < dmg) {
		    	dmgPlayer.setHealth(0D);
		    	return;
		    } else
		    	nearbyPlayer.setHealth(((Damageable) nearbyPlayer).getHealth() - dmg);
		}
	}
}