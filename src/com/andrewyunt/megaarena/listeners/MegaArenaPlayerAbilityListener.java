package com.andrewyunt.megaarena.listeners;

import static com.andrewyunt.megaarena.objects.Class.SKELETON;

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

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.managers.PlayerManager;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.Utils;

import de.slikey.effectlib.effect.ExplodeEffect;
import de.slikey.effectlib.util.DynamicLocation;

public class MegaArenaPlayerAbilityListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {

		ItemStack item = event.getItem();

		if (item == null)
			return;

		Material type = item.getType();
		Action action = event.getAction();
		Player player = event.getPlayer();
		GamePlayer gp = null;

		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {

			if (!gp.isInGame())
				return;

			if (gp.getClassType() == SKELETON)
				return;

			if (!type.toString().toLowerCase().contains("sword"))
				return;

			gp.getClassType().getAbility().use(gp);

		} else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {

			if (!gp.isInGame())
				return;

			if (gp.getClassType() == SKELETON)
				if (type == Material.BOW)
					gp.getClassType().getAbility().use(gp);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEPH(EntityDamageByEntityEvent event) {
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getDamager();
		Player target = (Player) event.getEntity();
		GamePlayer targetAP = null;
		GamePlayer playerGP = null;

		try {
			targetAP = MegaArena.getInstance().getPlayerManager().getPlayer(target.getName());
			playerGP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		if (!playerGP.isInGame())
			return;
		
		if (playerGP.getClassType() == SKELETON)
			return;
		
		if (!targetAP.isInGame())
			return;
		
		if (targetAP.getGame().getArena().getType() == Arena.Type.TDM && targetAP.getSide() == playerGP.getSide())
			return;
		
		if (Utils.getTargetPlayer(player) != null)
			return;

		playerGP.addEnergy(playerGP.getClassType().getEnergyPerClick());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEPC(PlayerInteractEvent event) {

		if (!(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK))
			return;

		Player player = event.getPlayer();
		GamePlayer playerGP = null;

		try {
			playerGP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		if (!playerGP.isInGame())
			return;

		if (playerGP.getClassType() == SKELETON)
			return;
		
		if (Utils.getTargetPlayer(player) == null)
			return;
		
		playerGP.addEnergy(playerGP.getClassType().getEnergyPerClick());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {

		if (event.getCause() == DamageCause.ENTITY_EXPLOSION && (event.getDamager().getType() != EntityType.PRIMED_TNT))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		Entity damaged = event.getEntity();
		Entity damager = event.getDamager();

		if (!(damaged instanceof Player) || !(damager instanceof Projectile))
			return;

		PlayerManager playerManager = MegaArena.getInstance().getPlayerManager();

		Player damagedPlayer = (Player) damaged;
		Player damagerPlayer = null;

		if (damager instanceof Player)
			damagerPlayer = (Player) damager;
		else
			damagerPlayer = (Player) ((Projectile) damager).getShooter();

		GamePlayer damagedGP = null;
		GamePlayer damagerGP = null;

		try {
			damagedGP = playerManager.getPlayer(damagedPlayer.getName());
			damagerGP = playerManager.getPlayer(damagerPlayer.getName());
		} catch (PlayerException e) {
		}

		if (damagedGP == damagerGP)
			return;

		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if ((damagerGP.getSide() == damagedGP.getSide()) && damagerGP.getGame().getArena().getType() == Arena.Type.TDM)
			return;

		if (damager instanceof Projectile) {

			if (damagerGP.getClassType() != SKELETON)
				return;

			damagerGP.addEnergy(damagerGP.getClassType().getEnergyPerClick());

		} else {

			if (damagerGP.getClassType() == SKELETON)
				return;

			Material type = damagerPlayer.getItemInHand().getType();

			if (type != Material.STONE_SWORD && type != Material.IRON_SWORD && type != Material.DIAMOND_SWORD)
				return;

			damagerGP.addEnergy(damagerGP.getClassType().getEnergyPerClick());
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {

		Entity entity = event.getEntity();

		if (entity.getType() != EntityType.WITHER_SKULL)
			return;
		
		if (!entity.hasMetadata("MegaArena"))
			return;

		event.setCancelled(true);

		GamePlayer shooter = null;

		try {
			shooter = MegaArena.getInstance().getPlayerManager()
					.getPlayer(((Player) ((Projectile) entity).getShooter()).getName());
		} catch (PlayerException e) {
		}

		Player bukkitShooter = shooter.getBukkitPlayer();

		ExplodeEffect explodeEffect = new ExplodeEffect(MegaArena.getInstance().getEffectManager());

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
			dmgPlayer.damage(0.00001D, bukkitShooter);// So the player will get the kill as well as
													  // red damage and invisibility
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

		if (!entity.hasMetadata("MegaArena"))
			return;

		Player shooter = (Player) ((Projectile) entity).getShooter();
		GamePlayer shooterAP = null;

		try {
			shooterAP = MegaArena.getInstance().getPlayerManager().getPlayer(shooter.getName());
		} catch (PlayerException e) {
		}

		ExplodeEffect explodeEffect = new ExplodeEffect(MegaArena.getInstance().getEffectManager());

		explodeEffect.amount = 10;
		explodeEffect.setDynamicOrigin(new DynamicLocation(entity.getLocation()));

		explodeEffect.start();

		for (Entity nearby : entity.getNearbyEntities(5D, 3D, 5D)) {
			if (!(nearby instanceof Player))
				continue;

			if (nearby == shooter)
				continue;

			Player nearbyPlayer = (Player) nearby;

			GamePlayer nearbyAP = null;

			try {
				nearbyAP = MegaArena.getInstance().getPlayerManager().getPlayer(nearbyPlayer.getName());
			} catch (PlayerException e) {
			}

			if (!nearbyAP.isInGame())
				continue;

			if (nearbyAP.getGame().getArena().getType() == Arena.Type.TDM && nearbyAP.getSide() == shooterAP.getSide())
				continue;

			double dmg = 1.5 + (shooterAP.getClassType().getAbility().getLevel(shooterAP) * 0.5);
			Damageable dmgPlayer = (Damageable) nearbyPlayer;
			dmgPlayer.damage(0.00001D, shooter);// So the player will get the
												// kill as well as red damage
												// and invisibility

			if (dmgPlayer.getHealth() < dmg) {
				dmgPlayer.setHealth(0D);
				return;
			} else
				nearbyPlayer.setHealth(((Damageable) nearbyPlayer).getHealth() - dmg);
		}
	}
}