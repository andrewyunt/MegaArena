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
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.andrewyunt.arenaplugin.ArenaPlugin;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityTracker;
import net.minecraft.server.v1_7_R4.EntityTrackerEntry;
import net.minecraft.server.v1_7_R4.WorldServer;

/**
 * 
 * @author Andrew Yunt
 * @author Gavin Lutz
 * @author Kristian Stangeland
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
	 * 
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
	
	/**
	 * 
	 * @author Kristian Stangeland
	 * @param player
	 * @param distance
	 * @return
	 * 
	 */
	public static List<Player> getPlayersWithin(Player player, int distance) {
		
		List<Player> res = new ArrayList<Player>();
		int d2 = distance * distance;

		for (Player p : ArenaPlugin.getInstance().getServer().getOnlinePlayers())
			if (p.getWorld() == player.getWorld() && p.getLocation().distanceSquared(player.getLocation()) <= d2)
				res.add(p);

		return res;
	}
	
	/**
	 * 
	 * @author Kristian Stangeland
	 * @param observers
	 * 
	 */
	public static void updateEntities(List<Player> observers) {
		
		// Refresh every single player
		for (Player player : observers)
			updateEntity(player, observers);
	}
	
	/**
	 * 
	 * @author Kristian Stangeland
	 * @param entity
	 * @param observers
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void updateEntity(Entity entity, List<Player> observers) {

		World world = entity.getWorld();
		WorldServer worldServer = ((CraftWorld) world).getHandle();

		EntityTracker tracker = worldServer.tracker;
		EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(entity.getEntityId());

		List<EntityPlayer> nmsPlayers = getNmsPlayers(observers);

		entry.trackedPlayers.removeAll(nmsPlayers);
		entry.scanPlayers(nmsPlayers);
	}
	
	/**
	 * 
	 * @author Kristian Stangeland
	 * @param players
	 * @return
	 * 
	 */
	private static List<EntityPlayer> getNmsPlayers(List<Player> players) {
		
		List<EntityPlayer> nsmPlayers = new ArrayList<EntityPlayer>();

		for (Player bukkitPlayer : players) {
			CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
			nsmPlayers.add(craftPlayer.getHandle());
		}

		return nsmPlayers;
	}
}