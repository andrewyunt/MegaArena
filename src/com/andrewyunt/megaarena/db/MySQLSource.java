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

import com.andrewyunt.megaarena.MegaArena;
import com.andrewyunt.megaarena.objects.GamePlayer;
import com.andrewyunt.megaarena.objects.Upgradable;
import com.andrewyunt.megaarena.utilities.BukkitSerialization;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.sql.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLSource extends DataSource {

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
				connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, user,
						pass);
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
	public void disconnect() {

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// do nothing
		}
	}

	@Override
	public void savePlayer(GamePlayer player) {

		if (!player.isLoaded())
			return;

		String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();

		if (MegaArena.getInstance().isEnabled()) {
			BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
			scheduler.runTaskAsynchronously(MegaArena.getInstance(), () -> {
				savePlayer(player, uuid);

				for (Map.Entry<Upgradable, Integer> entry : player.getUpgradeLevels().entrySet()) {
					Upgradable upgradable = entry.getKey();
					int level = entry.getValue();

					setLevel(player, upgradable, level);
				}
			});
		} else {
			savePlayer(player, uuid);

			for (Map.Entry<Upgradable, Integer> entry : player.getUpgradeLevels().entrySet()) {
				Upgradable upgradable = entry.getKey();
				int level = entry.getValue();

				setLevel(player, upgradable, level);
			}
		}
	}

	private void savePlayer(GamePlayer player, String uuid) {

		com.andrewyunt.megaarena.objects.Class classType = player.getClassType();

		try {
			statement.executeUpdate(String.format(
					"INSERT INTO `Players` (`uuid`, `class`, `accepting_duels`, `blood_particles`, `coins`, `earned_coins`, `kills`)"
							+ " VALUES ('%s', '%s', %s, %s, %s, %s, %s) ON DUPLICATE KEY UPDATE class = '%2$s',"
							+ " accepting_duels = %3$s, blood_particles = %4$s, coins = %5$s, earned_coins = %6$s, kills = %7$s;",
					uuid, classType == null ? "none" : classType.toString(), player.isAcceptingDuels() ? 1 : 0,
					player.hasBloodEffect() ? 1 : 0, player.getCoins(), player.getEarnedCoins(), player.getKills()));
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger()
					.severe(String.format("An error occured while saving %s.", player.getName()));
		}
	}

	@Override
	public void loadPlayer(GamePlayer player) {

		String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();

		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.runTaskAsynchronously(MegaArena.getInstance(), () -> {
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

					player.setAcceptingDuels(resultSet.getInt("accepting_duels") == 1);
					player.setBloodEffect(resultSet.getInt("blood_particles") == 1);
					player.setCoins(resultSet.getInt("coins"));
					player.setEarnedCoins(resultSet.getInt("earned_coins"));
					player.setKills(resultSet.getInt("kills"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MegaArena.getInstance().getLogger()
						.severe(String.format("An error occured while loading %s.", player.getName()));
				return;
			}

			player.setLoaded(true);
		});
	}

	@Override
	public void saveLayout(GamePlayer player, com.andrewyunt.megaarena.objects.Class classType, Inventory inv) {

		String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();
		String version = MegaArena.getInstance().getDescription().getVersion();

		BukkitScheduler scheduler = MegaArena.getInstance().getServer().getScheduler();
		scheduler.runTaskAsynchronously(MegaArena.getInstance(), () -> {
			try {
				statement.executeUpdate(String.format(
						"INSERT INTO `Layouts` (`uuid`, `layout`, `level`, `inventory`, `version`)"
								+ " VALUES ('%s', '%s', %s, '%s', '%s') ON DUPLICATE KEY UPDATE `layout` = '%2$s',"
								+ "`inventory` = '%4$s';",
						uuid, classType.toString(), player.getLevel(classType), BukkitSerialization.toBase64(inv),
						version));
			} catch (SQLException e) {
				MegaArena.getInstance().getLogger()
						.severe(String.format("An error occured while saving %s.", player.getName()));
			}
		});
	}

	@Override
	public Inventory loadLayout(GamePlayer player, com.andrewyunt.megaarena.objects.Class classType) {

		String uuid = player.getBukkitPlayer().getUniqueId().toString();
		String version = MegaArena.getInstance().getDescription().getVersion();

		ResultSet resultSet = null;

		try {
			resultSet = statement.executeQuery(
					"SELECT * FROM `Layouts` WHERE `uuid` = '" + uuid + "' AND" + " layout = '" + classType.toString()
							+ "' AND `level` = " + player.getLevel(classType) + " AND `version` = '" + version + "';");
		} catch (SQLException e) {
			return null; // layout doesn't exist
		}

		try {
			while (resultSet.next()) {
				String layoutStr = resultSet.getString("inventory");
				return BukkitSerialization.fromBase64(layoutStr);
			}
		} catch (SQLException | IOException e) {
			MegaArena.getInstance().getLogger().severe(String.format("An error occured while loading %s's %s layout.",
					player.getName(), player.getClassType().getName()));
		}

		return null;
	}

	@Override
	public Map<Integer, Map.Entry<OfflinePlayer, Integer>> getMostKills() {

		Map<Integer, Map.Entry<OfflinePlayer, Integer>> mostKills = new HashMap<Integer, Map.Entry<OfflinePlayer, Integer>>();

		ResultSet resultSet = null;

		try {
			resultSet = statement.executeQuery("SELECT `uuid`, `kills` FROM `Players` ORDER BY `kills` DESC LIMIT 5;");
		} catch (SQLException e) {
			return null;
		}

		try {
			int place = 1;

			while (resultSet.next()) {
				OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(UUID.fromString(resultSet.getString("uuid")));

				mostKills.put(place,
						new AbstractMap.SimpleEntry<OfflinePlayer, Integer>(op, resultSet.getInt("kills")));

				place++;
			}
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe("An error occured while getting players with the most kills.");
		}

		return mostKills;
	}

	@Override
	public int getLevel(GamePlayer player, Upgradable upgradable) {

		String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();

		ResultSet resultSet = null;

		try {
			resultSet = statement.executeQuery("SELECT * FROM `Upgrades` WHERE `uuid` = '" + uuid
					+ "' AND `upgradable` = '" + upgradable.toString() + "';");
		} catch (SQLException e) {
			return 1;
		}

		try {
			while (resultSet.next())
				return resultSet.getInt("level");
		} catch (SQLException e) {
			e.printStackTrace();
			MegaArena.getInstance().getLogger().severe(String.format(
					"An error occured while loading %s's %s upgradable.", player.getName(), upgradable.getName()));
		}

		return 1;
	}

	@Override
	public void setLevel(GamePlayer player, Upgradable upgradable, int level) {

		String uuid = MegaArena.getInstance().getServer().getOfflinePlayer(player.getName()).getUniqueId().toString();

		try {
			statement.executeUpdate(String.format(
					"INSERT INTO `Upgrades` (`uuid`, `upgradable`, `level`)"
							+ " VALUES ('%s', '%s', '%s') ON DUPLICATE KEY UPDATE `level` = '%3$s';",
					uuid, upgradable.toString(), level));
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger()
					.severe(String.format("An error occured while saving %s.", player.getName()));
		}
	}

	@Override
	public void createPlayersTable() {

		String query = "CREATE TABLE IF NOT EXISTS `Players`" + "  (`uuid`             CHAR(36) PRIMARY KEY NOT NULL,"
				+ "   `class`            CHAR(20) NOT NULL," + "   `accepting_duels`  INT,"
				+ "   `blood_particles`  INT," + "   `coins`            INT," + "   `earned_coins`     INT,"
				+ "   `kills`            INT);";

		try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe("An error occured while creating the Players table.");
		}
	}

	@Override
	public void createLayoutsTable() {

		String query = "CREATE TABLE IF NOT EXISTS `Layouts`" + "  (`uuid`             CHAR(36) NOT NULL,"
				+ "   `layout`           CHAR(20) NOT NULL," + "   `level`            INT NOT NULL,"
				+ "   `inventory`        VARCHAR(8000) NOT NULL," + "   `version`          CHAR(10) NOT NULL,"
				+ "   PRIMARY KEY (`uuid`, `layout`, `level`));";

		try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe("An error occured while creating the Layouts table.");
		}
	}

	@Override
	public void createUpgradesTable() {

		String query = "CREATE TABLE IF NOT EXISTS `Upgrades`" + "  (`uuid`             CHAR(36) NOT NULL,"
				+ "   `upgradable`       CHAR(20) NOT NULL," + "   `level`            INT NOT NULL,"
				+ "   PRIMARY KEY (`uuid`, `upgradable`));";

		try {
			statement.execute(query);
		} catch (SQLException e) {
			MegaArena.getInstance().getLogger().severe("An error occured while creating the Upgrades table.");
		}
	}
}