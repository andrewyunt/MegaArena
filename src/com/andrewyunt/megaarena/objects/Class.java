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
package com.andrewyunt.megaarena.objects;

import static com.andrewyunt.megaarena.objects.Ability.*;
import static com.andrewyunt.megaarena.objects.Skill.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The enumeration used for player's selected class types.
 * 
 * @author Andrew Yunt
 * @author MaccariTA
 */
public enum Class implements Upgradable {
	
	ZOMBIE("Zombie", HEAL, RESIST, SWIFTNESS, 4, false),
	SKELETON("Skeleton", EXPLOSIVE_ARROW, MUTUAL_WEAKNESS, BOOMERANG, 15, false),
	HEROBRINE("Herobrine", LIGHTNING, RECHARGE, FLURRY, 10, false),
	CREEPER("Creeper", EXPLODE, POWERFUL_WEAKNESS, SUPPORT, 10, false),
	SPIRIT_WARRIOR("Spirit Warrior", TORNADO, WEAKENING_SWING, SWIFT_BACKUP, 5, true),
	WITHER_MINION("Wither Minion", WITHER_HEADS, SOUL_SUCKER, UNDEAD, 5, true);
	
	private String name;
	private Ability ability;
	private Skill skillOne;
	private Skill skillTwo;
	private int energyPerClick;
	private boolean hero;
	
	Class(String name, Ability ability, Skill skillOne, Skill skillTwo, int energyPerClick, boolean hero) {
		
		this.name = name;
		this.ability = ability;
		this.skillOne = skillOne;
		this.skillTwo = skillTwo;
		this.energyPerClick = energyPerClick;
		this.hero = hero;
	}
	
	@Override
	public String getName() {
		
		return name;
	}
	
	public Ability getAbility() {
		
		return ability;
	}
	
	public Skill getSkillOne() {
		
		return skillOne;
	}
	
	public Skill getSkillTwo() {
		
		return skillTwo;
	}
	
	public int getEnergyPerClick() {
		
		return energyPerClick;
	}
	
	public boolean isHero() {
		
		return hero;
	}

	public ItemStack[] giveKitItems(GamePlayer ap) {

		PlayerInventory inv = ap.getBukkitPlayer().getInventory();
		int kitLevel = ap.getLevel(this);

		/* Health potion */
		ItemStack potH = new ItemStack(Material.POTION, 1);
		PotionMeta pmH = (PotionMeta) potH.getItemMeta();
		PotionEffect effectH = new PotionEffect(PotionEffectType.HEAL, 1, 2, false);
		List<String> lstH = new ArrayList<String>();
		lstH.add(ChatColor.RESET + "HEAL 8" + ChatColor.RED + "\u2764");
		pmH.setDisplayName(ChatColor.RESET + "" + ChatColor.DARK_RED + "Health Potion");
		pmH.setLore(lstH);
		pmH.setMainEffect(PotionEffectType.HEAL);
		pmH.addCustomEffect(effectH, true);
		potH.setItemMeta(pmH);
		ItemStack potH2 = new ItemStack(Material.POTION, 2);
		potH2.setItemMeta(pmH);

		/* Speed potion */
		ItemStack potS = new ItemStack(Material.POTION, 1);
		PotionMeta pmS = (PotionMeta) potS.getItemMeta();
		PotionEffect effectS = new PotionEffect(PotionEffectType.SPEED, (15 * 20), 1, false);
		List<String> lstS = new ArrayList<String>();
		lstS.add(ChatColor.RESET + "Duration: " + ChatColor.GRAY + "15s");
		pmS.setLore(lstS);
		pmS.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + "Speed Potion");
		pmS.setMainEffect(PotionEffectType.SPEED);
		pmS.addCustomEffect(effectS, true);
		potS.setItemMeta(pmS);
		ItemStack potS2 = new ItemStack(Material.POTION, 2);
		potS2.setItemMeta(pmS);

