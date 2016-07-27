package com.andrewyunt.arenaplugin.listeners;

import static com.andrewyunt.arenaplugin.objects.Class.SKELETON;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.managers.PlayerManager;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.utilities.Utils;

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
		
		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
			
			if (!ap.isInGame())
				return;
			
			if (ap.getClassType() == SKELETON)
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
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		
		Player player = event.getPlayer();
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (!ap.isInGame())
			return;
		
		if (Utils.getTargetPlayer(player) != null)
			ap.addEnergy(ap.getClassType().getEnergyPerClick());
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
	
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Arrow))
			return;
		
		if (!entity.hasMetadata("ArenaPlugin"))
			return;
		
		ArenaPlayer shooter = null;
		
		try {
			shooter = ArenaPlugin.getInstance().getPlayerManager().getPlayer(((Player) ((Projectile) entity).getShooter()).getName());
		} catch (PlayerException e) {
		}
		
		for (Entity nearby : entity.getNearbyEntities(5D, 3D, 5D)) {
			if (!(nearby instanceof Player))
				continue;
			
			Player nearbyPlayer = (Player) nearby;
			
			double dmg = 1.5 + (shooter.getClassType().getAbility().getLevel(shooter) * .5);
			nearbyPlayer.setHealth(((Damageable) nearbyPlayer).getHealth() - dmg);
		}
		
		entity.getWorld().createExplosion(event.getEntity().getLocation(), 5F);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		
	    if (event.getEntity() instanceof Player && event.getCause().equals(DamageCause.ENTITY_EXPLOSION))
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
}