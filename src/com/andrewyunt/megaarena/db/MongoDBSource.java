/**
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

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.objects.Arena;
import com.andrewyunt.megaarena.objects.Class;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBSource extends DatabaseHandler {
	
	private MongoClient client;
	private DB db;
	private DBCollection playersCollection;
	private Map<String, DBObject> players = new HashMap<String, DBObject>();
	
	@Override
	public boolean connect() {
		
		FileConfiguration config = MegaArena.getInstance().getConfig();
		
		String ip = config.getString("database-ip");
		int port = config.getInt("database-port");
		
        try {
            client = new MongoClient(ip, port);
        } catch (UnknownHostException e) {
            return false;
        }
        
        db = client.getDB(config.getString("database-name"));
        playersCollection = db.getCollection("players");
        
        return true;
	}
	
	// savePlayer method can be found in the DatabaseHandler class
	
	@Override
	public void saveClassType(GamePlayer player) {
		
		String uuid = player.getBukkitPlayer().getUniqueId().toString();
		
		DBObject playerObj = players.get(uuid);
		
		DBObject replacementObj = new BasicDBObject("uuid", uuid);
		
		replacementObj.put("classtype", player.getClassType().toString());
		playersCollection.update(playerObj, replacementObj);
	}
	
	@Override
	public void saveCoins(GamePlayer player) {
		
		String uuid = player.getBukkitPlayer().getUniqueId().toString();
		
		DBObject playerObj = players.get(uuid);
		DBObject replacementObj = new BasicDBObject("uuid", uuid);
		
		replacementObj.put("coins", player.getCoins());
		playersCollection.update(playerObj, replacementObj);
	}
	
	@Override
	public void loadPlayer(GamePlayer player) {
		
		String uuid = player.getBukkitPlayer().getUniqueId().toString();
		
		DBObject field = new BasicDBObject("uuid", uuid);
		DBObject playerObj = playersCollection.findOne(field);
		
		boolean created = false;
		
		if (playerObj == null) {
			playerObj = new BasicDBObject("uuid", uuid);
			playersCollection.insert(playerObj);
			created = true;
			return;
		}
		
		if (!players.containsKey(uuid))
			players.put(uuid, playerObj);
		
		if (created == true)
			return;
		
		loadClassType(player);
		loadCoins(player);
		loadLayout(player, player.getClassType());
	}
	
	@Override
	public void loadClassType(GamePlayer player) {
		
		DBObject playerObj = players.get(player.getBukkitPlayer().getUniqueId().toString());
		
		Class classType = Class.valueOf((String) playerObj.get("classtype"));
		
		if (classType != null)
			player.setClassType(classType);
	}
	
	@Override
	public void loadCoins(GamePlayer player) {
		
		DBObject playerObj = players.get(player.getBukkitPlayer().getUniqueId().toString());
		
		if (playerObj.get("coins") != null)
			player.setCoins((Integer) playerObj.get("coins"));
	}

	@Override
	public void saveLayouts(GamePlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveLayout(GamePlayer player, Class classType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadLayout(GamePlayer player, Class classType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveKill(GamePlayer killer, GamePlayer victim, Date date, Arena arena) {
		// TODO Auto-generated method stub
		
	}
	
	//TODO: Add loadLayout method
}