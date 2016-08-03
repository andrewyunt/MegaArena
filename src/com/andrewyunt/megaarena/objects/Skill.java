package com.andrewyunt.megaarena.objects;

import org.bukkit.entity.Player;

import com.andrewyunt.megaarena.MegaArena;

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
	
	public int getLevel(GamePlayer player) {
		
		Player bp = player.getBukkitPlayer();
		
		for (int i = 9; i > 1; i--)
			if (bp.hasPermission(String.format("megaarena.%s.%s", this.toString().toLowerCase(), i)))
				return i;
		
		return 1;
	}
	
	public void setLevel(GamePlayer player, int level) {
		
		MegaArena.getInstance().getServer().dispatchCommand(MegaArena.getInstance().getServer().getConsoleSender(),
				String.format("pex user %s add megaarena.%s.%s", player.getName(), this.toString().toLowerCase(), level));
	}
}