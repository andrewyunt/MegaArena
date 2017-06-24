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
package com.andrewyunt.megaarena.menu;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.exception.GameException;
import com.andrewyunt.megaarena.exception.PlayerException;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.GamePlayer;

public class PlayMenu implements Listener {
	
	private final ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
	
	public PlayMenu() {
		
		ItemMeta glassPaneMeta = glassPane.getItemMeta();
		glassPaneMeta.setDisplayName(" ");
		glassPaneMeta.setLore(new ArrayList<>());
		glassPane.setItemMeta(glassPaneMeta);
	}
	
	public void open(GamePlayer player) {
		
		Inventory inv = Bukkit.createInventory(null, 27, "Play");
		
		for (int i = 0; i < 12; i++) {
            inv.setItem(i, glassPane);
        }
		
		ItemStack FFA = new ItemStack(Material.IRON_SWORD);
		ItemStack TDM = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack tournament = new ItemStack(Material.BLAZE_POWDER);
		ItemStack close = new ItemStack(Material.ARROW);
		
		ItemMeta FFAMeta = FFA.getItemMeta();
		ItemMeta TDMMeta = TDM.getItemMeta();
		ItemMeta tournamentMeta = tournament.getItemMeta();
		ItemMeta closeMeta = close.getItemMeta();
		
		FFAMeta.setDisplayName("Free-for-all");
		TDMMeta.setDisplayName("Team-deathmatch");
		tournamentMeta.setDisplayName("Tournament");
		closeMeta.setDisplayName(ChatColor.RED + "Close");
		
		FFA.setItemMeta(FFAMeta);
		TDM.setItemMeta(TDMMeta);
		tournament.setItemMeta(tournamentMeta);
		close.setItemMeta(closeMeta);
		
		inv.setItem(12, FFA);
		inv.setItem(13, TDM);
		inv.setItem(14, tournament);
		
		for (int i = 15; i < 22; i++) {
            inv.setItem(i, glassPane);
        }
		
		inv.setItem(22, close);
		
		for (int i = 23; i < 27; i++) {
            inv.setItem(i, glassPane);
        }
		
		player.getBukkitPlayer().openInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		Inventory inv = event.getClickedInventory();
		
		if (inv == null) {
            return;
        }
		
		String title = inv.getTitle();
		
		if (title == null) {
            return;
        }
		
		if (!title.equals("Play")) {
            return;
        }
		
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		GamePlayer gp = null;

		try {
			gp = MegaArena.getInstance().getPlayerManager().getPlayer(player.getName());
		} catch (PlayerException e) {
		}
		
		ItemStack is = event.getCurrentItem();
		
		if(is == null || is.getType() == Material.AIR) {
            return;
        }
		
		if (!is.hasItemMeta()) {
            return;
        }

		String name = is.getItemMeta().getDisplayName();
		
		if (name.equals("Free-for-all")) {
			try {
				MegaArena.getInstance().getGameManager().matchMake(gp, Arena.Type.FFA, false);
			} catch (GameException e) {
				player.sendMessage(e.getMessage());
			}
		} else if (name.equals("Team-deathmatch")) {
			try {
				MegaArena.getInstance().getGameManager().matchMake(gp, Arena.Type.TDM, false);
			} catch (GameException e) {
				player.sendMessage(e.getMessage());
			}
		} else if (name.equals("Tournament")) {
			try {
				MegaArena.getInstance().getGameManager().matchMake(gp, null, true);
			} catch (GameException e) {
				player.sendMessage(e.getMessage());
			}
		} else if (name.equals(ChatColor.RED + "Close")) {
            player.closeInventory();
        }
	}
}