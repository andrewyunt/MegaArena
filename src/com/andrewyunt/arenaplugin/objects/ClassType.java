package com.andrewyunt.arenaplugin.objects;

import static com.andrewyunt.arenaplugin.objects.Ability.EXPLODE;
import static com.andrewyunt.arenaplugin.objects.Ability.HEAL;
import static com.andrewyunt.arenaplugin.objects.Ability.HURRICANE;
import static com.andrewyunt.arenaplugin.objects.Ability.LIGHTNING;
import static com.andrewyunt.arenaplugin.objects.Ability.MASTERS_ATTACK;
import static com.andrewyunt.arenaplugin.objects.Ability.SPLIT_ARROW;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.andrewyunt.arenaplugin.ArenaPlugin;

/**
 * 
 * @author Andrew Yunt
 *
 */
public enum ClassType {
	
	ZOMBIE("Zombie", HEAL, 0, 4) {
	},
	
	SKELETON("Skeleton", SPLIT_ARROW, 9, 15) {
	},
	
	HEROBRINE("Herobrine", LIGHTNING, 18, 10) {
	},
	
	CREEPER("Creeper", EXPLODE, 27, 10) {
	},
	
	SPIRIT_WARRIOR("Spirit Warrior", HURRICANE, 36, 5) {
	},
	
	WITHER_MINION("Wither Minion", MASTERS_ATTACK, 45, 5) {	
	};
	
	private String name;
	private int upgradeRowStart;
	private int energyPerClick;
	private Ability ability;
	
	ClassType(String name, Ability ability, int upgradeRowStart, int energyPerClick) {
		
		this.name = name;
		this.ability = ability;
		this.upgradeRowStart = upgradeRowStart;
		this.energyPerClick = energyPerClick;
	}
	
	public String getName() {
		
		return name;
	}
	
	public Ability getAbility() {
		
		return ability;
	}
	
	public int getUpgradeRowStart() {
		
		return upgradeRowStart;
	}
	
	public int getEnergyPerClick() {
		
		return energyPerClick;
	}
	
