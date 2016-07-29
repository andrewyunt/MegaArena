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
import com.andrewyunt.arenaplugin.objects.Vector3D;

/**
 * 
 * @author Andrew Yunt
 * @author Gavin Lutz
 *
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
    	
        Player targetPlayer = null;
        Location playerPos = player.getEyeLocation();
        Vector3D playerDir = new Vector3D(playerPos.getDirection());
        Vector3D playerStart = new Vector3D(playerPos);
        Vector3D playerEnd = playerStart.add(playerDir.multiply(100));
 
        for(Player p : player.getWorld().getPlayers()) {
            Vector3D targetPos = new Vector3D(p.getLocation());
            Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
            Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);
 
            if(p != player && hasIntersection(playerStart, playerEnd, minimum, maximum))
                if(targetPlayer == null || targetPlayer.getLocation().distanceSquared(playerPos) > p.getLocation().distanceSquared(playerPos))
                    targetPlayer = p;
        }
       
        return targetPlayer;
    }
   
    /**
     * 
     * @author Gavin Lutz
     * @param p1
     * @param p2
     * @param min
     * @param max
     * @return
     * 
     */
    private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
    	
        final double epsilon = 0.0001f;
        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();
 
        if(Math.abs(c.x) > e.x + ad.x)
        	return false;
        if(Math.abs(c.y) > e.y + ad.y)
        	return false;
        if(Math.abs(c.z) > e.z + ad.z)
        	return false;
 
        if(Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
        	return false;
        if(Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon) 
        	return false;
        if(Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
        	return false;
 
        return true;
    }
}