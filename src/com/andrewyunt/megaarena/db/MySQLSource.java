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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.objects.GamePlayer;

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
		} catch (SQLException e) {
			return false;
		} catch (ClassNotFoundException e1) {
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
					"INSERT INTO " + database + ".Players (uuid, class, coins, earned_coins, kills) VALUES ('%s', '%s', %s, %s, %s);",
					uuid,
					classType == null ? "none" : classType.toString(),
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
			resultSet = statement.executeQuery("SELECT * FROM " + database + ".Players WHERE uuid = '" + uuid + "';");
		} catch (SQLException e) {
			return; // player does not exist, so don't load their data
		}
 
        try {
    		while (resultSet.next()) {
    			String classStr = resultSet.getString("class");
    			
    			if (!classStr.equals("none"))
    				player.setClassType(com.andrewyunt.megaarena.objects.Class.valueOf(classStr));
    			
    			player.setCoins(resultSet.getInt("coins"));
    			player.setEarnedCoins(resultSet.getInt("earned_coins"));
    			player.setKills(resultSet.getInt("kills"));
    		}
		} catch (SQLException e) {
			e.printStackTrace();
			MegaArena.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s.", player.getName()));
		}
    }

	@Override
	public void saveLayout(GamePlayer player, com.andrewyunt.megaarena.objects.Class classType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadLayout(GamePlayer player, com.andrewyunt.megaarena.objects.Class classType) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void createPlayersTable() {
	    String query = "CREATE TABLE IF NOT EXISTS Players"
	            + "  (uuid             VARCHAR(50),"
	            + "   class            VARCHAR(20),"
	            + "   coins            BIGINT,"
	            + "   earned_coins     BIGINT,"
	            + "   kills            BIGINT);";
	    
	    try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe( "An error occured while creating the Players table.");
		}
	}
}