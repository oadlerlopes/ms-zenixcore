package br.com.zenix.core.spigot.player.punish;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PunishManager extends Management {
	public PunishManager(CoreManager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public PunishRecord punishPlayer(int punished, int staff, String motive, long expire, PunishType type) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.LAST_ID.toString());
			preparedStatement.setString(1, "global_punish");
			ResultSet resultSet = preparedStatement.executeQuery();

			int lastId = 1;
			if (resultSet.next()) {
				lastId = resultSet.getInt(1);
			}

			resultSet.close();
			preparedStatement.close();

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.PUNISH_PUNISHED_INSERT.toString());
			insertStatment.setInt(1, punished);
			insertStatment.setInt(2, staff);
			insertStatment.setLong(3, System.currentTimeMillis() / 1000);
			insertStatment.setLong(4, expire / 1000);
			insertStatment.setString(5, motive);
			insertStatment.setInt(6, 1);
			insertStatment.setInt(7, type.getId());
			insertStatment.execute();
			insertStatment.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The player " + staff + " punish the player " + punished + " by " + motive + ".");

			return new PunishRecord(lastId, punished, staff, start, expire, motive, true, type);
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to punish the player " + punished + ".", e);
			return null;
		}
	}

	public boolean unPunishPlayer(int punished, int id) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.PUNISH_PUNISHED_UPDATE.toString());
			insertStatment.setInt(1, 0);
			insertStatment.setInt(2, id);
			insertStatment.execute();
			insertStatment.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Updated the punish of the player " + punished + " id " + id + ".");

			return true;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to unpunish the player " + punished + ".", e);
			return false;
		}
	}

	public boolean hasPunishActive(int id, PunishType... type) {
		try {
			long start = System.currentTimeMillis();
			boolean have = false;

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.PUNISH_PUNISHED_SELECT_ACTIVE.toString());
			insertStatment.setInt(1, id);
			insertStatment.setInt(2, 1);
			ResultSet resultSet = insertStatment.executeQuery();

			while (resultSet.next()) {
				for (PunishType types : type) {
					getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Checking if the player " + id + " have one punish of the type " + types.getId() + " active.");

					if (resultSet.getInt(8) == types.getId())
						have = true;
				}
			}

			resultSet.close();
			insertStatment.close();

			return have;
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to check the player punish " + id + ".", e);
			return false;
		}
	}

	public List<PunishRecord> getPlayerPunishRecords(int id) {
		ArrayList<PunishRecord> punishList = new ArrayList<>();

		try {
			long start = System.currentTimeMillis();

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection().prepareStatement(CoreQueries.PUNISH_PUNISHED_SELECT.toString());
			insertStatment.setInt(1, id);
			ResultSet resultSet = insertStatment.executeQuery();

			while (resultSet.next()) {
				PunishRecord punish = new PunishRecord(resultSet.getInt(1));
				punish.setPunished(resultSet.getInt(2));
				punish.setStaff(resultSet.getInt(3));
				punish.setStart(resultSet.getLong(4) * 1000);

				long expire = resultSet.getLong(5);

				punish.setExpire(expire > 1 ? expire * 1000 : expire);
				punish.setMotive(resultSet.getString(6));
				punish.setActive(Utils.getBooleanByInteger(resultSet.getInt(7)));
				punish.setType(PunishType.getType(resultSet.getInt(8)));

				//getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Punish getted: " + punish.toString());

				punishList.add(punish);
			}

			resultSet.close();
			insertStatment.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Getting the players " + id + " punish records.");

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to check the player punish " + id + ".", e);
		}

		return punishList;
	}

	public boolean canPunish(String args) {
		if (Bukkit.getPlayer(args) == null) {
			return true;
		} else if (Utils.isUUID(args) && getCoreManager().getNameFetcher().getId(UUID.fromString(args)) != -1) {
			return true;
		} else if (getCoreManager().getNameFetcher().getId(args) != -1) {
			return true;
		} else {
			return false;
		}
	}

}
