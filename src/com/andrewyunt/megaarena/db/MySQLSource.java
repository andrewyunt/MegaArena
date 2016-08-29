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
package com.andrewyunt.megaarena.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.utilities.BukkitSerialization;

public class MySQLSource extends DatabaseHandler {
 
	private String ip, database, user, pass;
	private int port;
	private Connection connection;
	private Statement statement;
 
    @Override
    public boolean connect() {
 
        FileConfiguration config = MegaArena.getInstance().getConfig();
 
        ip = config.getString("database-ip");
        port = config.getInt("database-port");
        database = config.getString("database-name");
        user = config.getString("database-user");
        pass = config.getString("database-pass");
        
        try {
			if (connection != null && !connection.isClosed() && statement != null)
			    return true;
			
			synchronized (this) {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, user, pass);
			}
		} catch (SQLException | ClassNotFoundException e) {
			return false;
		}
        
        try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			return false;
		}
 
        return true;
    }
 
    @Override
    public void savePlayer(GamePlayer player) {
    	
        String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
 
        com.andrewyunt.megaarena.objects.Class classType = player.getClassType();
        
        try {     	
			statement.executeUpdate(String.format(
					"INSERT INTO `Players` (`uuid`, `class`, `accepting_duels`, `coins`, `earned_coins`, `kills`)"
							+ " VALUES ('%s', '%s', %s, %s, %s, %s) ON DUPLICATE KEY UPDATE class = '%2$s',"
							+ " accepting_duels = %3$s, coins = %4$s, earned_coins = %5$s, kills = %6$s;",
					uuid,
					classType == null ? "none" : classType.toString(),
					player.isAcceptingDuels() == true ? 1 : 0,
					player.getCoins(),
					player.getEarnedCoins(),
					player.getKills()));
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe(String.format(
					"An error occured while saving %s.", player.getName()));
		}
    }
 
    @Override
    public void loadPlayer(GamePlayer player) {
 
        String uuid = player.getBukkitPlayer().getUniqueId().toString();

        ResultSet resultSet = null;
        
		try {
			resultSet = statement.executeQuery("SELECT * FROM `Players` WHERE `uuid` = '" + uuid + "';");
		} catch (SQLException e) {
			return; // player does not exist, so don't load their data
		}
 
        try {
    		while (resultSet.next()) {
    			String classStr = resultSet.getString("class");
    			
    			if (!classStr.equals("none"))
    				player.setClassType(com.andrewyunt.megaarena.objects.Class.valueOf(classStr));
    			
    			player.setAcceptingDuels(resultSet.getInt("accepting_duels") == 1 ? true : false);
    			player.setCoins(resultSet.getInt("coins"));
    			player.setEarnedCoins(resultSet.getInt("earned_coins"));
    			player.setKills(resultSet.getInt("kills"));
    		}
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s.", player.getName()));
		}
    }

	@Override
	public void saveLayout(GamePlayer player, com.andrewyunt.megaarena.objects.Class classType, Inventory inv) {
		
        String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
        String version = MegaArena.getInstance().getDescription().getVersion();
        
        try {     	
			statement.executeUpdate(String.format(
					"INSERT INTO `Layouts` (`uuid`, `layout`, `inventory`, `version`)"
							+ " VALUES ('%s', '%s', '%s', '%s') ON DUPLICATE KEY UPDATE `layout` = '%2$s',"
							+ "`inventory` = '%3$s';",
					uuid,
					classType.toString(),
					BukkitSerialization.toBase64(inv),
					version));
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe(String.format(
					"An error occured while saving %s.", player.getName()));
		}
	}

	@Override
	public Inventory loadLayout(GamePlayer player, com.andrewyunt.megaarena.objects.Class classType) {
		
		String uuid = player.getBukkitPlayer().getUniqueId().toString();
		String version = MegaArena.getInstance().getDescription().getVersion();
		
        ResultSet resultSet = null;
        
		try {
			resultSet = statement.executeQuery("SELECT * FROM `Layouts` WHERE `uuid` = '" + uuid + "' AND"
					+ " layout = '" + classType.toString() + "' AND `version` = '" + version + "';");
		} catch (SQLException e) {
			return null; // layout doesn't exist
		}
		
        try {
    		while (resultSet.next()) {
    			String layoutStr = resultSet.getString("inventory");
    			return BukkitSerialization.fromBase64(layoutStr);
    		}
		} catch (SQLException | IOException e) {
			MegaArena.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s's %s layout.", player.getName(), 
					player.getClassType().getName()));
		}
        
		return null;
	}
	
	@Override
	public void createPlayersTable() {
		
	    String query = "CREATE TABLE IF NOT EXISTS `Players`"
	            + "  (`uuid`             CHAR(36) PRIMARY KEY NOT NULL,"
	            + "   `class`            CHAR(20) NOT NULL,"
	            + "   `accepting_duels`  INT,"
	            + "   `coins`            INT,"
	            + "   `earned_coins`     INT,"
	            + "   `kills`            INT);";
	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe( "An error occured while creating the Players table.");
		}
	}
	
	@Override
	public void createLayoutsTable() {
		
	    String query = "CREATE TABLE IF NOT EXISTS `Layouts`"
	            + "  (`uuid`             CHAR(36) NOT NULL,"
	            + "   `layout`           CHAR(20) NOT NULL,"
	            + "   `inventory`        VARCHAR(8000) NOT NULL,"
	            + "   `version`          CHAR(10) NOT NULL,"
	            + "   PRIMARY KEY (`uuid`, `layout`));";

	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe( "An error occured while creating the Layouts table.");
		}
	}
}