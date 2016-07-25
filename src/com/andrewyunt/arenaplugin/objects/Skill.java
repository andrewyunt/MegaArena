package com.andrewyunt.arenaplugin.objects;

import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;

public enum Skill {
	
	RESIST,
	SWIFTNESS,
	BOOMERANG,
	MUTUAL_WEAKNESS,
	RECHARGE,
	FLURRY,
	POWERFUL_WEAKNESS,
	SUPPORT,
	WEAKENING_SWING,
	SWIFT_BACKUP,
	SOUL_SUCKER,
	UNDEAD;
	
	public int getLevel(ArenaPlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("arenaplugin.%s.%s", this.toString().toLowerCase(), i)))
				return i;
		
		return 1; 
	}
	
	public void setLevel(ArenaPlayer player, int level) {
		
		ArenaPlugin.getInstance().getPermissions().playerAdd(player.getBukkitPlayer(),
				String.format("arenaplugin.%s.%s", this.toString().toLowerCase(), level));
	}
}