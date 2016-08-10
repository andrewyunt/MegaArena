/**
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

import java.util.HashMap;
import java.util.Random;

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
import org.bukkit.util.Vector;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Skill;

/**
 * 
 * @author Andrew Yunt
 * @author MaccariTA
 *
 */
public class MegaArenaPlayerSkillListener implements Listener {

	public HashMap<TNTPrimed, Player> creeperTnt = new HashMap<TNTPrimed, Player>();

	@EventHandler
	public void boomerangSkill(EntityDamageByEntityEvent event) { // Skeleton -> Boomerang -> Works

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

		/* Check if shooter isn't a Skeleton */
		if (shooterGP.getClassType() != Class.SKELETON)
			return;

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int precentage = 0;

		if (shooterGP.getClassType().getSkillOne() == Skill.BOOMERANG)
			precentage = 20 * shooterGP.getClassType().getSkillOne().getLevel(shooterGP);
		else if (shooterGP.getClassType().getSkillTwo() == Skill.BOOMERANG)
			precentage = 20 * shooterGP.getClassType().getSkillTwo().getLevel(shooterGP);

		if (random > precentage)
			return;

		shooter.getInventory().addItem(new ItemStack(Material.ARROW));

		shooter.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.BOOMERANG.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void mutualWeakness(EntityDamageByEntityEvent event) { // Skeleton -> Mutual Weakness -> Works

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

		/* Check if shooter isn't a Skeleton */
		if (shooterGP.getClassType() != Class.SKELETON)
			return;

		/* Apply Effects */
		int skillLevel = 0;

		if (shooterGP.getClassType().getSkillOne().equals(Skill.MUTUAL_WEAKNESS))
			skillLevel = shooterGP.getClassType().getSkillOne().getLevel(shooterGP);
		else if (shooterGP.getClassType().getSkillTwo().equals(Skill.MUTUAL_WEAKNESS))
			skillLevel = shooterGP.getClassType().getSkillTwo().getLevel(shooterGP);

		int duration = (int) ((2 + 0.5 * (skillLevel - 1))) * 20;
		PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, duration, 0, true);
		PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, duration, 0, true);

		shooter.addPotionEffect(slowness, true);
		shooter.addPotionEffect(regen, true);
		damaged.addPotionEffect(slowness, true);

