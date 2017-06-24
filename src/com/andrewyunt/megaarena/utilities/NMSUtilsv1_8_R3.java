/*
 * Unpublished Copyright (c) 2017 Andrew Yunt, All Rights Reserved.
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
package com.andrewyunt.megaarena.utilities;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class NMSUtilsv1_8_R3 extends NMSUtils {
	
	@Override
	public void playParticle(Player player, Location loc, String particleType) {
		
		if (particleType.equals("snowshovel")) {
            playParticle(player, loc, EnumParticle.SNOW_SHOVEL);
        } else if (particleType.equals("largesmoke")) {
            playParticle(player, loc, EnumParticle.SMOKE_LARGE);
        }
	}
	
	public void playParticle(Player player, Location loc, EnumParticle particleType) {
		
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
				particleType,
				false,
				(float) loc.getX(),
				(float) loc.getY(),
				(float) loc.getZ(),
				0, 0, 0, 1, 0, null);
		
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public ItemStack addGlow(ItemStack is) {
		
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
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