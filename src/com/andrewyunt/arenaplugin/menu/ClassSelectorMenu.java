package com.andrewyunt.arenaplugin.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.arenaplugin.ArenaPlugin;
import com.andrewyunt.arenaplugin.exception.PlayerException;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.Class;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ClassSelectorMenu implements Listener {

	private Player player;
	private Inventory inv;
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public ClassSelectorMenu(Player player) {

		this.player = player;
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);

		ArenaPlugin.getInstance().getServer().getPluginManager().registerEvents(this, ArenaPlugin.getInstance());

		openMainMenu();
	}

	private void openMainMenu() {

		inv = Bukkit.createInventory(null, 27, "Class Selector");

		ItemStack normalClasses = new ItemStack(Material.IRON_SWORD);
		ItemStack heroClasses = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack close = new ItemStack(Material.ARROW);
		
		ItemMeta normalClassesMeta = normalClasses.getItemMeta();
		ItemMeta heroClassesMeta = heroClasses.getItemMeta();
		ItemMeta closeMeta = close.getItemMeta();
		
		normalClassesMeta.setDisplayName("NORMAL CLASSES");
		heroClassesMeta.setDisplayName("HERO CLASSES");
		closeMeta.setDisplayName("Close");
		
		normalClasses.setItemMeta(normalClassesMeta);
		heroClasses.setItemMeta(heroClassesMeta);
		close.setItemMeta(closeMeta);
		
		for (int i = 0; i < 12; i++)
			inv.setItem(i, glassPane);

		inv.setItem(12, normalClasses);
		inv.setItem(13, glassPane);
		inv.setItem(14, heroClasses);

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, close);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.openInventory(inv);
	}

	private void openNormalClassSelector() {

		inv = Bukkit.createInventory(null, 27, "Normal Classes");
		
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

		player.openInventory(inv);
	}

	private void openHeroClassSelector() {

		inv = Bukkit.createInventory(null, 27, "Hero Classes");

		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemStack spiritWarrior = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemStack witherMinion = new ItemStack(Material.SKULL_ITEM);

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

		inv.setItem(12, spiritWarrior);
		inv.setItem(13, glassPane);
		inv.setItem(14, witherMinion);

		for (int i = 15; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, goBack);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		String title = inv.getTitle();
		
		if (!(title.equals("Hero Classes") || title.equals("Normal Classes") || title.equals("Class Selector")))
			return;
		
		event.setCancelled(true);

		ArenaPlayer ap = null;

		try {
			ap = ArenaPlugin.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		ItemStack is = event.getCurrentItem();
		
		if (!is.hasItemMeta())
			return;

		String name = is.getItemMeta().getDisplayName();

		if (title.equals("Hero Classes")) {
			
			if (name.equals("Wither Minion"))
				ap.setClassType(Class.WITHER_MINION);
			else if (name.equals("Spirit Warrior"))
				ap.setClassType(Class.SPIRIT_WARRIOR);
			else if (name.equals("Go Back")) {
				openMainMenu();
				return;
			} else
				return;

			player.sendMessage(String.format(ChatColor.GREEN + "You selected the %s class.",
					ChatColor.AQUA + name + ChatColor.GREEN));

			close();

		} else if (title.equals("Normal Classes")) {

			switch (name) {
				case "Go Back":
					openMainMenu();
					break;
				case "Zombie":
				case "Skeleton":
				case "Creeper":
				case "Herobrine":
					ap.setClassType(Class.valueOf(name.toUpperCase()));
					player.sendMessage(String.format(ChatColor.GREEN + "You selected the %s class.",
							ChatColor.AQUA + name + ChatColor.GREEN));
					close();
					break;
			}
			
		} else if (title.equals("Class Selector")) {
			
			if (name.equals("NORMAL CLASSES"))
				openNormalClassSelector();
			else if (name.equals("HERO CLASSES"))
				openHeroClassSelector();
			else if (name.equals("Close")) {
				close();
			}
		}
	}

	public void close() {

		HandlerList.unregisterAll(this);

		player.closeInventory();
	}
}