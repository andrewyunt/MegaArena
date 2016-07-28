package com.andrewyunt.arenaplugin.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;
import com.andrewyunt.arenaplugin.objects.Skill;

public class ArenaPluginPlayerSkillListener implements Listener{
	
	@EventHandler
	public void boomerangSkill(EntityDamageByEntityEvent e){ // Skeleton -> Boomerang
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
		if (!apShooter.getClassType().equals(Class.SKELETON))
			return;
		
		/* Randomization */
		Random r = new Random();
		int random = r.nextInt(100)+1;
		int precentage = 0;
		if (apShooter.getClassType().getSkillOne().equals(Skill.BOOMERANG)){
			precentage = 20*apShooter.getClassType().getSkillOne().getLevel(apShooter);
		}else if (apShooter.getClassType().getSkillTwo().equals(Skill.BOOMERANG))
			precentage = 20*apShooter.getClassType().getSkillTwo().getLevel(apShooter);
		if (random < precentage){
			shooter.getInventory().addItem(new ItemStack(Material.ARROW));
			shooter.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.BOOMERANG.toString().toLowerCase()));
		}
	}
	
	@EventHandler
	public void mutualWeakness(EntityDamageByEntityEvent e){ // Skeleton -> Mutual Weakness
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
		if (!apShooter.getClassType().equals(Class.SKELETON))
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
		shooter.sendMessage(String.format(ChatColor.GOLD+"Your %s skill activated.", Skill.MUTUAL_WEAKNESS.toString().toLowerCase()));
		damaged.sendMessage(String.format(ChatColor.GOLD+"%s's arrow inflicted you with Slowness for %ss", shooter.getName(), duration+""));
	}
}