	public ItemStack[] getItems(ArenaPlayer ap) {
		
		Inventory inv = ArenaPlugin.getInstance().getServer().createInventory(ap.getBukkitPlayer(), 54);
		int classLevel = ap.getClassLevel(this);
		ItemStack potH = new ItemStack(Material.POTION, 1);
		PotionMeta pmH = (PotionMeta)potH.getItemMeta();
		PotionEffect effectH = new PotionEffect(PotionEffectType.HEAL, 1, 2, false);
		pmH.addCustomEffect(effectH, true);
		potH.setItemMeta(pmH);
		ItemStack potH2 = new ItemStack(Material.POTION, 2);
		potH2.setItemMeta(pmH);
		
		ItemStack potS = new ItemStack(Material.POTION, 1);
		PotionMeta pmS = (PotionMeta)potS.getItemMeta();
		PotionEffect effectS = new PotionEffect(PotionEffectType.SPEED, (15*20), 1, false);
		pmS.addCustomEffect(effectS, true);
		potS.setItemMeta(pmS);
		ItemStack potS2 = new ItemStack(Material.POTION, 2);
		potS.setItemMeta(pmS);
		
		ItemStack chest;
		ItemStack bow;
		ItemStack helmet;
		ItemStack leggings;
		ItemStack boots;
		if (this == ZOMBIE) {
			switch (classLevel){
				case 1: 
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 2:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 3:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 4:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(3, potH);
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 5:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, potS);
					inv.setItem(3, potH);
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 6:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, potS);
					inv.setItem(3, potH);
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
					chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
					inv.setItem(102, chest);
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 7:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, potS2);
					inv.setItem(3, potH);
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
					chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
					inv.setItem(102, chest);
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 8:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, potS2);
					inv.setItem(3, potH2);
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
					chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
					inv.setItem(102, chest);
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 9:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, potS2);
					inv.setItem(3, potH2);
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					chest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
					chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
					inv.setItem(102, chest);
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				default:
					break;
			}
		} else if (this == SKELETON) {
			switch (classLevel){
				case 1:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, new ItemStack(Material.BOW, 1));
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
					inv.setItem(8, new ItemStack(Material.ARROW, 32));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 2:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, new ItemStack(Material.BOW, 1));
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
					inv.setItem(8, new ItemStack(Material.ARROW, 48));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 3:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, new ItemStack(Material.BOW, 1));
					inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 4:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, new ItemStack(Material.BOW, 1));
					inv.setItem(4, potH);
					inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 5:	
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					inv.setItem(2, new ItemStack(Material.BOW, 1));
					inv.setItem(3, potS);
					inv.setItem(4, potH);
					inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 6:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					bow = new ItemStack(Material.BOW);
					bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
					inv.setItem(2, bow);
					inv.setItem(3, potS2);
					inv.setItem(4, potH);
					inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 7:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					bow = new ItemStack(Material.BOW);
					bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
					inv.setItem(2, bow);
					inv.setItem(3, potS2);
					inv.setItem(4, potH);
					inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					helmet = new ItemStack(Material.IRON_HELMET);
					helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
					inv.setItem(103, helmet);
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 8:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					bow = new ItemStack(Material.BOW);
					bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
					inv.setItem(2, bow);
					inv.setItem(3, potS2);
					inv.setItem(4, potH2);
					inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					helmet = new ItemStack(Material.IRON_HELMET);
					helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
					inv.setItem(103, helmet);
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				case 9:
					inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1)); // stone maybe ?
					inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
					bow = new ItemStack(Material.BOW);
					bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
					inv.setItem(2, bow);
					inv.setItem(3, potS2);
					inv.setItem(4, potH2);
					inv.setItem(5, new ItemStack(Material.COBBLESTONE, 64));
					inv.setItem(8, new ItemStack(Material.ARROW, 64));
					inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
					helmet = new ItemStack(Material.DIAMOND_HELMET);
					helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
					inv.setItem(103, helmet);
					inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
					inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
					inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
					break;
				default:
					break;
			}
		} else if (this == HEROBRINE) {
		 switch (classLevel){
			case 1:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 5:	
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(103, helmet);
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(3, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(103, helmet);
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			default:
				break;
		  }
		} else if (this == CREEPER) {
		  switch (classLevel){
			case 1:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 5:	
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1);
				inv.setItem(101, leggings);
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setItem(101, leggings);
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				leggings = new ItemStack(Material.IRON_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(101, leggings);
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				inv.setItem(101, leggings);
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			default:
				break;
		  }
			
		} else if (this == SPIRIT_WARRIOR) {
		  switch (classLevel){
			case 1:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 5:	
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setItem(100, boots);
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(100, boots);
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				boots = new ItemStack(Material.IRON_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
				inv.setItem(100, boots);
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				boots = new ItemStack(Material.DIAMOND_BOOTS);
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				boots.addEnchantment(Enchantment.PROTECTION_FALL, 2);
				inv.setItem(100, boots);
				break;
			default:
				break;
		  }
			
		} else if (this == WITHER_MINION) {
		  switch (classLevel){
			case 1:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 32));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 4));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 2:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 48));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 8));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 3:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64));
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 4:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64)); 
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 5:	
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64)); 
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				inv.setItem(103, new ItemStack(Material.IRON_HELMET, 1));
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 6:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64)); 
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				inv.setItem(103, helmet);
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 7:
				inv.setItem(0, new ItemStack(Material.IRON_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64)); 
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(103, helmet);
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 8:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64)); 
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.IRON_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(103, helmet);
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			case 9:
				inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
				inv.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));
				inv.setItem(2, potS2);
				inv.setItem(3, potH2);
				inv.setItem(4, new ItemStack(Material.COBBLESTONE, 64)); 
				inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
				helmet = new ItemStack(Material.DIAMOND_HELMET);
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				inv.setItem(103, helmet);
				inv.setItem(102, new ItemStack(Material.IRON_CHESTPLATE, 1));
				inv.setItem(101, new ItemStack(Material.IRON_LEGGINGS, 1));
				inv.setItem(100, new ItemStack(Material.IRON_BOOTS, 1));
				break;
			default:
				break;
		  }
		}
		return inv.getContents();
 	}
}