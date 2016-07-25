package com.andrewyunt.arenaplugin.listeners;

import static com.andrewyunt.arenaplugin.objects.Class.*;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;

public class ArenaPluginPlayerAbilityListener implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		ItemStack item = event.getItem();
		
		if (item == null)
			return;
		
		Material type = item.getType();
		
		if (!(type == Material.DIAMOND_SWORD || type == Material.IRON_SWORD || type == Material.BOW))
			return;
		
		Action action = event.getAction();
		Player player = event.getPlayer();
		ArenaPlayer ap = null;
		
		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
			
			if (ap.getClassType() == SKELETON)
				return;
			
			if (type == Material.BOW)
				return;
			
			ap.getClassType().getAbility().use(ap);
			
		} else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
			
			if (ap.getClassType() != SKELETON)
				return;
			
			if (type != Material.BOW)
				return;
			
			ap.getClassType().getAbility().use(ap);
		}
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
}