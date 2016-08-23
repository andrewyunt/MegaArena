package net.darkseraphim.scoreboard;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
*
* @author DarkSeraphim
*/
public class ScoreboardOptions
{
   
    static Scoreboard sb;
   
    static final Map<String, Integer> personal = new HashMap<String, Integer>();
   
    static final Map<String, Map<String, TeamOptions>> teamopts = new HashMap<String, Map<String, TeamOptions>>();
   
    public ScoreboardOptions(Scoreboard sb)
    {
        ScoreboardOptions.sb = sb;
    }
   
    static TeamOptions getTeamOptions(Player player, Team team)
    {
        Map<String, TeamOptions> gopts = teamopts.get(player.getName());
        TeamOptions opts = null;
        if(gopts == null)
        {
            gopts = new HashMap<String, TeamOptions>();
            teamopts.put(player.getName(), gopts);
        }
        else
            opts = gopts.get(team.getName());
        if(opts == null)
        {
            opts = new TeamOptions(team);
            gopts.put(team.getName(), opts);
        }
        return opts;
    }
   
    /**
     * Sets the prefix for the team of a specific scoreboard, but only
     * for the specified player
     */
    public static void setPrefix(Player player, Team team, String prefix)
    {
        if(!isTracked(player))
            return;
        if(!hasTeam(player, team))
            return;
        getTeamOptions(player, team).setPrefix(prefix);
    }
   
    /**
     * Sets the suffix for the team of a specific scoreboard, but only
     * for the specified player
     */
    public void setSuffix(Player player, Team team, String suffix)
    {
        if(!isTracked(player))
            return;
        if(!hasTeam(player, team))
            return;
        getTeamOptions(player, team).setSuffix(suffix);
    }
   
    protected void overridePacket(PacketContainer teamPacket, Player player, Team team)
    {
        StructureModifier<Integer> ints = teamPacket.getIntegers();
        if(ints.read(0) != 0 && ints.read(0) != 2)
            return;
       
        TeamOptions opts = getTeamOptions(player, team);
        if(opts == null)
            return;
        StructureModifier<String> strings = teamPacket.getStrings();
        strings.write(2, opts.getPrefix());
        strings.write(3, opts.getSuffix());
        int i = ints.read(1);
       
        /**
         * Do note that the following most likely has no effect
         * whatsoever on the teams
         */
        if(opts.canSeeInvisibles())
            i |= 0x2;
        else
            i &= ~0x2;
        ints.write(1, i);
    }
   
    /**
     * Cleans up any references to the player from the options
     */
    public void cleanup(Player player)
    {
        teamopts.remove(player.getName());
        personal.remove(player.getName());
    }
   
    public void cleanup()
    {
        teamopts.clear();
        personal.clear();
    }
   
    static boolean isTracked(Player player)
    {
        return ScoreboardUtil.getInstance().isTracked(player.getScoreboard());
    }
   
    static boolean hasTeam(Player player, Team team)
    {
        return  team != null && player.getScoreboard().getTeam(team.getName()) == team;
    }
   
    /**
     * Generates a player specific Objective and shows it to the player.
     * If you need this later on, get the scoreboard and call
     * Scoreboard#getObjective(player.getName());
     *
     * Note: this will remove any objective that is using the player's name,
     * I use this as an 'unique' id
     */
    public Objective getPlayerObjective(Player player, DisplaySlot slot)
    {
        if(!isTracked(player))
            return null;
        String name = player.getName();
        personal.put(name, displaySlotToInt(slot));
        Scoreboard sb = player.getScoreboard();
        Objective o = sb.getObjective(name);
        if(o == null)
            o = sb.registerNewObjective(name, "dummy");
        o.setDisplaySlot(slot);
        return o;
    }
   
    static int displaySlotToInt(DisplaySlot slot)
    {
        switch(slot)
        {
            case BELOW_NAME:
                return 2;
            case SIDEBAR:
                return 1;
            case PLAYER_LIST:
                return 0;
        }
        return -1;
    }
   
    /**
     * Checks whether an Objective is a personal Objective
     */
    public boolean isPlayerObjective(Objective o)
    {
        if(o == null)
            return false;
        return o.getScoreboard() == sb && personal.containsKey(o.getName());
    }
   
    public int getDisplaySlot(Objective o)
    {
        return personal.get(o.getName());
    }
   
    static class TeamOptions
    {
        String prefix;
        String suffix;
        boolean canSeeInvis;
       
        TeamOptions(Team team)
        {
            this.prefix = team.getPrefix();
            this.suffix = team.getSuffix();
            this.canSeeInvis = team.canSeeFriendlyInvisibles();
        }
       
        public void setPrefix(String prefix)
        {
            this.prefix = prefix;
        }
       
        public String getPrefix()
        {
            return this.prefix;
        }
       
        public void setSuffix(String suffix)
        {
            this.suffix = suffix;
        }
       
        public String getSuffix()
        {
            return this.suffix;
        }
       
        /**
         * Do note that the following most likely has no effect
         * whatsoever on the teams
         */
        public boolean canSeeInvisibles()
        {
            return this.canSeeInvis;
        }
       
        public void setCanSeeInvisibles(boolean canSeeInvis)
        {
            this.canSeeInvis = canSeeInvis;
        }
    }
}