		shooter.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.MUTUAL_WEAKNESS.getName() + ChatColor.GREEN));
		damaged.sendMessage(String.format(ChatColor.RED + "%s's arrow inflicted you with Slowness for %s seconds.",
				shooter.getName(), String.valueOf(duration / 20)));
	}

	@EventHandler
	public void resist(EntityDamageByEntityEvent event) { // Zombie - Resist ->
															// Works

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

		/* Checking that the damaged player is a Zombie */
		if (damagedGP.getClassType() != Class.ZOMBIE)
			return;

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.RESIST)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.RESIST)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		int precentage = 11 + 3 * (skillLevel - 1);

		if (random > precentage)
			return;

		PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, true);

		damaged.addPotionEffect(resistance, true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.RESIST.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void swiftness(EntityDamageByEntityEvent event) { // Zombie - Swiftness -> Works

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

		/* Checking that the shooter is a ZOMBIE */
		if (damagedGP.getClassType() != Class.ZOMBIE)
			return;

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.SWIFTNESS)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SWIFTNESS)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		int precentage = 10 + 5 * (skillLevel - 1);

		if (random > precentage)
			return;

		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 60, 1, true);

		damaged.addPotionEffect(speed, true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SWIFTNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void recharge(EntityDamageByEntityEvent event) { // Herobrine - Recharge -> Works

		/* Checking if damager and damaged are players */
		if (!(event.getDamager() instanceof Player))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player damager = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		// TODO: Fix Herobrine Ability not giving this skill

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

		/* Checking that the damaged player is a Herobrine */
		if (damagedGP.getClassType() != Class.HEROBRINE)
			return;

		/* Checking if killed */
		boolean dead = false;

		if (event.getDamage() < 0.0001D) {
			double dmg = 1.0 + 0.5 * (damagedGP.getClassType().getAbility().getLevel(damagedGP) - 1);

			if (((Damageable) damaged).getHealth() - dmg < 0)
				dead = true;
		}

		if (((Damageable) damaged).getHealth() - event.getFinalDamage() > 0 && !dead)
			return;

		/* Randomization */
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.RECHARGE)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.RECHARGE)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		double seconds = 2 + 0.5 * (skillLevel - 1);
		PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, (int) (seconds * 20), 0, true);
		PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (seconds * 20), 0, true);

		damager.addPotionEffect(regen, true);
		damager.addPotionEffect(resistance, true);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.RECHARGE.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void flurry(EntityDamageByEntityEvent event) { // Herobrine - Flurry -> Works

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

		/* Checking that the damaged player is a Herobrine */
		if (damagedGP.getClassType() != Class.HEROBRINE)
			return;

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.FLURRY)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.FLURRY)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		int percentage = 10 + 5 * (skillLevel - 1);

		if (random > percentage)
			return;

		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 40, 0, true);

		damager.addPotionEffect(speed, true);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.FLURRY.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void powerfulWeakness(EntityDamageEvent event) { // Creeper - Powerful Weakness -> Works

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

		if (damagedGP.hasSpeed())
			return;

		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.POWERFUL_WEAKNESS)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.POWERFUL_WEAKNESS)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		int health = 16 + (skillLevel - 1);

		if (((Damageable) damaged).getHealth() - event.getFinalDamage() > health)
			return;

		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true);

		damaged.addPotionEffect(speed, false);
		damagedGP.setHasSpeed(true);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.POWERFUL_WEAKNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void powerfulWeakness(EntityRegainHealthEvent event) { // Creeper - Powerful Weakness TODO: Disable Speed

		/* Check if the entity is player */
		if (!(event.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player player = (Player) event.getEntity();

		GamePlayer playerAP = null;

		try {
			playerAP = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		/* Check if players are in-game */
		if (!playerAP.isInGame())
			return;

		/* Checking that the player player is a Creeper */
		if (playerAP.getClassType() != Class.CREEPER)
			return;

		if (!playerAP.hasSpeed())
			return;

		int skillLevel = 0;

		if (playerAP.getClassType().getSkillOne() == Skill.POWERFUL_WEAKNESS)
			skillLevel = playerAP.getClassType().getSkillOne().getLevel(playerAP);
		else if (playerAP.getClassType().getSkillTwo() == Skill.POWERFUL_WEAKNESS)
			skillLevel = playerAP.getClassType().getSkillTwo().getLevel(playerAP);

		int health = 16 + (skillLevel - 1);

		if (((Damageable) player).getHealth() < health)
			return;

		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 1, 0);

		player.addPotionEffect(speed, true);
		playerAP.setHasSpeed(false);

		player.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been deactivated.",
				ChatColor.AQUA + Skill.POWERFUL_WEAKNESS.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void support(EntityDamageByEntityEvent event) { // Creeper - Support

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

		/* Checking that the damaged player is a CREEPER */
		if (damagedGP.getClassType() != Class.CREEPER)
			return;

		/* Randomization */
		Random r = new Random();
		double random = (r.nextDouble() * 99) + 1;
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.SUPPORT)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SUPPORT)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		double precentage = 6 + 0.5 * (skillLevel - 1);

		if (random > precentage)
			return;

		TNTPrimed tnt = (TNTPrimed) damaged.getWorld().spawnEntity(damaged.getLocation(), EntityType.PRIMED_TNT);

		tnt.setFuseTicks(60); // 3 second delay before explosion
		creeperTnt.put(tnt, damaged);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill activated.",
				ChatColor.AQUA + Skill.SUPPORT.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void disableTnt(EntityDamageByEntityEvent event) { // Creeper Support TODO: Disable TNT damage

		if (!(event.getDamager() instanceof TNTPrimed))
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		TNTPrimed tnt = (TNTPrimed) event.getDamager();

		if (!creeperTnt.containsKey(tnt))
			return;

		event.setCancelled(true);

		Player creeper = creeperTnt.remove(tnt);
		Player damaged = (Player) event.getEntity();

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

		if (damaged == creeper) {
			creeper.setVelocity(new Vector(0F, 0F, 0F));
			return;
		}

		Damageable dmgPlayer = (Damageable) damaged;

		if (dmgPlayer.getHealth() <= 3.0)
			dmgPlayer.setHealth(0.0D);
		else
			dmgPlayer.setHealth(dmgPlayer.getHealth() - 3.0);
	}

	@EventHandler
	public void weakeningSwing(EntityDamageByEntityEvent event) { // Spirit Warrior - Weakening Swing -> Works

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

		/* Checking that the damaged player is a SPIRIT WARRIOR */
		if (damagerGP.getClassType() != Class.SPIRIT_WARRIOR)
			return;

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int skillLevel = 0;

		if (damagerGP.getClassType().getSkillOne() == Skill.WEAKENING_SWING)
			skillLevel = damagerGP.getClassType().getSkillOne().getLevel(damagerGP);
		else if (damagerGP.getClassType().getSkillTwo() == Skill.WEAKENING_SWING)
			skillLevel = damagerGP.getClassType().getSkillTwo().getLevel(damagerGP);

		double duration = 2 + 0.5 * (skillLevel - 1);

		if (random > 20)
			return;

		PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, (int) (duration * 20), 0, true);

		damaged.addPotionEffect(weakness, false);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.WEAKENING_SWING.getName() + ChatColor.GREEN));
		damaged.sendMessage(String.format(ChatColor.RED + "%s's hit inflicted you with Weakness for %s seconds.",
				damager.getName(), duration + ""));
	}

	@EventHandler
	public void swiftBackup(EntityDamageByEntityEvent event) { // Spirit Warrior - Swift Backup -> Works

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
		
		/* Checking that the damaged player is a SPIRIT WARRIOR */
		if (damagedGP.getClassType() != Class.SPIRIT_WARRIOR)
			return;
		
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.SWIFT_BACKUP)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.SWIFT_BACKUP)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);
		
		double duration = 4 + (skillLevel - 1);
		double rand = Math.random();
		
		if (rand > 0.1D)
			return;
		
		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SWIFT_BACKUP.getName() + ChatColor.GREEN));

		Wolf wolf = (Wolf) damaged.getWorld().spawnEntity(damaged.getLocation(), EntityType.WOLF);
		wolf.setOwner((AnimalTamer) damaged);
		((LivingEntity) wolf).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);

		new BukkitRunnable() {
			@Override
			public void run() {

				wolf.remove();
			}
		}.runTaskLater(MegaArena.getInstance(), (long) duration * 20L);
	}

	@EventHandler
	public void soulSucker(EntityDamageByEntityEvent event) { // Wither Minion - Soul Sucker -> Works

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
		if (damagerGP.getClassType() != Class.WITHER_MINION)
			return;

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int skillLevel = 0;

		if (damagerGP.getClassType().getSkillOne() == Skill.SOUL_SUCKER)
			skillLevel = damagerGP.getClassType().getSkillOne().getLevel(damagerGP);
		else if (damagerGP.getClassType().getSkillTwo() == Skill.SOUL_SUCKER)
			skillLevel = damagerGP.getClassType().getSkillTwo().getLevel(damagerGP);

		int precentage = 12 + (skillLevel - 1);

		if (random > precentage)
			return;

		if (((Damageable) damager).getHealth() > ((Damageable) damager).getMaxHealth() - 2.0)
			((Damageable) damager).setHealth(40.0);
		else
			((Damageable) damager).setHealth(((Damageable) damager).getHealth() + 2.0);

		damager.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.SOUL_SUCKER.getName() + ChatColor.GREEN));
	}

	@EventHandler
	public void undead(EntityDamageByEntityEvent event) { // Wither Minion - Undead

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

		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100) + 1;
		int skillLevel = 0;

		if (damagedGP.getClassType().getSkillOne() == Skill.UNDEAD)
			skillLevel = damagedGP.getClassType().getSkillOne().getLevel(damagedGP);
		else if (damagedGP.getClassType().getSkillTwo() == Skill.UNDEAD)
			skillLevel = damagedGP.getClassType().getSkillTwo().getLevel(damagedGP);

		int precentage = 7 + (skillLevel - 1);

		if (random > precentage)
			return;

		if (((Damageable) damaged).getHealth() > ((Damageable) damaged).getMaxHealth() - 2.0)
			((Damageable) damaged).setHealth(40.0);
		else
			((Damageable) damaged).setHealth(((Damageable) damaged).getHealth() + 2.0);

		damaged.sendMessage(String.format(ChatColor.GREEN + "Your %s skill has been activated!",
				ChatColor.AQUA + Skill.UNDEAD.getName() + ChatColor.GREEN));
	}
}