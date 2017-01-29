package com.andrewyunt.megaarena.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.GamePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The general utilities class for methods without a category / methods yet to
 * be categorized into another class.
 * 
 * @author Andrew Yunt
 * @author blablubbabc
 */
public class Utils {
	
	public static Location deserializeLocation(ConfigurationSection section) {
		
		return new Location(Bukkit.getWorld(section.getString("w")), section.getDouble("x"), section.getDouble("y"),
				section.getDouble("z"));
	}
	
	public static Map<String, Object> serializeLocation(Location loc) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("w", loc.getWorld().getName());
		map.put("x", loc.getX());
		map.put("y", loc.getY());
		map.put("z", loc.getZ());
		
		return map;
	}
	
	public static List<String> colorizeList(List<String> list, ChatColor color) {
		
		return list.stream().map(line -> color + line).collect(Collectors.toList());
	}
	
	public static List<org.bukkit.entity.Entity> getNearbyEntities(Location l, int distance) {
		
		return l.getWorld().getEntities().stream()
				.filter(e -> l.distanceSquared(e.getLocation()) <= distance * distance).collect(Collectors.toList());
	}
	
	/**
	 * Rotates the given vector around the y axis. This modifies the given
	 * vector.
	 *
	 * @author blablubbabc
	 * @param vector
	 * 		the vector to rotate
	 * @param angleD
	 * 		the angle of the rotation in degrees
	 */
	public static void rotateYAxis(Vector vector, double angleD) {
		
		vector = vector.clone();
		
		// Validate.notNull(vector);
		if (angleD == 0.0D)
			return;
		
		double angleR = Math.toRadians(angleD);
		double x = vector.getX();
		double z = vector.getZ();
		double cos = Math.cos(angleR);
		double sin = Math.sin(angleR);
		
		vector.setX(x * cos + z * (-sin));
		vector.setZ(x * sin + z * cos);
	}
	
	public static Inventory fromChest(Inventory inv) {
		
		Inventory newInv = Bukkit.createInventory(null, 36);
		
		for (int i = 0; i <= 26; i++) {
			ItemStack is = inv.getItem(i);
			
			if (is == null)
				continue;
			
			newInv.setItem(i + 9, is.clone());
		}
		
		for (int i = 27; i <= 35; i++) {
			ItemStack is = inv.getItem(i);
			
			if (is == null)
				continue;
			
			newInv.setItem(i - 27, is.clone());
		}
		
		return newInv;
	}
	
	public static Inventory toChest(Inventory inv) {
		
		Inventory newInv = Bukkit.createInventory(null, 36);
		
		for (int i = 0; i <= 8; i++) {
			ItemStack is = inv.getItem(i);
			
			if (is == null)
				continue;
			
			newInv.setItem(i + 27, is.clone());
		}
		
		for (int i = 9; i <= 34; i++) {
			ItemStack is = inv.getItem(i);
			
			if (is == null)
				continue;
			
			newInv.setItem(i - 9, is.clone());
		}
		
		return newInv;
	}
	
	public static int getHighestEntry(ConfigurationSection section) {
		
		int highest = 0;
		
		if (section == null)
			return 1;
		
		Set<String> keys = section.getKeys(false);
		
		if (keys.size() == 0)
			return 0;
		
		for (String key : section.getKeys(false)) {
			int num = Integer.valueOf(key);
			
			if (highest < num)
				highest = num;
		}
		
		return highest;
	}
	
	public static String getNumberSuffix(int num) {
		
		num = num % 100;
		
		if (num >= 11 && num <= 13)
			return "th";
		
		switch (num % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}
	
	/**
	 * Plays blood effect for players (in the radius) around the specified
	 * player that are in game and has blood effect on.
	 * 
	 * <p>
	 * Note that the effect will not be played for the specified player.
	 * </p>
	 *
	 * @param damagedPlayer
	 * 		The damaged player (blood will be played on him) - He will not see the effect.
	 * @param bloodRadius
	 * 		The radius from damagedPlayer the effect will be played in.
	 */
	public static void playBloodEffect(Player damagedPlayer, int bloodRadius) {
		
		GamePlayer damagedGP = null;
		
		try {
			damagedGP = MegaArena.getInstance().getPlayerManager().getPlayer(damagedPlayer.getName());
		} catch (PlayerException e) {
			e.printStackTrace();
		}
		
		if (!damagedGP.isInGame())
			return;
		
		Location loc = damagedPlayer.getLocation();
		
		for (Entity entity : Utils.getNearbyEntities(loc, bloodRadius)) {
			if (!(entity instanceof Player))
				continue;
			
			Player player = (Player) entity;
			GamePlayer gp = null;
			
			try {
				gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
			} catch (PlayerException e) {
				e.printStackTrace();
			}
			
			if (player == damagedPlayer)
				continue;
			
			if (!gp.isInGame())
				continue;
			
			if (!gp.hasBloodEffect())
				continue;
			
			player.playEffect(loc.add(0.0D, 0.8D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
		}
	}
	
	public static String getFormattedMessage(String configPath) {
		
		return ChatColor.translateAlternateColorCodes('&', MegaArena.getInstance().getConfig().getString(configPath));
	}
}