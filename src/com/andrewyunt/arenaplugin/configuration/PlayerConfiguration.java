package com.andrewyunt.arenaplugin.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.andrewyunt.arenaplugin.ArenaPlugin;

/**
 * 
 * @author Andrew Yunt
 *
 */
public class PlayerConfiguration {
	
	private FileConfiguration config = null;
	private File configFile = null;
	
	public void reloadConfig() {
	    
		if (configFile == null)
			configFile = new File(ArenaPlugin.getInstance().getDataFolder(), "players.yml");

		config = YamlConfiguration.loadConfiguration(configFile);

	    Reader defConfigStream = null;
	    
		try {
			defConfigStream = new InputStreamReader(ArenaPlugin.getInstance().getResource("players.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getConfig() {
		
	    if (config == null)
	        reloadConfig();
	    
	    return config;
	}
	
	public void saveConfig() {
		
	    if (config == null || configFile == null)
	        return;
	    
	    try {
	        getConfig().save(configFile);
	    } catch (IOException ex) {
	        ArenaPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
	    }
	}
	
	public void saveDefaultConfig() {
		
	    if (configFile == null)
	        configFile = new File(ArenaPlugin.getInstance().getDataFolder(), "players.yml");
	        
	    if (!configFile.exists())
	    	ArenaPlugin.getInstance().saveResource("players.yml", false);
	}
}