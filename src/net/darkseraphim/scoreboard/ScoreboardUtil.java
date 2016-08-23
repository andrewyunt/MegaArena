package net.darkseraphim.scoreboard;

import com.andrewyunt.megaarena.MegaArena;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
*
* @author DarkSeraphim
*/
public class ScoreboardUtil
{
   
    private final Set<String> nope = new HashSet<String>();
   
    private final Map<Scoreboard, ScoreboardOptions> tracked = new WeakHashMap<Scoreboard, ScoreboardOptions>();
   
    private final PacketAdapter listener;
   
    private static ScoreboardUtil instance;
   
    // PUT YOUR OWN MAIN CLASS HERE
    // I need to borrow it for the packet listener :D
    private final Class<? extends JavaPlugin> mainClass = MegaArena.class;
   
    private ScoreboardUtil()
    {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        PacketType[] scoreboardPackets = new PacketType[]
        {
            PacketType.Play.Server.SCOREBOARD_OBJECTIVE,
            PacketType.Play.Server.SCOREBOARD_SCORE,
            PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE,
            PacketType.Play.Server.SCOREBOARD_TEAM
        };
        this.listener = new PacketAdapter(JavaPlugin.getPlugin(this.mainClass), scoreboardPackets)
        {
            @Override
            public void onPacketSending(PacketEvent event)
            {
                PacketType pt = event.getPacketType();
                PacketContainer packet = event.getPacket();
                final Player player = event.getPlayer();
                if(!ScoreboardUtil.this.isTracked(player.getScoreboard()))
                {
                    return;
                }
               
                if(pt == PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
                {
                    Scoreboard sb = player.getScoreboard();
                    ScoreboardOptions opts = ScoreboardUtil.this.getOptions(sb);
                    String oname = packet.getStrings().read(0);
                    if((!oname.equals(player.getName())) && opts.isPlayerObjective(sb.getObjective(oname)))
                        event.setCancelled(true);
                    else if(!ScoreboardUtil.this.nope.contains(player.getName()) && packet.getIntegers().read(0) == 0)
                    {
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                ScoreboardUtil.this.nope.add(player.getName());
                            }
                        }.runTaskLater(JavaPlugin.getPlugin(ScoreboardUtil.this.mainClass), 1L);
                    }
                    else if(ScoreboardUtil.this.nope.contains(player.getName()) && packet.getIntegers().read(0) == 1)
                        event.setCancelled(true);
                }
                else if(pt == PacketType.Play.Server.SCOREBOARD_TEAM)
                {
                    Scoreboard sb = player.getScoreboard();
                    ScoreboardOptions opts = ScoreboardUtil.this.getOptions(sb);
                    String tname = packet.getStrings().read(0);
                    Team t = sb.getTeam(tname);
                    if(t == null)
                        return; // Weird :o
                    opts.overridePacket(packet, player, t);
                }
                else if(pt == PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE)
                {
                    Scoreboard sb = player.getScoreboard();
                    ScoreboardOptions opts = ScoreboardUtil.this.getOptions(sb);
                    String oname = packet.getStrings().read(0);
                    Objective o = sb.getObjective(player.getName());
                    if(!oname.equals(player.getName()) && opts.isPlayerObjective(o)
                     && (opts.getDisplaySlot(o) == packet.getIntegers().read(0)))
                        event.setCancelled(true);
                }
                else if(pt == PacketType.Play.Server.SCOREBOARD_SCORE)
                {
                    Scoreboard sb = player.getScoreboard();
                    ScoreboardOptions opts = ScoreboardUtil.this.getOptions(sb);
                    String oname = packet.getStrings().read(1);
                    Objective o = sb.getObjective(player.getName());
                    Objective o2 = sb.getObjective(oname);
                    if(!oname.equals(player.getName()) && opts.isPlayerObjective(o)
                     && opts.isPlayerObjective(o2) && opts.getDisplaySlot(o) == opts.getDisplaySlot(o2))
                        event.setCancelled(true);
                }
            }
        };
        pm.addPacketListener(this.listener);
    }
   
    /**
     * Returns the singleton instance of the API
     */
    public static ScoreboardUtil getInstance()
    {
        if(instance == null)
            instance = new ScoreboardUtil();
        return instance;
    }
   
    /**
     * Call this in onDisable, does the cleanup
     */
    public static void destroy()
    {
        getInstance()._destroy();
        instance = null;
    }
   
    /**
     * Inner method for cleaning up instance stuff
     */
    @SuppressWarnings("deprecation")
    private void _destroy()
    {
        ProtocolLibrary.getProtocolManager().removePacketListener(this.listener);
        for(Player player : Bukkit.getOnlinePlayers())
            cleanup(player);
        this.tracked.clear();
        this.nope.clear();
    }
   
    /**
     * Add a scoreboard to be tracked. For instance, the main scoreboard
     * This is a mandarory step to start using the API. The ScoreboardOptions
     * instance that it returns can be used to give players their unique
     * Objective (and allows you to revoke it) WITHOUT creating a new scoreboard,
     * and preserving any Teams.
     *
     * Great, isn't it?
     */
    public ScoreboardOptions trackScoreboard(Scoreboard sb)
    {
        ScoreboardOptions options = new ScoreboardOptions(sb);
        this.tracked.put(sb, options);
        return options;
    }
   
    /**
     * Checks if the Scoreboard is currently tracked
     */
    public boolean isTracked(Scoreboard sb)
    {
        return this.tracked.containsKey(sb);
    }
   
    /**
     * Retrieves the ScoreboardOptions of a specific Scoreboard.
     *
     * Returns null if it isn't being tracked by the API
     */
    public ScoreboardOptions getOptions(Scoreboard sb)
    {
        return this.tracked.get(sb);
    }
   
    /**
     * Stops manipulating the Scoreboard. Java GC will do the cleanup here
     */
    public void untrackScoreboard(Scoreboard sb)
    {
        ScoreboardOptions options = this.tracked.remove(sb);
        if(options != null)
        {
            options.cleanup();
        }
    }
   
    /**
     * Removes any references regarding the player.
     */
    public void cleanup(Player player)
    {
        ScoreboardOptions opts = this.tracked.get(player.getScoreboard());
        if(opts != null)
            opts.cleanup(player);
        this.nope.remove(player.getName());
    }
}