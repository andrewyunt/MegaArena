/*
 * Unpublished Copyright (c) 2016 Andrew Yunt, All Rights Reserved.
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
package com.andrewyunt.megaarena.managers;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.event.EffectApplyEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

/**
 * The class used for calling custom events within the plugin.
 * 
 * @author Andrew Yunt
 */
public class EventManager {

	public void registerEffectApplyEvent() {
		
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(
				new PacketAdapter(MegaArena.getInstance(),
				ListenerPriority.NORMAL,
				PacketType.Play.Server.ENTITY_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {

				if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
					int entityID = event.getPacket().getIntegers().read(0);
					int playerID = event.getPlayer().getEntityId();

					if (entityID == playerID) {
						byte effectID = event.getPacket().getBytes().read(0);
						EffectApplyEvent effectEvent = new EffectApplyEvent(
								event.getPlayer(),
								PotionEffectType.getById(effectID),
								event.isCancelled());
						
						Bukkit.getServer().getPluginManager().callEvent(effectEvent);

						if (effectEvent.isCancelled()) {
	                        event.getPlayer().removePotionEffect(PotionEffectType.getById(effectID));
							
							event.setCancelled(true);
						}
					}
				}
			}
		});
	}
}