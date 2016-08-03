package com.andrewyunt.megaarena.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Ability;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.Skill;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class ShopMenu implements Listener {

	private Player player;
	private GamePlayer ap;
	private Inventory inv;
	private ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

	public ShopMenu(Player player) {

		this.player = player;
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<String>());
		glassPane.setItemMeta(glassPaneMeta);
		
		MegaArena.getInstance().getServer().getPluginManager().registerEvents(this, MegaArena.getInstance());
		
		try {
			ap = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}

		openClassUpgradesMenu();
	}
	
	private void openClassUpgradesMenu() {
		
		inv = Bukkit.createInventory(null, 27, "Class Upgrades");

		ItemStack zombie = new ItemStack(Material.ROTTEN_FLESH);
		ItemStack skeleton = new ItemStack(Material.BONE);
		ItemStack creeper = new ItemStack(Material.TNT);
		ItemStack herobrine = new ItemStack(Material.ENDER_PEARL);
		ItemStack witherMinion = new ItemStack(Material.ENCHANTMENT_TABLE);
		ItemStack spiritWarrior = new ItemStack(Material.SKULL_ITEM);
		ItemStack close = new ItemStack(Material.ARROW);
		
		ItemMeta zombieMeta = zombie.getItemMeta();
		ItemMeta skeletonMeta = skeleton.getItemMeta();
		ItemMeta creeperMeta = creeper.getItemMeta();
		ItemMeta herobrineMeta = herobrine.getItemMeta();
		ItemMeta witherMinionMeta = witherMinion.getItemMeta();
		ItemMeta spiritWarriorMeta = spiritWarrior.getItemMeta();
		ItemMeta closeMeta = close.getItemMeta();
		
		zombieMeta.setDisplayName("Zombie");
		skeletonMeta.setDisplayName("Skeleton");
		creeperMeta.setDisplayName("Creeper");
		herobrineMeta.setDisplayName("Herobrine");
		witherMinionMeta.setDisplayName("Wither Minion");
		spiritWarriorMeta.setDisplayName("Spirit Warrior");
		closeMeta.setDisplayName("Close");
		
		zombie.setItemMeta(zombieMeta);
		skeleton.setItemMeta(skeletonMeta);
		creeper.setItemMeta(creeperMeta);
		herobrine.setItemMeta(herobrineMeta);
		witherMinion.setItemMeta(witherMinionMeta);
		spiritWarrior.setItemMeta(spiritWarriorMeta);
		close.setItemMeta(closeMeta);
		
		for (int i = 0; i < 10; i++)
			inv.setItem(i, glassPane);

		inv.setItem(10, zombie);
		inv.setItem(11, skeleton);
		inv.setItem(12, creeper);
		inv.setItem(14, herobrine);
		inv.setItem(13, glassPane);
		inv.setItem(15, witherMinion);
		inv.setItem(16, spiritWarrior);

		for (int i = 17; i < 22; i++)
			inv.setItem(i, glassPane);

		inv.setItem(22, close);

		for (int i = 23; i < 27; i++)
			inv.setItem(i, glassPane);

		player.openInventory(inv);
	}

	private void openClassUpgradeMenu(Class classType) {

		inv = Bukkit.createInventory(null, 45, "Class Upgrades - " + classType.getName());

		Ability ability = classType.getAbility();
		Skill skillOne = classType.getSkillOne();
		Skill skillTwo = classType.getSkillTwo();

		int abilityLevel = ability.getLevel(ap);
		int skillOneLevel = skillOne.getLevel(ap);
		int skillTwoLevel = skillTwo.getLevel(ap);
		int kitLevel = classType.getKitLevel(ap);
		
		FileConfiguration config = MegaArena.getInstance().getConfig();

		List<String> lines = new ArrayList<String>();
		lines.add(ability.toString());
		lines.add(skillOne.toString());
		lines.add(skillTwo.toString());
		lines.add(classType.toString());
		
		int pass = 0;
		
		for (String line : lines) {
			int level = 0;
			int i = 0;
			int curLevel = 1;
			
			switch (pass) {
				case 0:
					level = abilityLevel;
					break;
				case 1:
					level = skillOneLevel;
					i = 9;
					curLevel = i - 8;
					break;
				case 2:
					level = skillTwoLevel;
					i = 18;
					curLevel = i - 17;
					break;
				case 3:
					level = kitLevel;
					i = 27;
					curLevel = i - 26;
					break;
			}
			
			int stop = i + 9;
			boolean available = false;
			
			while (i < stop) {
				ItemStack is = null;
				ConfigurationSection section = config.getConfigurationSection("classes." + classType.toString() 
				+ "." + line + "." + String.valueOf(curLevel));
				
				String name = null;
				List<String> lore = null;
				
				if (section.contains("title") && section.contains("description")) {
					name = section.getString("title");
					lore = new ArrayList<String>(Arrays.asList(section.getString("description").split("\\r?\\n")));
				}
				
				lore.add("");

				ChatColor color = null;
				int cost = config.getInt("tier-" + String.valueOf(curLevel) + "-upgrade-cost");
				
				if (available == true) {
					is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
					color = ChatColor.RED;
				} else
					if (level >= curLevel) {
						is = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
						color = ChatColor.GREEN;
						lore.add("Purchased");
					} else {
						lore.add("Cost: " + String.valueOf(cost));
						
						if (ap.getCoins() < cost) {
							is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
							color = ChatColor.RED;
						} else {
							is = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
							color = ChatColor.YELLOW;
							available = true;
						}
					}
				
				lore = Utils.colorizeList(lore, color);
				ItemMeta meta = is.getItemMeta();
				
				meta.setDisplayName(color + name);
				meta.setLore(lore);
				is.setItemMeta(meta);
				
				inv.setItem(i, is);
				
				curLevel++;
				i++;
			}
			
			pass++;
		}
		
		ItemStack goBack = new ItemStack(Material.ARROW);
		ItemMeta goBackMeta = goBack.getItemMeta();
		
		goBackMeta.setDisplayName("Go Back");
		goBack.setItemMeta(goBackMeta);
		
		for (int i = 36; i < 40; i++)
			inv.setItem(i, glassPane);
		
		inv.setItem(40, goBack);
		
		for (int i = 41; i < 45; i++)
			inv.setItem(i, glassPane);
		
		player.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (event.getWhoClicked() != player)
			return;
		
		String title = inv.getTitle();
		
		if (!(title.startsWith("Class Upgrades")))
			return;
		
		event.setCancelled(true);
		
		ItemStack is = event.getCurrentItem();
		String name = is.getItemMeta().getDisplayName();

		if (title.equals("Class Upgrades")) {
			
			switch (name) {
				case "Close":
					close();
					break;
				case "Zombie":
				case "Skeleton":
				case "Creeper":
				case "Herobrine":
				case "Wither Minion":
				case "Spirit Warrior":
					openClassUpgradeMenu(Class.valueOf(name.toUpperCase().replace(' ', '_')));
					break;
			}
			
		} else {
			
			Class classType = Class.valueOf(title.split("\\-", -1)[1].toUpperCase().substring(1).replace(' ', '_'));
			
			if (is.getType() == Material.ARROW) {
				openClassUpgradesMenu();
				return;
			}

			if (is.getType() != Material.STAINED_CLAY)
				return;
			
			if (is.getDurability() == 14) {
				player.sendMessage(ChatColor.RED + "You must unlock the preceding upgrades or you cannot afford that upgrade.");
				return;
			} else if (is.getDurability() == 5) {
				player.sendMessage(ChatColor.RED + "You have already puchased that class upgrade.");
				return;
			}
			
			Inventory inv = event.getInventory();
			
			int slot = event.getSlot();
			
			if (slot < 9) {
				
				slot++;
				Ability ability = classType.getAbility();
				
				ability.setLevel(ap, slot);
				ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
						ability.getName() + ChatColor.GREEN));
			
			} else if (9 <= slot && slot < 18) {
				
				slot = slot - 8;
				Skill skillOne = classType.getSkillTwo();
				
				skillOne.setLevel(ap, slot);
				ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
						skillOne.getName() + ChatColor.GREEN));
		
			} else if (18 <= slot && slot < 27) {
			
				slot = slot - 17;		
				Skill skillTwo = classType.getSkillTwo();
				
				skillTwo.setLevel(ap, slot);
				ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s upgrade purchased successfully.",
						skillTwo.getName() + ChatColor.GREEN));
		
			} else if (27 <= slot && slot < 36) {
			
				slot = slot - 26;
				
				classType.setKitLevel(ap, slot);
				ap.getBukkitPlayer().sendMessage(ChatColor.AQUA + String.format("%s kit upgrade purchased successfully.",
						classType.getName() + ChatColor.GREEN));
			}
			
			int cost = MegaArena.getInstance().getConfig().getInt("tier-" + String.valueOf(slot) + "-upgrade-cost");
			ap.removeCoins(cost);
			
			player.openInventory(inv);
		}
	}
	
	public void close() {
		
		HandlerList.unregisterAll(this);

		player.closeInventory();
	}
}