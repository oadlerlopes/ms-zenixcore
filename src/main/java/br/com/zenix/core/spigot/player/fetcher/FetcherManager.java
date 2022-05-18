package br.com.zenix.core.spigot.player.fetcher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class FetcherManager extends Management {

	public FetcherManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return true;
	}

	public String getName(String uuid) {
		String name = null;
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.NAME_FETCHER_SELECT.toString().replace("%index%", "unique_id"));

			preparedStatement.setString(1, uuid);

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				name = resultSet.getString(3);
			}

			resultSet.close();
		} catch (SQLException exception) {
			getLogger().log("Error to get the nick of the uuid " + uuid + ".", exception);
			return null;
		}
		return name;
	}

	public String getName(int id) {
		String name = null;
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.NAME_FETCHER_SELECT.toString().replace("%index%", "id"));

			preparedStatement.setInt(1, id);

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				name = resultSet.getString(3);
			}

			resultSet.close();
		} catch (SQLException exception) {
			getLogger().log("Error to get the nick of the uuid " + id + ".", exception);
			return null;
		}
		return name;
	}

	public UUID getUUID(String nick) {
		UUID uuid = null;
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.NAME_FETCHER_SELECT.toString().replace("%index%", "nick"));

			preparedStatement.setString(1, nick);

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				uuid = UUID.fromString(resultSet.getString(2));
			}

			resultSet.close();
		} catch (SQLException exception) {
			getLogger().log("Error to get the uuid of the name " + uuid + ".", exception);
			return null;
		}
		return uuid;
	}

	public int getId(UUID uuid) {
		int id = -1;
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.NAME_FETCHER_SELECT.toString().replace("%index%", "unique_id"));

			preparedStatement.setString(1, uuid.toString());

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			}

			resultSet.close();
		} catch (SQLException exception) {
			getLogger().log("Error to get the id of the uuid " + uuid + ".", exception);
		}
		return id;
	}

	public int getId(String name) {
		int id = -1;
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.NAME_FETCHER_SELECT.toString().replace("%index%", "nick"));

			preparedStatement.setString(1, name.toString());

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			
			resultSet.close();
		} catch (SQLException exception) {
			getLogger().log("Error to get the id of the name " + name + ".", exception);
		}
		return id;
	}

	public UUID makeUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
	}

}
