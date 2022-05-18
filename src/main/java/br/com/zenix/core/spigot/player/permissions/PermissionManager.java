package br.com.zenix.core.spigot.player.permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.permissions.type.RankType;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class PermissionManager extends Management {

	private static final HashMap<String, Rank> ranks = new HashMap<>();

	public PermissionManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return loadGroups();
	}

	public static HashMap<String, Rank> getRanks() {
		return ranks;
	}

	public boolean loadGroups() {
		long start = System.currentTimeMillis();
		getLogger().debug("Trying to load all the ranks in the table.");
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_SELECT.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				Tag tag = getCoreManager().getTagManager().getTag(resultSet.getInt(3));
				boolean defaultGroup = Utils.getBooleanByInteger(resultSet.getInt(4));

				if (!ranks.containsKey(name)) {
					Rank rank = new Rank(name.toLowerCase(), id, tag, 0, defaultGroup);
					if (rank.getId() > 14) {
						rank.setVip(true);
					}
					ranks.put(name, rank);
					getLogger().debug("The ranks " + name + " / " + id + " was added to the list of ranks.");
				}
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

	public boolean giveRankPlayer(int id, RankType group, long time) {
		try {
			long start = System.currentTimeMillis();

			time = time / 1000;

			PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.ACCOUNT_UPDATE_RANK.toString());
			defaultStatement.setInt(1, group.getId());
			defaultStatement.setLong(2, time);
			defaultStatement.setInt(3, id);
			defaultStatement.execute();
			defaultStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group of the player " + id
					+ " was changed " + group.getName() + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the player " + id + " permissions.", e);
			return false;
		}
	}

	public int createGroup(String groupName, int lastId) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.LAST_ID.toString());
			preparedStatement.setString(1, "global_groups");
			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.close();
			preparedStatement.close();
			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The last id of table global_groups.");

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_INSERT.toString());
			insertStatment.setString(1, groupName);
			insertStatment.setInt(2, getCoreManager().getTagManager().getTags().values().iterator().next().getId());
			insertStatment.execute();
			insertStatment.close();

			return lastId;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to create the group " + groupName + ".", e);
			return -1;
		}
	}

	private boolean loadPermissions() {
		getLogger().debug("Trying to load all the ranks permissions in the table.");
		long start = System.currentTimeMillis();
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
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

				getLogger().debug("The permission " + permission + "(" + active + ") was added to the group "
						+ rank.getName() + ".");

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
		for (RankType rankType : RankType.values()) {
			if (!ranks.containsKey(rankType.getName())) {
				createGroup(rankType.getName(), rankType.getId());
			}
		}
		return true;
	}

	public void updateAttachment(Player player, Rank rank, PermissionAttachment permissionAttachment) {
		long start = System.currentTimeMillis();

		Permission permission = getCreateWrapper(player.getUniqueId().toString());
		permissionAttachment.setPermission(permission, (permissionAttachment.toString().contains("-") ? false : true));
		permission.getChildren().clear();
		
		for (Map.Entry<String, Boolean> perm : rank.getPermissions().entrySet()) {
			if (!permission.getChildren().containsKey(perm.getKey())) {
				permission.getChildren().put(perm.getKey(), perm.getValue());
			}
		}

		Account account = getCoreManager().getAccountManager().getAccount(player);
		
		if (account != null) {
			updatePermissions(account, permission, permissionAttachment);
		}

		player.recalculatePermissions();
		
		getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Updated the permissions of the player "
				+ player.getName() + " correctly.");
	}

	public void updatePermissions(Account account, Permission permission, PermissionAttachment permissionAttachment) {
		for (Map.Entry<String, Boolean> perm : account.getPermissions().entrySet()) {
			if (!permission.getChildren().containsKey(perm.getKey())) {
				permission.getChildren().put(perm.getKey(), perm.getValue());
			}
		}
	}

	private Permission getCreateWrapper(String name) {
		Permission perm = Bukkit.getPluginManager().getPermission(name);
		if (perm == null) {
			perm = new Permission(name, "Internal Permission", PermissionDefault.FALSE);
			Bukkit.getPluginManager().addPermission(perm);
		}
		return perm;
	}

	public boolean updatePermissionsPlayer(int id, String permission, boolean add, long time) {
		try {
			long start = System.currentTimeMillis();

			time = time / 1000;
			if (!add) {
				PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
						.prepareStatement(CoreQueries.ACCOUNT_PERMISSIONS_DELETE.toString());
				defaultStatement.setString(1, permission);
				defaultStatement.setInt(2, id);
				defaultStatement.execute();
				defaultStatement.close();

			} else {
				PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
						.prepareStatement(CoreQueries.ACCOUNT_PERMISSIONS_INSERT.toString());
				insertStatment.setString(1, permission);
				insertStatment.setInt(2, id);
				insertStatment.setInt(3, 1);
				insertStatment.setLong(4, time);
				insertStatment.execute();
				insertStatment.close();
			}
			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Updated the permission " + permission
					+ " to the player " + id + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the player " + id + " permissions.", e);
			return false;
		}

	}

	public boolean removeSecoundaryRank(int id, Rank rank) {
		try {
			long start = System.currentTimeMillis();
			
			PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.ACCOUNT_SECONDARY_GROUP_DELETE.toString());
			defaultStatement.setInt(1, id);
			defaultStatement.setInt(2, rank.getId());
			defaultStatement.execute();
			defaultStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Removed the group " + rank
					+ " to the player " + id + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the player " + id + " group.", e);
			return false;
		}

	}

	public ArrayList<Permissible> getPlayerPermissions(int id) {
		try {
			long start = System.currentTimeMillis();
			ArrayList<Permissible> permissions = new ArrayList<>();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
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

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The permissions of the player " + id
					+ " was getted.");
			return permissions;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to get the permissions of rg " + id + ".", e);
		}
		return null;
	}

	public boolean giveRankPlayer(int id, Rank rank, long time) {
		try {
			long start = System.currentTimeMillis();

			time = time / 1000;

			PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.ACCOUNT_UPDATE_RANK.toString());
			defaultStatement.setInt(1, rank.getId());
			defaultStatement.setLong(2, time);
			defaultStatement.setInt(3, id);
			defaultStatement.execute();
			defaultStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group of the player " + id
					+ " was changed " + rank.getName() + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the player " + id + " permissions.", e);
			return false;
		}
	}

	public boolean giveSecondaryGroupPlayer(int id, Rank rank, long time) {
		try {
			long start = System.currentTimeMillis();

			time = time / 1000;

			PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.ACCOUNT_INSERT_SECONDARY_RANK.toString());
			defaultStatement.setInt(1, id);
			defaultStatement.setInt(2, rank.getId());
			defaultStatement.setLong(3, time);
			defaultStatement.execute();
			defaultStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group of the player " + id
					+ " was changed " + rank.getName() + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the player " + id + " permissions.", e);
			return false;
		}
	}

	public int createGroup(String groupName) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.LAST_ID.toString());
			preparedStatement.setString(1, "global_groups");
			ResultSet resultSet = preparedStatement.executeQuery();

			int lastId = 1;
			if (resultSet.next()) {
				lastId = resultSet.getInt(1);
			}

			resultSet.close();
			preparedStatement.close();
			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The last id of table global_groups.");

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_INSERT.toString());
			insertStatment.setString(1, groupName);
			insertStatment.setInt(2, getCoreManager().getTagManager().getTags().values().iterator().next().getId());
			insertStatment.execute();
			insertStatment.close();

			Rank rank = new Rank(groupName, lastId,
					getCoreManager().getTagManager().getTags().values().iterator().next(), 0, false);
			ranks.put(groupName.toLowerCase(), rank);

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group " + groupName
					+ " was created " + rank.getId() + ".");

			return lastId;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to create the group " + groupName + ".", e);
			return -1;
		}
	}

	public int copyGroup(Rank rank, String groupName) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.LAST_ID.toString());
			preparedStatement.setString(1, "global_groups");
			ResultSet resultSet = preparedStatement.executeQuery();

			int lastId = 1;
			if (resultSet.next()) {
				lastId = resultSet.getInt(1);
			}

			resultSet.close();
			preparedStatement.close();
			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The last id of table global_groups.");

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_INSERT.toString());
			insertStatment.setString(1, groupName);
			insertStatment.setInt(2, rank.getTag().getId());
			insertStatment.execute();
			insertStatment.close();

			Rank newGroup = new Rank(groupName, lastId, rank.getTag(), 0, false);

			for (String permission : rank.getPermissions().keySet()) {
				boolean value = rank.getPermissions().get(permission);
				updatePermissionGroup(permission, value, newGroup);
			}

			ranks.put(groupName.toLowerCase(), newGroup);

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group " + groupName
					+ " was copied to " + newGroup.getId() + ".");

			return lastId;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to create the group " + groupName + ".", e);
			return -1;
		}
	}

	public boolean deleteGroup(Rank rank) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_DEFAULT_UPDATE_PLAYERS.toString());
			defaultStatement.setInt(1, RankType.MEMBRO.getId());
			defaultStatement.setInt(2, rank.getId());
			defaultStatement.execute();
			defaultStatement.close();

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_DELETE.toString());
			insertStatment.setInt(1, rank.getId());
			insertStatment.execute();
			insertStatment.close();

			if (ranks.containsKey(rank.getName())) {
				ranks.remove(rank.getName());
			}

			getLogger().debug(
					"[" + (System.currentTimeMillis() - start) + "ms] The group " + rank.getName() + " was deleted.");

			return true;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to remove the group " + rank.getName() + ".", e);
			return false;
		}
	}

	public boolean defaultGroup(Rank rank) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_DEFAULT_ALL_UPDATE.toString());
			defaultStatement.setInt(1, 0);
			defaultStatement.execute();
			defaultStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] All the ranks was unseted to default.");

			for (Rank ranks : ranks.values()) {
				ranks.setDefaultRank(false);
			}

			rank.setDefaultRank(true);

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.RANKS_DEFAULT_UPDATE.toString());
			insertStatment.setInt(1, 1);
			insertStatment.setInt(2, rank.getId());
			insertStatment.execute();
			insertStatment.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group " + rank.getName()
					+ " was seted to default.");

			return true;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to remove the group " + rank.getName() + ".", e);
			return false;
		}
	}

	public boolean updatePermissionGroup(String permission, boolean add, Rank rank) {
		try {
			long start = System.currentTimeMillis();
			boolean exists = rank.getPermissions().containsKey(permission);
			boolean active = exists ? rank.getPermissions().get(permission) : false;
			boolean disable = permission.startsWith("-");

			if (exists && active && !add) {

				PreparedStatement defaultStatement = getCoreManager().getDataManager().getMySQL().getConnection()
						.prepareStatement(CoreQueries.RANKS_PERMISSIONS_DELETE.toString());
				defaultStatement.setString(1, permission);
				defaultStatement.setInt(2, rank.getId());
				defaultStatement.execute();
				defaultStatement.close();

				rank.getPermissions().remove(permission);

			} else if (exists && !active) {

				PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
						.prepareStatement(CoreQueries.RAMKS_PERMISSIONS_UPDATE.toString());
				insertStatment.setInt(1, 1);
				insertStatment.setString(2, permission);
				insertStatment.setInt(3, rank.getId());
				insertStatment.execute();
				insertStatment.close();

				rank.getPermissions().put(permission, add);
			} else if (!exists) {

				PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
						.prepareStatement(CoreQueries.RANKS_PERMISSIONS_INSERT.toString());
				insertStatment.setString(1, permission);
				insertStatment.setInt(2, -1);
				insertStatment.setInt(3, rank.getId());
				insertStatment.setInt(4, disable ? 0 : 1);
				insertStatment.setInt(5, -1);
				insertStatment.execute();
				insertStatment.close();

				rank.getPermissions().put(permission, add);

			}

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The group " + rank.getName()
					+ " was update permissions.");

			return true;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to modify the group " + rank.getName() + " permissions.", e);
			return false;
		}
	}

	public Integer getDefaultRankId() {
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
		for (Rank rank : ranks.values())
			if (rank.getId() == id)
				return rank;
		return null;
	}

	public Rank getDefaultRank() {
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
