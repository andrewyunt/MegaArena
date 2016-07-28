package com.andrewyunt.arenaplugin.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * 
 * @author Andrew Yunt
 * @author Gavin Lutz
 *
 */
public class Utils {

	private static final double SENSITIVITY = 0.05;
	private static final double MAX = 5.0;
	private static final double MINOR = 0.915;
	private static final double MAJOR = 0.940;
	private static final double OFFSET = 3.33;

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
	
	public static String[] colorizeArray(String[] description, ChatColor color) {
		
		List<String> colorized = new ArrayList<String>();

		for (String line : description)
			colorized.add(color + line);

		return colorized.toArray(new String[0]);
	}

	/**
	 * 
	 * @author Gavin Lutz
	 * @param player
	 * @return
	 * 
	 */
	public static Player getTargetPlayer(Player player) {
		
		Location location = player.getLocation();
		Player targetPlayer = null;

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (player.getWorld() != p.getWorld() || player.getName().equals(p.getName()))
				continue;
			else if (location.distanceSquared(p.getLocation()) > 16)
				continue;

			if (isInRange(location, p.getLocation())) {
				targetPlayer = p;
				break;
			}
		}

		return targetPlayer;
	}

	/**
	 * 
	 * @author Gavin Lutz
	 * @param location
	 * @param target
	 * @return
	 */
	private static boolean isInRange(Location location, Location target) {
		
		boolean isInRange = false;
		Vector direction = location.getDirection().normalize().add(new Vector(0.0, 0.5, 0.0));
		boolean shouldScrub = location.distanceSquared(target) >= 3.5;

		for (double i = 0; !isInRange && i < MAX; i += SENSITIVITY) {
			Location rotation = rotate(direction, location.getWorld(), i);
			double distance = location.add(rotation).distanceSquared(target);

			if (shouldScrub)
				distance /= OFFSET;

			if (distance > MINOR && distance < MAJOR)
				isInRange = true;
			else
				location.subtract(rotation);
		}

		return isInRange;
	}

	private static Location rotate(Vector vector, World world, double iteration) {
		
		double x = vector.getX() * iteration;
		double y = vector.getY() * iteration;
		double z = vector.getZ() * iteration;

		return new Location(world, x, y, z);
	}
}