		ItemStack chest;
		ItemStack bow;
		ItemStack helmet;
		ItemStack leggings;
		ItemStack boots;

		inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
		inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
		inv.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
		inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
		inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
		inv.setBoots(new ItemStack(Material.IRON_BOOTS, 1));

		if (this == ZOMBIE) {

			inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
			inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
			switch (kitLevel) {
			case 1:
				break;
			case 2:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				break;
			case 3:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 4:
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 5:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 6:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
				chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setChestplate(chest);
				break;
			case 7:
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
				chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setChestplate(chest);
				break;
			case 8:
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
				chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setChestplate(chest);
				break;
			case 9:
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				chest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
				chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setChestplate(chest);
				break;
			default:
				break;
			}

		} else if (this == SKELETON) {

			inv.setItem(0, new ItemStack(Material.STONE_SWORD, 1));
			inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
			switch (kitLevel) {
			case 1:
				inv.setItem(2, new ItemStack(Material.BOW, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
				inv.setItem(7, new ItemStack(Material.ARROW, 32));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
				break;
			case 2:
				inv.setItem(2, new ItemStack(Material.BOW, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(7, new ItemStack(Material.ARROW, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				break;
			case 3:
				inv.setItem(2, new ItemStack(Material.BOW, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 4:
				inv.setItem(2, new ItemStack(Material.BOW, 1));
				inv.setItem(4, potH);
				inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 5:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(2, new ItemStack(Material.BOW, 1));
				inv.setItem(3, potS);
				inv.setItem(4, potH);
				inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				inv.setItem(2, bow);
				inv.setItem(3, potS2);
				inv.setItem(4, potH);
				inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				inv.setItem(2, bow);
				inv.setItem(3, potS2);
				inv.setItem(4, potH);
				inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
				inv.setHelmet(helmet);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				inv.setItem(2, bow);
				inv.setItem(3, potS2);
				inv.setItem(4, potH2);
				inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
				inv.setHelmet(helmet);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
				inv.setItem(2, bow);
				inv.setItem(3, potS2);
				inv.setItem(4, potH2);
				inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(7, new ItemStack(Material.ARROW, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
				inv.setHelmet(helmet);
				break;
			default:
				break;
			}

		} else if (this == HEROBRINE) {

			inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
			inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
			switch (kitLevel) {
			case 1:
				break;
			case 2:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				break;
			case 3:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 4:
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 5:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 6:
				inv.setItem(2, potS);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setHelmet(helmet);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setHelmet(helmet);
				break;
			default:
				break;
			}

		} else if (this == CREEPER) {

			inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
			inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
			switch (kitLevel) {
			case 1:
				break;
			case 2:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				break;
			case 3:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 4:
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 5:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 6:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1);
				inv.setLeggings(leggings);
				break;
			case 7:
				inv.setItem(2, potS);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setLeggings(leggings);
				break;
			case 8:
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setLeggings(leggings);
				break;
			case 9:
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				inv.setLeggings(leggings);
				break;
			default:
				break;
			}

		} else if (this == SPIRIT_WARRIOR) {

			inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
			inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
			switch (kitLevel) {
			case 1:
				break;
			case 2:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				break;
			case 3:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 4:
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 5:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 6:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setBoots(boots);
				break;
			case 7:
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setBoots(boots);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
				inv.setBoots(boots);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				boots = new ItemStack(Material.DIAMOND_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 2);
				inv.setBoots(boots);
				break;
			default:
				break;
			}

		} else if (this == WITHER_MINION) {

			inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
			inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
			switch (kitLevel) {
			case 1:
				break;
			case 2:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				break;
			case 3:
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 4:
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 5:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				break;
			case 6:
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setHelmet(helmet);
				break;
			case 7:
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setHelmet(helmet);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setHelmet(helmet);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setHelmet(helmet);
				break;
			default:
				break;
			}
		}

		return inv.getContents();
	}
}