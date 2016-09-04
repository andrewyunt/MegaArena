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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.db.DataSource;
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
public class MegaArenaPlayerSkillListener implements Listener {

	public HashMap<TNTPrimed, Player> creeperTNT = new HashMap<TNTPrimed, Player>();

	@EventHandler
	public void boomerangSkill(EntityDamageByEntityEvent event) {

		/* Checking for a bow hit from a player to a player */
		if (!(event.getDamager() instanceof Arrow))
			return;

		final Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
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
		}

		/* Check if players are in-game */
		if (!shooterGP.isInGame() || !damagedGP.isInGame())
			return;

		if (shooterGP.getGame().getArena().getType() == Arena.Type.TDM && shooterGP.getSide() == damagedGP.getSide())
			return;

		double percentage = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();

		if (shooterGP.getClassType().getSkillOne() == Skill.BOOMERANG)
			percentage = 0.2 * ds.getLevel(shooterGP, shooterGP.getClassType().getSkillOne());
		else if (shooterGP.getClassType().getSkillTwo() == Skill.BOOMERANG)
			percentage = 0.2 * ds.getLevel(shooterGP, shooterGP.getClassType().getSkillTwo());
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
		
		/* Checking for a bow hit from a player to a player */
		if (!(event.getDamager() instanceof Arrow))
			return;
		
		Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
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
		}

		/* Check if players are in-game */
		if (!shooterGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (shooterGP.getGame().getArena().getType() == Arena.Type.TDM && shooterGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();

		if (shooterGP.getClassType().getSkillOne() == Skill.WEAKENING_ARROW)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (shooterGP.getClassType().getSkillTwo() == Skill.WEAKENING_ARROW)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
		else
			return;
		
		/* Apply Effects */
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

	@EventHandler
	public void resist(EntityDamageByEntityEvent event) {

		/* Check if damager and damaged entities are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();

		if (damagedGP.getClassType().getSkillOne() == Skill.RESIST)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.RESIST)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
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

		/* Checking for a bow hit from a player to a player */
		if (!(event.getDamager() instanceof Arrow))
			return;

		final Arrow arrow = (Arrow) event.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) event.getEntity();

		GamePlayer shooterGP = null;
		GamePlayer damagedGP = null;

		try {
			shooterGP = MegaArena.getInstance().getPlayerManager().getPlayer(shooter.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!shooterGP.isInGame() || !damagedGP.isInGame())
			return;

		if (shooterGP.getGame().getArena().getType() == Arena.Type.TDM && shooterGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagedGP.getClassType().getSkillOne() == Skill.SWIFTNESS)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SWIFTNESS)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.10 + 0.05 * (skillLevel - 1);

		if (Math.random() > percentage)
			return;
		
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 60, 1, true);
		
		Collection<PotionEffect> effects = damaged.getActivePotionEffects();
		for (PotionEffect e : effects){
			if (e.getType() == PotionEffectType.SPEED){
				if (e.getAmplifier() >= 2)
					return;
				else{
					if (e.getDuration() >= 60)
						return;
				}
					
			}
		}
		
		damaged.addPotionEffect(speed, true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SWIFTNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void recharge(EntityDamageByEntityEvent event) {

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagerGP.getClassType().getSkillOne() == Skill.RECHARGE)
			skillLevel = ds.getLevel(damagerGP, damagerGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.RECHARGE)
			skillLevel = ds.getLevel(damagerGP, damagerGP.getClassType().getSkillTwo());
		else
			return;

		/* Checking if killed */
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

		damager.addPotionEffect(regen, true);
		damager.addPotionEffect(resistance, true);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.RECHARGE.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void flurry(EntityDamageByEntityEvent event) {
		
		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getDamage() < 0.001D)
			return;
		
		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagerGP.getClassType().getSkillOne() == Skill.FLURRY)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.FLURRY)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.1 + 0.05 * (skillLevel - 1);

		if (Math.random() > percentage)
			return;
		
		Collection<PotionEffect> effects = damager.getActivePotionEffects();
		for (PotionEffect e : effects){
			if (e.getType() == PotionEffectType.SPEED){
				if (e.getAmplifier() >= 1)
					return;
				else{
					if (e.getDuration() >= 40)
						return;
				}
					
			}
		}
		
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 40, 0, true);
		damager.addPotionEffect(speed, true);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.FLURRY.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void powerfulWeakness(EntityDamageEvent event) {

		/* Check if the entity is player */
		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damaged = (Player) event.getEntity();

		GamePlayer damagedGP = null;

		try {
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagedGP.isInGame())
			return;

		/* Checking that the damaged player is a Creeper */
		if (damagedGP.getClassType() != Class.CREEPER)
			return;
		
		Arena.Type arenaType = damagedGP.getGame().getArena().getType();
		
		if (!damagedGP.hasFallen() && (arenaType == Arena.Type.FFA || arenaType == Arena.Type.TDM))
			return;

		if (damagedGP.hasSpeed())
			return;

		int skillLevel = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();

		if (damagedGP.getClassType().getSkillOne() == Skill.POWERFUL_WEAKNESS)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.POWERFUL_WEAKNESS)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());

		int health = 16 + (skillLevel - 1);

		if (((Damageable) damaged).getHealth() - event.getFinalDamage() > health)
			return;

		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true);
		Collection<PotionEffect> effects = damaged.getActivePotionEffects();
		for (PotionEffect e : effects){
			if (e.getType() == PotionEffectType.SPEED && e.getAmplifier() >= 1)
					return;
		}

		damaged.addPotionEffect(speed, false);
		damagedGP.setHasSpeed(true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.POWERFUL_WEAKNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void powerfulWeakness(EntityRegainHealthEvent event) {

		/* Check if the entity is player */
		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player player = (Player) event.getEntity();

		GamePlayer playerGP = null;

		try {
			playerGP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!playerGP.isInGame())
			return;

		if (!playerGP.hasSpeed())
			return;

		int skillLevel = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();

		if (playerGP.getClassType().getSkillOne() == Skill.POWERFUL_WEAKNESS)
			skillLevel = ds.getLevel(playerGP, playerGP.getClassType().getSkillOne());
		else if (playerGP.getClassType().getSkillTwo() == Skill.POWERFUL_WEAKNESS)
			skillLevel = ds.getLevel(playerGP, playerGP.getClassType().getSkillTwo());
		else
			return;
		
		Arena.Type arenaType = playerGP.getGame().getArena().getType();
		
		if (!playerGP.hasFallen() && (arenaType == Arena.Type.FFA || arenaType == Arena.Type.TDM))
			return;

		int health = 16 + (skillLevel - 1);

		if (((Damageable) player).getHealth() < health)
			return;

		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 1, 0);

		player.addPotionEffect(speed, true);
		playerGP.setHasSpeed(false);

		player.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been deactivated.",
				ChatColor.AQUA + Skill.POWERFUL_WEAKNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void support(EntityDamageByEntityEvent event) {

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();

		if (damagedGP.getClassType().getSkillOne() == Skill.SUPPORT)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SUPPORT)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
		else
			return;

		double percentage = 0.06 + 0.005 * (skillLevel - 1);

		if (Math.random() > percentage)
			return;

		TNTPrimed tnt = (TNTPrimed) damaged.getWorld().spawnEntity(damaged.getEyeLocation(), EntityType.PRIMED_TNT);

		tnt.setFuseTicks(60); // 3 second delay before explosion
		creeperTNT.put(tnt, damaged);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill activated.",
				ChatColor.AQUA + Skill.SUPPORT.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void disableTNT(EntityDamageByEntityEvent event) {
		
		if (!(event.getDamager() instanceof TNTPrimed))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		TNTPrimed tnt = (TNTPrimed) event.getDamager();

		event.setCancelled(true);
		
		if (!creeperTNT.containsKey(tnt))
			return;

		Player creeper = creeperTNT.get(tnt);
		Player damaged = (Player) event.getEntity();
		
		if (creeper.getName().equals(damaged.getName()))
			return;

		GamePlayer creeperAP = null;
		GamePlayer damagedGP = null;

		try {
			creeperAP = MegaArena.getInstance().getPlayerManager().getPlayer(creeper.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
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

	@EventHandler
	public void weakeningSwing(EntityDamageByEntityEvent event) {

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		int skillLevel = 0;

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagerGP.getClassType().getSkillOne() == Skill.WEAKENING_SWING)
			skillLevel = ds.getLevel(damagerGP, damagerGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.WEAKENING_SWING)
			skillLevel = ds.getLevel(damagerGP, damagerGP.getClassType().getSkillTwo());
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

	@EventHandler
	public void swiftBackup(EntityDamageByEntityEvent event) {

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;
		
		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagedGP = null;
		GamePlayer damagerGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;
		
		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;
		
		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagedGP.getClassType().getSkillOne() == Skill.SWIFT_BACKUP)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SWIFT_BACKUP)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
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

	@EventHandler
	public void soulSucker(EntityDamageByEntityEvent event) {

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;
		
		int skillLevel = 0;

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagerGP.getClassType().getSkillOne() == Skill.SOUL_SUCKER)
			skillLevel = ds.getLevel(damagerGP, damagerGP.getClassType().getSkillOne());
		else if (damagerGP.getClassType().getSkillTwo() == Skill.SOUL_SUCKER)
			skillLevel = ds.getLevel(damagerGP, damagerGP.getClassType().getSkillTwo());
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

	@EventHandler
	public void undead(EntityDamageByEntityEvent event) {

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		GamePlayer damagerGP = null;
		GamePlayer damagedGP = null;

		try {
			damagerGP = MegaArena.getInstance().getPlayerManager().getPlayer(damager.getName());
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!damagerGP.isInGame() || !damagedGP.isInGame())
			return;

		if (damagerGP.getGame().getArena().getType() == Arena.Type.TDM && damagerGP.getSide() == damagedGP.getSide())
			return;

		/* Checking that the damaged player is a WITHER MINION */
		if (damagedGP.getClassType() != Class.WITHER_MINION)
			return;

		int skillLevel = 0;

		DataSource ds = MegaArena.getInstance().getDataSource();
		
		if (damagedGP.getClassType().getSkillOne() == Skill.UNDEAD)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillOne());
		else if (damagedGP.getClassType().getSkillTwo() == Skill.UNDEAD)
			skillLevel = ds.getLevel(damagedGP, damagedGP.getClassType().getSkillTwo());
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