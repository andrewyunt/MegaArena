package com.andrewyunt.megaarena.utilities;

import com.andrewyunt.megaarena.objects.Vector3D;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The general utilities class for methods without a category / methods yet
 * to be categorized into another class.
 * 
 * @author Andrew Yunt
 * @author Gavin Lutz
 * @author md_5
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

	/**
	 * Gets the target player in a player's crosshairs.
	 * 
	 * @author Gavin Lutz
	 * @param player
	 * @return
	 */
    public static Player getTargetPlayer(Player player) {
    	
        Player targetPlayer = null;
        Location playerPos = player.getEyeLocation();
        Vector3D playerDir = new Vector3D(playerPos.getDirection());
        Vector3D playerStart = new Vector3D(playerPos);
        Vector3D playerEnd = playerStart.add(playerDir.multiply(100));
 
        for(Player p : player.getWorld().getPlayers()) {
            Vector3D targetPos = new Vector3D(p.getLocation());
            Vector3D minimum = targetPos.add(-0.3, 0, -0.3);
            Vector3D maximum = targetPos.add(0.3, 1.9, 0.3);
            double range = 4.5;
            
            if (p.getLocation().distanceSquared(player.getLocation()) > range*range)
            	continue;
            
            List<Block> blocks = null;
            
            try {
            	blocks = p.getLastTwoTargetBlocks(null, (int) range);
            } catch (IllegalStateException e) {
            	return null;
            }
            
            for (Block b : blocks)
            	if (b.getType().isOccluding())
            		return targetPlayer;
            	
            if(p != player && hasIntersection(playerStart, playerEnd, minimum, maximum))
                if(targetPlayer == null || targetPlayer.getLocation().distanceSquared(playerPos) > p.getLocation().distanceSquared(playerPos))
                    targetPlayer = p;
        }
       
        return targetPlayer;
    }
   
    /**
     * Checks if vectors are intersecting with each other.
     * 
     * @author Gavin Lutz
     * @param p1
     * @param p2
     * @param min
     * @param max
     * @return
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

		return Math.abs(d.x * c.y - d.y * c.x) <= e.x * ad.y + e.y * ad.x + epsilon;
	}
    
    public static List<org.bukkit.entity.Entity> getNearbyEntities(Location l, int distance){

		return l.getWorld().getEntities().stream().filter(e -> l.distanceSquared(e.getLocation()) <= distance * distance).collect(Collectors.toList());
    }
    
    /**
     * Rotates the given vector around the y axis.
     * This modifies the given vector.
     *
     * @author blablubbabc
     * @param vector
     *            the vector to rotate
     * @param angleD
     *            the angle of the rotation in degrees
     * @return the given vector rotated
     */
    public static Vector rotateYAxis(Vector vector, double angleD) {
    	
        // Validate.notNull(vector);
        if (angleD == 0.0D)
        	return vector;
 
        double angleR = Math.toRadians(angleD);
        double x = vector.getX();
        double z = vector.getZ();
        double cos = Math.cos(angleR);
        double sin = Math.sin(angleR);
 
        vector.setX(x * cos + z * (-sin));
        vector.setZ(x * sin + z * cos);
 
        return vector;
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
    
    public static ItemStack addGlow(ItemStack item){ 
    	
    	net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
    	NBTTagCompound tag = null;
    	
    	if (!nmsStack.hasTag()) {
    		tag = new NBTTagCompound();
    		nmsStack.setTag(tag);
    	}
    	
    	if (tag == null)
    		tag = nmsStack.getTag();
    	
    	NBTTagList ench = new NBTTagList();
    	tag.set("ench", ench);
    	nmsStack.setTag(tag);
    	
    	return CraftItemStack.asCraftMirror(nmsStack);
    }
}