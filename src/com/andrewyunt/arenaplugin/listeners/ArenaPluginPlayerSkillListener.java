package com.andrewyunt.arenaplugin.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.util.permissions.BroadcastPermissions;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.Arena.ArenaType;
import com.andrewyunt.arenaplugin.objects.Ability;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.Game.Side;
import com.andrewyunt.arenaplugin.objects.Skill;

public class ArenaPluginPlayerSkillListener implements Listener{
	public ArrayList<Player> gotSpeed = new ArrayList<Player>();
	public HashMap<TNTPrimed, Player> creeperTnt = new HashMap<TNTPrimed, Player>();
	
	@EventHandler
	public void boomerangSkill(EntityDamageByEntityEvent e){ // Skeleton -> Boomerang -> Works
		/* Checking for a bow hit from a player to a player*/
		if (!(e.getDamager() instanceof Arrow))
			return;
		final Arrow arrow = (Arrow) e.getDamager();
		if (!(arrow.getShooter() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) e.getEntity();
		
		ArenaPlayer apShooter=null;
		/* Checking if players are in the Arena */
		try {
			apShooter = ArenaPlugin.getInstance().getPlayerManager().getPlayer(shooter.getName());
		} catch (PlayerException e1) { return; 	/* Shooter isn't in the Arena.*/}
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
	
		/* Checking that the shooter is a Skeleton */
		if (!(apShooter.getClassType() == Class.SKELETON))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int precentage = 0;
		if (apShooter.getClassType().getSkillOne().equals(Skill.BOOMERANG)){
			precentage = 20*apShooter.getClassType().getSkillOne().getLevel(apShooter);
		}else if (apShooter.getClassType().getSkillTwo().equals(Skill.BOOMERANG))
			precentage = 20*apShooter.getClassType().getSkillTwo().getLevel(apShooter);
		if (random > precentage)
			return;
		shooter.getInventory().addItem(new ItemStack(Material.ARROW));
		shooter.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.BOOMERANG.toString()));
	}
	
	@EventHandler
	public void mutualWeakness(EntityDamageByEntityEvent e){ // Skeleton -> Mutual Weakness -> Works
		/* Checking for a bow hit from a player to a player*/
		if (!(e.getDamager() instanceof Arrow))
			return;
		final Arrow arrow = (Arrow) e.getDamager();
		if (!(arrow.getShooter() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) e.getEntity();
		
		ArenaPlayer apShooter=null;
		/* Checking if players are in the Arena */
		try {
			apShooter = ArenaPlugin.getInstance().getPlayerManager().getPlayer(shooter.getName());
		} catch (PlayerException e1) { return; 	/* Shooter isn't in the Arena.*/}
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the shooter is a Skeleton */
		if (!(apShooter.getClassType() == Class.SKELETON))
			return;
		
		/* Apply Effects */
		int skillLevel = 0;
		if (apShooter.getClassType().getSkillOne().equals(Skill.MUTUAL_WEAKNESS)){
			skillLevel = apShooter.getClassType().getSkillOne().getLevel(apShooter);
		}else if (apShooter.getClassType().getSkillTwo().equals(Skill.MUTUAL_WEAKNESS))
			skillLevel = apShooter.getClassType().getSkillTwo().getLevel(apShooter);
		int duration = (int)((2+0.5*(skillLevel-1)))*(20);
		PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, duration, 0, true);
		PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, duration, 0, true);
		shooter.addPotionEffect(slowness, true);
		shooter.addPotionEffect(regen, true);
		damaged.addPotionEffect(slowness, true);
		shooter.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.MUTUAL_WEAKNESS.toString()));
		damaged.sendMessage(String.format(ChatColor.GOLD+"%s's arrow inflicted you with Slowness for %ss", shooter.getName(), duration/20+""));
	}
	
	@EventHandler
	public void resist(EntityDamageByEntityEvent e){ // Zombie - Resist -> Works
		/* Checking if damager and damaged are players*/
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamaged=null; 
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
	
		/* Checking that the damaged player is a Zombie */
		if (!(apDamaged.getClassType() == Class.ZOMBIE))
			return;
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel=0;
		if (apDamaged.getClassType().getSkillOne().equals(Skill.RESIST)){
			skillLevel = apDamaged.getClassType().getSkillOne().getLevel(apDamaged);
		}else if (apDamaged.getClassType().getSkillTwo().equals(Skill.RESIST))
			skillLevel = apDamaged.getClassType().getSkillTwo().getLevel(apDamaged);
		int precentage = 11+3*(skillLevel-1);
	
		if (random > precentage)
			return;
		PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, true);
		damaged.addPotionEffect(resistance, true);
		damaged.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.RESIST.toString()));
	}
	
	@EventHandler
	public void swiftness(EntityDamageByEntityEvent e){ // Zombie - Swiftness -> Works
		/* Checking for a bow hit from a player to a player*/
		if (!(e.getDamager() instanceof Arrow))
			return;
		final Arrow arrow = (Arrow) e.getDamager();
		if (!(arrow.getShooter() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player shooter = (Player) arrow.getShooter();
		Player damaged = (Player) e.getEntity();
		
		ArenaPlayer apDamaged=null;
		/* Checking if players are in the Arena */
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(shooter.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the shooter is a ZOMBIE */
		if (!(apDamaged.getClassType() == Class.ZOMBIE))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel = 0;
		if (apDamaged.getClassType().getSkillOne().equals(Skill.SWIFTNESS)){
			skillLevel = apDamaged.getClassType().getSkillOne().getLevel(apDamaged);
		}else if (apDamaged.getClassType().getSkillTwo().equals(Skill.SWIFTNESS))
			skillLevel = apDamaged.getClassType().getSkillTwo().getLevel(apDamaged);
		int precentage = 10 + 5*(skillLevel-1);
		if (random > precentage)
			return;
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 60, 1, true);
		damaged.addPotionEffect(speed, true);
		damaged.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.SWIFTNESS.toString()));
	}
	
	@EventHandler
	public void recharge(EntityDamageByEntityEvent e){ // Herobrine - Recharge -> Works
		/* Checking if damager and damaged are players*/
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/*Fix Herobrine Ability not giving this skill*/
		/* Checking if players are in the Arena */
		ArenaPlayer apDamaged=null; 
		ArenaPlayer apDamager=null;
		try {
			apDamager = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		/* Checking that the damaged player is a Herobrine */
		if (!(apDamager.getClassType() == Class.HEROBRINE))
			return;
		/* Checking if killed */
		boolean dead = false;
		if (e.getDamage() < 0.0001D){
			double dmg = 1.0 + 0.5*(apDamager.getClassType().getAbility().getLevel(apDamager)-1);
			if (((Damageable)damaged).getHealth() - dmg < 0)
				dead=true;
		}
		if (((Damageable)damaged).getHealth() - e.getFinalDamage() > 0 && !dead)
			return;
		/* Randomization */
		int skillLevel=0;
		if (apDamager.getClassType().getSkillOne().equals(Skill.RECHARGE)){
			skillLevel = apDamager.getClassType().getSkillOne().getLevel(apDamager);
		}else if (apDamager.getClassType().getSkillTwo().equals(Skill.RECHARGE)){
			skillLevel = apDamager.getClassType().getSkillTwo().getLevel(apDamager);
		}
		double seconds = 2 + 0.5*(skillLevel-1);
		PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, (int)(seconds*20), 0, true);
		PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int)(seconds*20), 0, true);
		damager.addPotionEffect(regen, true);
		damager.addPotionEffect(resistance, true);
		damager.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.RECHARGE.toString()));
		}
	
	@EventHandler
	public void flurry(EntityDamageByEntityEvent e){ // Herobrine - Flurry -> Works
		/* Checking if damager and damaged are players */
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		if (e.getDamage() < 0.001D)
			return;
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamager=null;
		try {
			apDamager = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the damaged player is a Herobrine */
		if (!(apDamager.getClassType() == Class.HEROBRINE))
			return;
		
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel=0;
		if (apDamager.getClassType().getSkillOne().equals(Skill.FLURRY)){
			skillLevel = apDamager.getClassType().getSkillOne().getLevel(apDamager);
		}else if (apDamager.getClassType().getSkillTwo().equals(Skill.FLURRY))
			skillLevel = apDamager.getClassType().getSkillTwo().getLevel(apDamager);
		int precentage = 10 + 5*(skillLevel-1);
		
		if (random > precentage)
			return;
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 40, 0, true);
		damager.addPotionEffect(speed, true);
		damager.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.FLURRY.toString()));
		}
	
	@EventHandler
	public void powerfulWeakness(EntityDamageEvent e){ // Creeper - Powerful Weakness -> Works
		/* Check if the entity is player */
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamaged=null; 
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the damaged player is a Creeper */
		if (!(apDamaged.getClassType() == Class.CREEPER))
			return;
		
		if (gotSpeed.contains(damaged))
			return;
		
		int skillLevel=0;
		if (apDamaged.getClassType().getSkillOne().equals(Skill.POWERFUL_WEAKNESS)){
			skillLevel = apDamaged.getClassType().getSkillOne().getLevel(apDamaged);
		}else if (apDamaged.getClassType().getSkillTwo().equals(Skill.POWERFUL_WEAKNESS))
			skillLevel = apDamaged.getClassType().getSkillTwo().getLevel(apDamaged);
		int health = 16+(skillLevel-1);
		if (((Damageable)damaged).getHealth() - e.getFinalDamage() > health)
			return;
		
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true);
		damaged.addPotionEffect(speed, false);
		damaged.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.POWERFUL_WEAKNESS.toString()));
		gotSpeed.add(damaged);
	   }
	
	@EventHandler
	public void powerfulWeakness(EntityRegainHealthEvent e){ // Creeper - Powerful Weakness // Disable Speed
		/* Check if the entity is player */
		if (!(e.getEntity() instanceof Player))
			return;

		/* Casting to players */
		Player player = (Player) e.getEntity();
		/* Checking if players are in the Arena */
		ArenaPlayer apPlayer=null; 
		try {
			apPlayer = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e1) { return; 	/* player Player isn't in the Arena.*/}
		/* Checking that the player player is a Creeper */
		if (!(apPlayer.getClassType() == Class.CREEPER))
			return;
		if (!gotSpeed.contains(player))
			return;
		int skillLevel=0;
		if (apPlayer.getClassType().getSkillOne().equals(Skill.POWERFUL_WEAKNESS)){
			skillLevel = apPlayer.getClassType().getSkillOne().getLevel(apPlayer);
		}else if (apPlayer.getClassType().getSkillTwo().equals(Skill.POWERFUL_WEAKNESS))
			skillLevel = apPlayer.getClassType().getSkillTwo().getLevel(apPlayer);
		int health = 16+(skillLevel-1);
		if (((Damageable)player).getHealth() < health)
			return;
		PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 1, 0);
		player.addPotionEffect(speed, true);
		player.sendMessage(String.format(ChatColor.GOLD+"Your %s skill deactivated.", Skill.POWERFUL_WEAKNESS.toString()));
		gotSpeed.remove(player);
		}
	
	@EventHandler
	public void support(EntityDamageByEntityEvent e){ //Creeper - Support
		/* Checking if damager and damaged are players */
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		/* Checking if players are in the Arena */
		ArenaPlayer apDamaged=null;
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}

		/* Checking that the damaged player is a CREEPER */
		if (!(apDamaged.getClassType() == Class.CREEPER))
			return;
		/* Randomization */
		Random r = new Random();
		double random = (r.nextDouble()*99)+1;
		int skillLevel=0;
		if (apDamaged.getClassType().getSkillOne().equals(Skill.SUPPORT)){
			skillLevel = apDamaged.getClassType().getSkillOne().getLevel(apDamaged);
		}else if (apDamaged.getClassType().getSkillTwo().equals(Skill.SUPPORT))
			skillLevel = apDamaged.getClassType().getSkillTwo().getLevel(apDamaged);
		double precentage = 6 + 0.5*(skillLevel-1);
		if (random > precentage)
			return;
		TNTPrimed tnt = (TNTPrimed) damaged.getWorld().spawnEntity(damaged.getLocation(), EntityType.PRIMED_TNT);
		tnt.setFuseTicks(60); // 3 Seconds to explode
		creeperTnt.put(tnt, damaged);
		damager.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.SUPPORT.toString()));
		}
	
	@EventHandler 
	public void disableTnt(EntityDamageByEntityEvent e){ // Creeper Support // Disable TNT Damage
			if (!(e.getDamager() instanceof TNTPrimed))
				return;
			if (!(e.getEntity() instanceof Player))
				return;
			TNTPrimed tnt = (TNTPrimed) e.getDamager();
			if (!creeperTnt.containsKey(tnt))
				return;
			e.setCancelled(true);
			Player creeper = creeperTnt.remove(tnt);
			try {
				 ArenaPlugin.getInstance().getPlayerManager().getPlayer(creeper.getName());
			} catch (PlayerException e1) {
				return; //Player left the game.
			}
			Player damaged =  (Player) e.getEntity();
			if (damaged == creeper)
			{
				creeper.setVelocity(new Vector(0F,0F,0F));
				return;
			}	
				Damageable dmgPlayer = (Damageable) damaged;
				if (dmgPlayer.getHealth() <= 3.0 )
					dmgPlayer.setHealth(0D);
				else
					dmgPlayer.setHealth(dmgPlayer.getHealth()-3.0);
 		}
			
	@EventHandler
	public void weakeningSwing(EntityDamageByEntityEvent e){ // Spirit Warrior - Weakening Swing -> Works
		/* Checking if damager and damaged are players */
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamager=null;
		try {
			apDamager = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the damaged player is a SPIRIT WARRIOR */
		if (!(apDamager.getClassType() == Class.SPIRIT_WARRIOR))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel=0;
		if (apDamager.getClassType().getSkillOne().equals(Skill.WEAKENING_SWING)){
			skillLevel = apDamager.getClassType().getSkillOne().getLevel(apDamager);
		}else if (apDamager.getClassType().getSkillTwo().equals(Skill.WEAKENING_SWING))
			skillLevel = apDamager.getClassType().getSkillTwo().getLevel(apDamager);
		double duration = 2 + 0.5*(skillLevel-1);
		if (random > 20)
			return;
		PotionEffect weakness = new PotionEffect(PotionEffectType.WEAKNESS, (int) (duration*20), 0, true);
		damaged.addPotionEffect(weakness, false);
		damager.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.WEAKENING_SWING.toString()));
		damaged.sendMessage(String.format(ChatColor.GOLD+"%s's hit inflicted you with Weakness for %ss", damager.getName(), duration+""));
	}
	
	@EventHandler
	public void swiftBackup(EntityDamageByEntityEvent e){ // Spirit Warrior - Swift Backup -> Works
		/* Checking if damager and damaged are players */
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamaged=null;
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the damaged player is a SPIRIT WARRIOR */
		if (!(apDamaged.getClassType() == Class.SPIRIT_WARRIOR))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel=0;
		if (apDamaged.getClassType().getSkillOne().equals(Skill.SWIFT_BACKUP)){
			skillLevel = apDamaged.getClassType().getSkillOne().getLevel(apDamaged);
		}else if (apDamaged.getClassType().getSkillTwo().equals(Skill.SWIFT_BACKUP))
			skillLevel = apDamaged.getClassType().getSkillTwo().getLevel(apDamaged);
		double duration = 4 + (skillLevel-1);
		if (random > 10)
			return;
		damaged.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.SWIFT_BACKUP.toString()));
		Wolf wolf = (Wolf) damaged.getWorld().spawnEntity(damaged.getLocation(), EntityType.WOLF);
		wolf.setOwner((AnimalTamer) damaged);
		((LivingEntity)wolf).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
		new BukkitRunnable(){
			@Override
			public void run() {
				wolf.remove();
			}}.runTaskLater(ArenaPlugin.getInstance(), (long)duration*20L);
	}
	
	@EventHandler
    public void soulSucker(EntityDamageByEntityEvent e){ // Wither Minion - Soul Sucker -> Works
		/* Checking if damager and damaged are players */
    	if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamager=null;
		try {
			apDamager = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the damaged player is a WITHER MINION */
		if (!(apDamager.getClassType() == Class.WITHER_MINION))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel=0;
		if (apDamager.getClassType().getSkillOne().equals(Skill.SOUL_SUCKER)){
			skillLevel = apDamager.getClassType().getSkillOne().getLevel(apDamager);
		}else if (apDamager.getClassType().getSkillTwo().equals(Skill.SOUL_SUCKER))
			skillLevel = apDamager.getClassType().getSkillTwo().getLevel(apDamager);
		int precentage = 12 + (skillLevel-1);
		
		if (random > precentage)
			return;
		if (((Damageable)damager).getHealth() > ((Damageable)damager).getMaxHealth()-2.0)
			((Damageable)damager).setHealth(40.0);
		else
			((Damageable)damager).setHealth(((Damageable)damager).getHealth()+2.0);
		damager.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.SOUL_SUCKER.toString()));
	}

	@EventHandler
    public void undead(EntityDamageByEntityEvent e){ // Wither Minion - Undead
    	/* Checking if damager and damaged are players */
    	if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Player))
			return;
		
		/* Casting to players */
		Player damager = (Player) e.getDamager();
		Player damaged = (Player) e.getEntity();
		
		/* Checking if players are in the Arena */
		ArenaPlayer apDamaged=null;
		try {
			ArenaPlugin.getInstance().getPlayerManager().getPlayer(damager.getName());
		} catch (PlayerException e1) { return; 	/* Damager isn't in the Arena.*/}
		try {
			apDamaged = ArenaPlugin.getInstance().getPlayerManager().getPlayer(damaged.getName());
		} catch (PlayerException e1) { return; 	/* Damaged Player isn't in the Arena.*/}
		
		/* Checking that the damaged player is a WITHER MINION */
		if (!(apDamaged.getClassType() == Class.WITHER_MINION))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int skillLevel=0;
		if (apDamaged.getClassType().getSkillOne().equals(Skill.UNDEAD)){
			skillLevel = apDamaged.getClassType().getSkillOne().getLevel(apDamaged);
		}else if (apDamaged.getClassType().getSkillTwo().equals(Skill.UNDEAD))
			skillLevel = apDamaged.getClassType().getSkillTwo().getLevel(apDamaged);
		int precentage = 7 + (skillLevel-1);
		
		if (random > precentage)
			return;
		if (((Damageable)damaged).getHealth() > ((Damageable)damaged).getMaxHealth()-2.0)
			((Damageable)damaged).setHealth(40.0);
		else
			((Damageable)damaged).setHealth(((Damageable)damaged).getHealth()+2.0);
		damaged.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.UNDEAD.toString()));
    }
}

