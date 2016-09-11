package com.andrewyunt.megaarena.menu;

import java.util.ArrayList;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.Utils;

public class GeneralMenu implements Listener {
	
	private Inventory inv;
	private final ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public GeneralMenu() {
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);
	}
	
	public void openMainMenu(GamePlayer player) {

		inv = Bukkit.createInventory(null, 27, "General");

		ItemStack normalClasses = new ItemStack(Material.IRON_SWORD);
		ItemStack heroClasses = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack close = new ItemStack(Material.ARROW);
		
		ItemMeta normalClassesMeta = normalClasses.getItemMeta();
		ItemMeta heroClassesMeta = heroClasses.getItemMeta();
		ItemMeta closeMeta = close.getItemMeta();
		
		normalClassesMeta.setDisplayName("Normal Classes");
		heroClassesMeta.setDisplayName("Hero Classes");
		closeMeta.setDisplayName("Close");
		
		normalClasses.setItemMeta(normalClassesMeta);
		heroClasses.setItemMeta(heroClassesMeta);
		close.setItemMeta(closeMeta);
		
		for (int i = 0; i < 12; i++)
			inv.setItem(i, glassPane);

		inv.setItem(12, normalClasses);
		inv.setItem(13, glassPane);
		inv.setItem(14, Utils.addGlow(heroClasses));

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, close);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.getBukkitPlayer().openInventory(inv);
	}

	private void openNormalClasses(GamePlayer player) {

		inv = Bukkit.createInventory(null, 27, "General - Normal Classes");
		
		ItemStack zombie = new ItemStack(Material.ROTTEN_FLESH);
		ItemStack skeleton = new ItemStack(Material.BONE);
		ItemStack creeper = new ItemStack(Material.TNT);
		ItemStack herobrine = new ItemStack(Material.ENDER_PEARL);
		ItemStack goBack = new ItemStack(Material.ARROW);
		
		ItemMeta zombieMeta = zombie.getItemMeta();
		ItemMeta skeletonMeta = skeleton.getItemMeta();
		ItemMeta creeperMeta = creeper.getItemMeta();
		ItemMeta herobrineMeta = herobrine.getItemMeta();
		ItemMeta goBackMeta = goBack.getItemMeta();
		
		zombieMeta.setDisplayName("Zombie");
		skeletonMeta.setDisplayName("Skeleton");
		creeperMeta.setDisplayName("Creeper");
		herobrineMeta.setDisplayName("Herobrine");
		goBackMeta.setDisplayName("Go Back");
		
		zombie.setItemMeta(zombieMeta);
		skeleton.setItemMeta(skeletonMeta);
		creeper.setItemMeta(creeperMeta);
		herobrine.setItemMeta(herobrineMeta);
		goBack.setItemMeta(goBackMeta);
		
		for (int i = 0; i < 11; i++)
			inv.setItem(i, glassPane);

		inv.setItem(11, zombie);
		inv.setItem(12, skeleton);
		inv.setItem(13, glassPane);
		inv.setItem(14, creeper);
		inv.setItem(15, herobrine);

		for (int i = 16; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, goBack);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.getBukkitPlayer().openInventory(inv);
	}

	private void openHeroClasses(GamePlayer player) {

		inv = Bukkit.createInventory(null, 27, "General - Hero Classes");

		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemStack witherMinion = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
		ItemStack spiritWarrior = new ItemStack(Material.ENCHANTMENT_TABLE);

		ItemMeta goBackMeta = goBack.getItemMeta();
		ItemMeta spiritWarriorMeta = spiritWarrior.getItemMeta();
		ItemMeta witherMinionMeta = witherMinion.getItemMeta();

		goBackMeta.setDisplayName("Go Back");
		spiritWarriorMeta.setDisplayName("Spirit Warrior");
		witherMinionMeta.setDisplayName("Wither Minion");

		goBack.setItemMeta(goBackMeta);
		spiritWarrior.setItemMeta(spiritWarriorMeta);
		witherMinion.setItemMeta(witherMinionMeta);

		for (int i = 0; i < 12; i++)
			inv.setItem(i, glassPane);

		inv.setItem(12, witherMinion);
		inv.setItem(13, glassPane);
		inv.setItem(14, spiritWarrior);

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, goBack);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.getBukkitPlayer().openInventory(inv);
	}
	
	public void openClassMenu(GamePlayer player, Class classType) {
		
		inv = Bukkit.createInventory(null, 27, "General - Class - " + classType.getName());

		ItemStack upgrades = new ItemStack(Material.EMERALD);
		ItemStack layoutEditor = new ItemStack(Material.CHEST);
		ItemStack goBack = new ItemStack(Material.ARROW);
		
		ItemMeta upgradesMeta = upgrades.getItemMeta();
		ItemMeta layoutEditorMeta = layoutEditor.getItemMeta();
		ItemMeta goBackMeta = goBack.getItemMeta();
		
		upgradesMeta.setDisplayName("Upgrades");
		layoutEditorMeta.setDisplayName("Layout Editor");
		goBackMeta.setDisplayName("Go Back");
		
		upgrades.setItemMeta(upgradesMeta);
		layoutEditor.setItemMeta(layoutEditorMeta);
		goBack.setItemMeta(goBackMeta);
		
		for (int i = 0; i < 12; i++)
			inv.setItem(i, glassPane);

		inv.setItem(12, upgrades);
		inv.setItem(13, glassPane);
		inv.setItem(14, layoutEditor);

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, goBack);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.getBukkitPlayer().openInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		
		if (inv == null)
			return;
		
		String title = inv.getTitle();
		
		if (title == null)
			return;
		
		if (!(title.equals("General - Hero Classes") || title.equals("General - Normal Classes")
				|| title.equals("General") || title.startsWith("General - Class - ")))
			return;
		
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		GamePlayer gp = null;

		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		ItemStack is = event.getCurrentItem();
		
		if(is == null || is.getType() == Material.AIR)
			return;
		
		if (!is.hasItemMeta())
			return;

		String name = is.getItemMeta().getDisplayName();

		if (title.equals("General - Normal Classes") || title.equals("General - Hero Classes")) {
			
			if (name == null || Objects.equals(name, " "))
				return;

			if (name.equals("Go Back")) {
				openMainMenu(gp);
				return;
			}
			
			String classStr = name.replace(" ", "_").toUpperCase();

			if (!player.hasPermission("megaarena.class." + classStr.toLowerCase()))  {
				player.sendMessage(ChatColor.RED + "You do not own that class.");
				return;
			}
			
			openClassMenu(gp, Class.valueOf(classStr));
			
		} else if (title.equals("General")) {

			switch (name) {
				case "Normal Classes":
					openNormalClasses(gp);
					break;
				case "Hero Classes":
					openHeroClasses(gp);
					break;
				case "Close":
					player.closeInventory();
					break;
			}
		} else if (title.startsWith("General - Class - ")) {
			
			String classStr = title.replace("General - Class - ", "").replace(" ", "_").toUpperCase();
			Class classType = Class.valueOf(classStr);
			
			switch (name) {
			case "Upgrades":
				MegaArena.getInstance().getShopMenu().openClassUpgradeMenu(gp, classType);
				break;
			case "Layout Editor":
				MegaArena.getInstance().getLayoutEditorMenu().openClassMenu(gp, classType, true);
				break;
			case "Go Back":
				if (classType.isHero())
					openHeroClasses(gp);
				else
					openNormalClasses(gp);
				break;
			}
		}
	}
}