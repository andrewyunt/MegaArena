package com.andrewyunt.megaarena.utilities;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class NMSUtils {
	
	public abstract void playParticle(Player player, Location loc, String particleType);
	
	public abstract ItemStack addGlow(ItemStack is);
}