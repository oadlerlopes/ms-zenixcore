package br.com.zenix.core.proxy.manager.managements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import br.com.zenix.core.spigot.player.permissions.Permissible;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.permissions.type.RankType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class GroupManager extends SimpleHandler {

	private static final HashMap<String, Rank> ranks = new HashMap<>();

	public GroupManager(ProxyManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return loadRanks();
	}

	public boolean loadRanks() {
		long start = System.currentTimeMillis();
		System.out.print("Trying to load all the ranks in the table.");
		try {
			PreparedStatement preparedStatement = getProxyManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_SELECT.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				boolean defaultGroup = Utils.getBooleanByInteger(resultSet.getInt(4));

				Rank rank = new Rank(name.toLowerCase(), id, null, 0, defaultGroup);
				if (rank.getId() < 14) {
					rank.setVip(true);
				}
				ranks.put(name, rank);
				System.out.print("The ranks " + name + " was added to the list of ranks.");

			}

			resultSet.close();
			preparedStatement.close();

			getLogger().log(
					"[" + (System.currentTimeMillis() - start) + "ms] All ranks was loaded by the tables in mysql.");
		} catch (Exception e) {
			getLogger().error("Error when the plugin tryed to load the ranks.", e);
			return false;
		}
		return loadPermissions();
	}

	private boolean loadPermissions() {
		System.out.print("Trying to load all the ranks permissions in the table.");
		long start = System.currentTimeMillis();
		try {
			PreparedStatement preparedStatement = getProxyManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_PERMISSIONS_SELECT.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int groupid = resultSet.getInt(4);

				if (groupid <= 0)
					continue;

				Rank rank = getRank(groupid);

				if (rank == null)
					continue;

				String permission = resultSet.getString(2);
				boolean active = Utils.getBooleanByInteger(resultSet.getInt(5));

				rank.getPermissions().put(permission, active);

			}

			resultSet.close();
			preparedStatement.close();

			getLogger().log(
					"[" + (System.currentTimeMillis() - start) + "ms] All the permissions of the ranks was loaded.");

		} catch (Exception e) {
			getLogger().error("Error when the plugin tryed to load the ranks permissions.", e);
			return false;
		}
		return true;
	}

	public ArrayList<Permissible> getPlayerPermissions(int id) {
		try {
			long start = System.currentTimeMillis();
			ArrayList<Permissible> permissions = new ArrayList<>();

			PreparedStatement preparedStatement = getProxyManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.ACCOUNT_PERMISSIONS_SELECT.toString());
			preparedStatement.setInt(1, id);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String name = resultSet.getString(2);
				boolean active = Utils.getBooleanByInteger(resultSet.getInt(5));
				long time = resultSet.getLong(6) * 1000;

				permissions.add(new Permissible(name, active, time));
			}

			resultSet.close();
			preparedStatement.close();

			System.out.print("[" + (System.currentTimeMillis() - start) + "ms] The permissions of the player " + id
					+ " was getted.");
			return permissions;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to get the permissions of rg " + id + ".", e);
		}
		return null;
	}

	public boolean giveGroupPlayer(int id, Rank rank, long time) {
		try {
			long start = System.currentTimeMillis();

			time = time / 1000;

			PreparedStatement defaultStatement = getProxyManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.ACCOUNT_UPDATE_RANK.toString());
			defaultStatement.setInt(1, rank.getId());
			defaultStatement.setLong(2, time);
			defaultStatement.setInt(3, id);
			defaultStatement.execute();
			defaultStatement.close();

			System.out.print("[" + (System.currentTimeMillis() - start) + "ms] The group of the player " + id
					+ " was changed " + rank.getName() + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the player " + id + " permissions.", e);
			return false;
		}
	}

	public Integer getDefaultGroupId() {
		return 18;
	}

	public RankType getRankType(int id) {
		for (RankType rankType : RankType.values())
			if (rankType.getId() == id)
				return rankType;
		return null;
	}

	public RankType getRankType(String name) {
		for (RankType rankType : RankType.values())
			if (rankType.getName().equalsIgnoreCase(name))
				return rankType;
		return null;
	}

	public Rank getRank(int id) {
		for (Rank rank : ranks.values()) {
			if (rank.getId() == id) {
				return rank;
			}
		}
		return null;
	}

	public Rank getRank() {
		for (Rank rank : ranks.values())
			if (rank.isDefaultRank())
				return rank;
		return null;
	}

	public Rank getRank(String name) {
		for (Rank rank : ranks.values())
			if (rank.getName().equalsIgnoreCase(name))
				return rank;
		return null;
	}

}
