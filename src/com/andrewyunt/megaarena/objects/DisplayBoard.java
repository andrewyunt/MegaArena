package com.andrewyunt.megaarena.objects;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * The class used for scoreboards on the side of a player's screen.
 * 
 * @author SaxSalute
 * @author Andrew Yunt
 */
public class DisplayBoard {
	
	private Player player;
	private Scoreboard scoreboard;
	private Objective objective;
	private String title = null;
	private HashMap<String, Score> fields = new HashMap<String, Score>();

	public DisplayBoard(Player player, String title) {
		
		this.player = player;
		this.title = title;

		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective(title, "dummy");
		objective.setDisplayName(title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	/**
	 * Puts a field on the DisplayBoard which is displayed to the player.
	 * 
	 * @param name
	 * 		The display name of the field to be added.
	 */
	public void putField(String text) {
		
		int originalLength = text.length();
		
		while (text.length() < 16)
			text += " ";

		while (true) {
			if (!fields.keySet().contains(text))
				break;

			text = text.substring(0, text.length() - 1);

			if (text.length() < originalLength)
				return;
		}

		fields.put(text, objective.getScore(text));
		fields.get(text).setScore(0);
		
		for (String str : fields.keySet())
			fields.get(str).setScore(fields.get(str).getScore() + 1);
	}

	/**
	 * Clears the board of all fields.
	 */
	public void clear() {
		
		objective.unregister();
		fields.clear();
		
		objective = scoreboard.registerNewObjective(title, "dummy");
		objective.setDisplayName(title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	/**
	 * Sets the title of the board.
	 * 
	 * @param title
	 * 		The title to set the board to.
	 */
	public void setTitle(String title) {
		
		objective.setDisplayName(title);
	}

	/**
	 * Sets the player's scoreboard to the scoreboard contained
	 * within an instance of DisplayBoard class.
	 */
	public void display() {
		
		player.setScoreboard(scoreboard);
	}
	
	/**
	 * Gets the scoreboard contained in an instance DisplayBoard class.
	 * 
	 * @return
	 * 		The scoreboard used in an instance DisplayBoard class.
	 */
	public Scoreboard getScoreboard() {
		
		return scoreboard;
	}
}