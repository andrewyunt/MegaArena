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
package com.andrewyunt.megaarena.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Skill;

/**
 * The listener class used for skills which holds methods to listen on events.
 * 
 * @author Andrew Yunt
 * @author MaccariTA
 */
public class PlayerSkillListener implements Listener {

	private HashMap<UUID, Player> creeperTNT = new HashMap<UUID, Player>();
	private HashMap<UUID, GamePlayer> explosiveWeaknessTNT = new HashMap<UUID, GamePlayer>();
	
	@EventHandler
	public void removeEffects(PlayerDeathEvent e) {
		
		if (!(e.getEntity() instanceof Player))
			return;
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), new Runnable() {
			@Override
			public void run() {
				
				e.getEntity().getActivePotionEffects().clear();
			}
		}, 20L);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void boomerangSkill(EntityDamageByEntityEvent event) {

		if (event.isCancelled())
			return;
		
		// Checking for a bow hit from a player to a player
		if (!(event.getDamager() instanceof Arrow))
			return;

		final Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) event.getEntity();
		
		if (shooter == damaged)
			return;

		GamePlayer shooterGP = null;
		GamePlayer damagedGP = null;

		try {
			shooterGP = MegaArena.getInstance().getPlayerManager().getPlayer(shooter.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!shooterGP.isInGame() || !damagedGP.isInGame())
			return;

		if (shooterGP.getGame().getArena().getType() == Arena.Type.TDM && shooterGP.getSide() == damagedGP.getSide())
			return;

		double percentage = 0;

		if (shooterGP.getClassType().getSkillOne() == Skill.BOOMERANG)
			percentage = 0.2 * shooterGP.getLevel(shooterGP.getClassType().getSkillOne());
		else if (shooterGP.getClassType().getSkillTwo() == Skill.BOOMERANG)
			percentage = 0.2 * shooterGP.getLevel(shooterGP.getClassType().getSkillTwo());
		else
			return;

		if (Math.random() > percentage)
			return;

		shooter.getInventory().addItem(new ItemStack(Material.ARROW));

		shooter.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.BOOMERANG.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void weakeningArrow(EntityDamageByEntityEvent event) {
		
		// Checking for a bow hit from a player to a player
		if (!(event.getDamager() instanceof Arrow))
			return;
		
		Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		// Casting to players
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) event.getEntity();

		if (shooter == damaged)
			return;
		
		GamePlayer shooterGP = null;
		GamePlayer damagedGP = null;

		try {
			shooterGP = MegaArena.getInstance().getPlayerManager().getPlayer(shooter.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!shooterGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (shooterGP.getGame().getArena().getType() == Arena.Type.TDM && shooterGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;

		if (shooterGP.getClassType().getSkillOne() == Skill.WEAKENING_ARROW)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (shooterGP.getClassType().getSkillTwo() == Skill.WEAKENING_ARROW)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;
		
		// Apply effects
		int duration = (int) (2 + 0.5 * (skillLevel - 1)) * 20;
		PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, duration, 1, true);
		PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, duration, 0, true);

		shooter.addPotionEffect(regen, true);
		damaged.addPotionEffect(weakness, true);

		shooter.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.WEAKENING_ARROW.getName() + ChatColor.GREEN));
		damaged.sendMessage(String.format(ChatColor.RED + "%s's arrow inflicted you with Weakness II for %s seconds.",
				shooter.getName(), String.valueOf(duration / 20)));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void resist(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Check if damager and damaged entities are players
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.RESIST)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.RESIST)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;
		
		double percentage = 0.11 + 0.03 * (skillLevel - 1);

		if (Math.random() > percentage)
			return;

		PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, true);

		damaged.addPotionEffect(resistance, true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.RESIST.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void swiftness(EntityDamageByEntityEvent event) {
		
		// Checking for a bow hit from a player to a player
		if (!(event.getDamager() instanceof Arrow))
			return;

		final Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) event.getEntity();

		GamePlayer shooterGP = null;
		GamePlayer damagedGP = null;

		try {
			shooterGP = MegaArena.getInstance().getPlayerManager().getPlayer(shooter.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!shooterGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (shooterGP.getGame().getArena().getType() == Arena.Type.TDM && shooterGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		if (damagedGP.getClassType().getSkillOne() == Skill.SWIFTNESS)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SWIFTNESS)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;
		
		double percentage = 0.10 + 0.05 * (skillLevel - 1);
		
		if (Math.random() > percentage)
			return;
		
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 60, 1, true);
		
		Collection<PotionEffect> effects = damaged.getActivePotionEffects();
		for (PotionEffect e : effects)
			if (e.getType() == PotionEffectType.SPEED)
				if (e.getAmplifier() >= 2)
					return;
				else
					if (e.getDuration() >= 60)
						return;
		
		damaged.addPotionEffect(speed, true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SWIFTNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void recharge(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Checking if damaged is a player
		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player damaged = (Player) event.getEntity();

		GamePlayer damagedGP = null;
		GamePlayer damagerGP = null;

		try {
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
			damagerGP = damagedGP.getLastDamager();
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (damagerGP == null)
			return;
		
		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		if (damagerGP.getClassType().getSkillOne() == Skill.RECHARGE)
			skillLevel = damagerGP.getLevel(damagerGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.RECHARGE)
			skillLevel = damagerGP.getLevel(damagerGP.getClassType().getSkillTwo());
		else
			return;

		// Checking if killed
		boolean dead = false;

		if (event.getDamage() < 0.0001D) {
			double dmg = 1.0 + 0.5 * (skillLevel - 1);

			if (((Damageable) damaged).getHealth() - dmg < 0)
				dead = true;
		}

		if (((Damageable) damaged).getHealth() - event.getFinalDamage() > 0 && !dead)
			return;

		double seconds = 2 + 0.5 * (skillLevel - 1);
		PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, (int) (seconds * 20), 0, true);
		PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (seconds * 20), 0, true);

		Player damager = damagerGP.getBukkitPlayer();
		
		damager.addPotionEffect(regen, true);
		damager.addPotionEffect(resistance, true);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.RECHARGE.getName() + ChatColor.GREEN));
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void flurry(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Checking if damager and damaged are players
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getDamage() < 0.001D)
			return;
		
		// Casting to players
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;
		
		if (damagerGP.getClassType().getSkillOne() == Skill.FLURRY)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.FLURRY)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.1 + 0.05 * (skillLevel - 1);

		if (Math.random() > percentage)
			return;
		
		Collection<PotionEffect> effects = damager.getActivePotionEffects();
		for (PotionEffect e : effects)
			if (e.getType() == PotionEffectType.SPEED)
				if (e.getAmplifier() >= 1)
					return;
				else if (e.getDuration() >= 40)
					return;
		
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 40, 0, true);
		damager.addPotionEffect(speed, true);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.FLURRY.getName() + ChatColor.GREEN));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void explosiveWeakness(EntityDamageEvent event) {
		
		if (event.isCancelled())
			return;
		
		explosiveWeakness(event.getEntity());
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void explosiveWeakness(EntityRegainHealthEvent event) {
		
		if (event.isCancelled())
			return;
		
		explosiveWeakness(event.getEntity());
	}
	
	public void explosiveWeakness(Entity entity) {
		
		// Check if the entity is player
		if (!(entity instanceof Player))
			return;
		
		// Casting to players
		Player player = (Player) entity;
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		// Check if players are in-game
		if (!gp.isInGame())
			return;
		
		int skillLevel = 0;
		
		if (gp.getClassType().getSkillOne() == Skill.EXPLOSIVE_WEAKNESS)
			skillLevel = gp.getLevel(gp.getClassType().getSkillOne());
		else if (gp.getClassType().getSkillTwo() == Skill.EXPLOSIVE_WEAKNESS)
			skillLevel = gp.getLevel(gp.getClassType().getSkillTwo());
		else
			return;
		
		if (gp.isExplosiveWeaknessCooldown())
			return;
		
		int hearts = 7 + skillLevel;
		
		if (((Damageable) player).getHealth() <= hearts)
			return;
		
		Location loc = entity.getLocation().clone();
		
		loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4, false, false);
		
		if (skillLevel == 9)
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2), true);
		
		gp.setExplosiveWeaknessCooldown(true);
		
		final GamePlayer finalGP = gp;
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(MegaArena.getInstance(), () -> {
			finalGP.setExplosiveWeaknessCooldown(false);
		}, 600L);
		
		player.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated.",
				ChatColor.AQUA + Skill.EXPLOSIVE_WEAKNESS.getName() + ChatColor.GREEN));
	}
	
	@EventHandler
	public void onTNTDamagePlayer(EntityDamageByEntityEvent event) {
		
		if (event.getDamager().getType() != EntityType.PRIMED_TNT)
			return;
		
		if (!explosiveWeaknessTNT.containsKey(event.getDamager().getUniqueId()))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		GamePlayer gp = null;
		
		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(((Player) event.getEntity()).getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (explosiveWeaknessTNT.get(event.getDamager().getUniqueId()).getSide() == gp.getSide())
			event.setCancelled(true);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void support(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Checking if damager and damaged are players
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players 
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;
		
		if (damagedGP.getClassType().getSkillOne() == Skill.SUPPORT)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SUPPORT)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.06 + 0.005 * (skillLevel - 1);

		if (Math.random() > percentage)
			return;

		TNTPrimed tnt = (TNTPrimed) damaged.getWorld().spawnEntity(damaged.getEyeLocation(), EntityType.PRIMED_TNT);

		tnt.setFuseTicks(60); // 3 second delay before explosion
		creeperTNT.put(tnt.getUniqueId(), damaged);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill activated.",
				ChatColor.AQUA + Skill.SUPPORT.getName() + ChatColor.GREEN));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void disableTNT(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		if (!(event.getDamager() instanceof TNTPrimed))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		TNTPrimed tnt = (TNTPrimed) event.getDamager();
		event.setCancelled(true);
		
		if (!creeperTNT.containsKey(tnt.getUniqueId()))
			return;

		Player creeper = creeperTNT.get(tnt.getUniqueId());
		Player damaged = (Player) event.getEntity();
		
		if (creeper.getName().equals(damaged.getName()))
			return;

		GamePlayer creeperAP = null;
		GamePlayer damagedGP = null;

		try {
			creeperAP = MegaArena.getInstance().getPlayerManager().getPlayer(creeper.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}

		// Check if players are in-game
		if (!creeperAP.isInGame() || !damagedGP.isInGame())
			return;

		if (creeperAP.getGame().getArena().getType() == Arena.Type.TDM && creeperAP.getSide() == damagedGP.getSide())
			return;

		Damageable dmgPlayer = (Damageable) damaged;

		if (dmgPlayer.getHealth() <= 2.0)
			dmgPlayer.setHealth(0.0D);
		else
			dmgPlayer.setHealth(dmgPlayer.getHealth() - 2.0D);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void weakeningSwing(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Checking if damager and damaged are players
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		if (damagerGP.getClassType().getSkillOne() == Skill.WEAKENING_SWING)
			skillLevel = damagerGP.getLevel(damagerGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.WEAKENING_SWING)
			skillLevel = damagerGP.getLevel(damagerGP.getClassType().getSkillTwo());
		else
			return;
		
		double duration = 2 + 0.5 * (skillLevel - 1);
		
		if (Math.random() > 0.15D)
			return;
		
		PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, (int) (duration * 20), 0, true);

		damaged.addPotionEffect(weakness, false);
		
		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.WEAKENING_SWING.getName() + ChatColor.GREEN));
		damaged.sendMessage(String.format(ChatColor.RED + "%s's hit inflicted you with Weakness for %s seconds.",
				damager.getName(), duration + ""));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void swiftBackup(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Checking if damager and damaged are players
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagedGP = null;
		GamePlayer damagerGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		if (damagedGP.getClassType().getSkillOne() == Skill.SWIFT_BACKUP)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SWIFT_BACKUP)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;
		
		double duration = 4 + (skillLevel - 1);
		
		if (Math.random() > 0.1D)
			return;
		
		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SWIFT_BACKUP.getName() + ChatColor.GREEN));

		Wolf wolf = (Wolf) damaged.getWorld().spawnEntity(damaged.getLocation(), EntityType.WOLF);
		wolf.setOwner((AnimalTamer) damaged);
		wolf.setMaxHealth(1.0D);
		wolf.setHealth(1.0D);
		((LivingEntity) wolf).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
		((LivingEntity) wolf).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1), true);
		
		new BukkitRunnable() {
			@Override
			public void run() {

				wolf.remove();
			}
		}.runTaskLater(MegaArena.getInstance(), (long) duration * 20L);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void soulSucker(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;

		// Checking if damager and damaged are players
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		if (damagerGP.getClassType().getSkillOne() == Skill.SOUL_SUCKER)
			skillLevel = damagerGP.getLevel(damagerGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.SOUL_SUCKER)
			skillLevel = damagerGP.getLevel(damagerGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.12 + (skillLevel - 1) / 100;

		if (Math.random() > percentage)
			return;

		if (((Damageable) damager).getHealth() > ((Damageable) damager).getMaxHealth() - 1.0)
			((Damageable) damager).setHealth(40.0);
		else
			((Damageable) damager).setHealth(((Damageable) damager).getHealth() + 1.0);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SOUL_SUCKER.getName() + ChatColor.GREEN));
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void undead(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		// Checking if damager and damaged are players
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		// Casting to players
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		// Check if players are in-game
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		// Checking that the damaged player is a WITHER MINION
		if (damagedGP.getClassType() != Class.WITHER_MINION)
			return;

		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.UNDEAD)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.UNDEAD)
			skillLevel = damagedGP.getLevel(damagedGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.07 + (skillLevel - 1) / 100;

		if (Math.random() > percentage)
			return;

		if (((Damageable) damaged).getHealth() > ((Damageable) damaged).getMaxHealth() - 1.0)
			((Damageable) damaged).setHealth(40.0);
		else
			((Damageable) damaged).setHealth(((Damageable) damaged).getHealth() + 1.0);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.UNDEAD.getName() + ChatColor.GREEN));
	}
}