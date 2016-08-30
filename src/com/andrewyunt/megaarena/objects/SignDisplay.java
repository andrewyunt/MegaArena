/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reerved.
 *
 * NOTICE: All information contained herein is, and remains the property of Andrew Yunt. The intellectual and technical concepts contained
 * herein are proprietary to Andrew Yunt and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Andrew Yunt. Access to the source code contained herein is hereby forbidden to anyone except current Andrew Yunt and those who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ANDREW YUNT IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package com.andrewyunt.megaarena.objects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.utilities.Utils;

/**
 * The object used to perform operations on signs in the MegaArena plugin.
 * 
 * @author Andrew Yunt
 */
public class SignDisplay {
	
	/**
	 * The enum used for possible sign types.
	 * 
	 * @author Andrew Yunt
	 *
	 */
	public enum Type {
		KILLS
	}
	
	private int configNumber;
	private Type type;
	private Map<Integer, Sign> bukkitSigns = new HashMap<Integer, Sign>();
	
	/**
	 * Creates a sign display of the specified type, location and update interval.
	 * 
	 * @param type
	 * 		The type of the display.
	 * @param loc
	 * 		The location of the display.
	 * @param updateInterval
	 * 		The update interval of the display in ticks.
	 */
	public SignDisplay (int configNumber, SignDisplay.Type type, Location loc, long updateInterval) {
		
		this.configNumber = configNumber;
		this.type = type;
		
		Block block = loc.getWorld().getBlockAt(loc);
		
		if (block instanceof Sign)
			bukkitSigns.put(1, (Sign) block.getState());
		
		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(MegaArena.getInstance(), new Runnable() {
			@Override
			public void run() {
				
				refreshSigns();
			}
		}, 0L, (long) updateInterval);
	}
	
	public Type getType() {
		
		return type;
	}
	
	public int getConfigNumber() {
		
		return configNumber;
	}
	
	public Map<Integer, Sign> getBukkitSigns() {
		
		return bukkitSigns;
	}
	
	public void addSign(int signNumber, Sign sign) {
		
		bukkitSigns.put(signNumber, sign);
	}
	
	public void refreshSigns() {
		
	}
	
	public void save() {
		
		MegaArena plugin = MegaArena.getInstance();
		FileConfiguration signConfig = plugin.getSignConfig().getConfig();
		
		signConfig.set("signs." + configNumber + ".type", type.toString());
		
		ConfigurationSection bukkitSignsSection = signConfig.createSection(
				"signs." + configNumber + ".bukkitSigns");
		
		for (Map.Entry<Integer, Sign> entry : bukkitSigns.entrySet())
			bukkitSignsSection.createSection(String.valueOf(entry.getKey()),
					Utils.serializeLocation(entry.getValue().getLocation()));
		
		MegaArena.getInstance().getSignConfig().saveConfig();
		
		MegaArena.getInstance().getSignManager().loadSign(
				signConfig.getConfigurationSection("signs." + String.valueOf(configNumber)));
	}
	
	public static SignDisplay loadFromConfig(ConfigurationSection section) {
		
		SignDisplay signDisplay = null;
		
		Type type = Type.valueOf(section.getString("type"));
		
		ConfigurationSection signsSection = section.getConfigurationSection("bukkitSigns");
		
		for (String key : signsSection.getKeys(false)) {
			Location loc = Utils.deserializeLocation(signsSection.getConfigurationSection(key));
			
			if (Integer.valueOf(key) == 1)
				signDisplay = new SignDisplay(Integer.valueOf(section.getName()), type, loc, 6000);
			
			signDisplay.addSign(Integer.valueOf(key),
					(Sign) loc.getWorld().getBlockAt(loc).getState());
		}
		
		return signDisplay;
	}
}