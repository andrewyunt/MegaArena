package com.andrewyunt.arenaplugin.objects;

import org.bukkit.entity.Player;

import com.andrewyunt.arenaplugin.ArenaPlugin;

public enum Skill {
	
	RESIST("Resist"),
	SWIFTNESS("Swiftness"),
	BOOMERANG("Boomerang"),
	MUTUAL_WEAKNESS("Mutual Weakness"),
	RECHARGE("Recharge"),
	FLURRY("Flurry"),
	POWERFUL_WEAKNESS("Powerful Weakness"),
	SUPPORT("Support"),
	WEAKENING_SWING("Weakening Swing"),
	SWIFT_BACKUP("Swift Backup"),
	SOUL_SUCKER("Soul Sucker"),
	UNDEAD("Undead");
	
	String name;
	
	Skill(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public int getLevel(ArenaPlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("arenaplugin.%s.%s", this.toString().toLowerCase(), i)))
				return i;
		
		return 1;
	}
	
	public void setLevel(ArenaPlayer player, int level) {
		
		ArenaPlugin.getInstance().getServer().dispatchCommand(ArenaPlugin.getInstance().getServer().getConsoleSender(),
				String.format("pex user %s add arenaplugin.%s.%s", player.getName(), this.toString().toLowerCase(), level));
	}
}