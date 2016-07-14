package com.andrewyunt.arenaplugin.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.andrewyunt.arenaplugin.objects.ArenaPlayer;
import com.andrewyunt.arenaplugin.objects.ClassType;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class Utils {
	
	public static Location deserializeLocation(ConfigurationSection section) {
		
		return new Location(
				Bukkit.getWorld(section.getString("w")),
				section.getDouble("x"),
				section.getDouble("y"),
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
	
	public static List<String> colorizeStringList(List<String> input) {
		
		List<String> colorized = new ArrayList<String>();
		
		for (String line : input)
			colorized.add(ChatColor.translateAlternateColorCodes('&', line));
		
		return colorized;
	}
	
	public static int getClassLevel(ArenaPlayer player, ClassType type) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("arenaplugin.%s.%f", type.toString().toLowerCase(), i)))
				return i;
		
		/* Somehow the player doesn't have permissions for any class level including 1,
		so set their class level to 1 as a default */
		return 1; 
	}
}