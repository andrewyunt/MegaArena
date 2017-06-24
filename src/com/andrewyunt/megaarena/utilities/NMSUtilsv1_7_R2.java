package com.andrewyunt.megaarena.utilities;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.NBTTagList;
import net.minecraft.server.v1_7_R2.PacketPlayOutWorldParticles;

public class NMSUtilsv1_7_R2 extends NMSUtils {
	
	@Override
	public void playParticle(Player player, Location loc, String particleType) {
		
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
				particleType,
				(float) loc.getX(),
				(float) loc.getY(),
				(float) loc.getZ(),
				0, 0, 0, 1, 0);
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	@Override
	public ItemStack addGlow(ItemStack item) {
		
		net.minecraft.server.v1_7_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		
		if (tag == null) {
            tag = nmsStack.getTag();
        }
		
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		
		return CraftItemStack.asCraftMirror(nmsStack);
	}
}