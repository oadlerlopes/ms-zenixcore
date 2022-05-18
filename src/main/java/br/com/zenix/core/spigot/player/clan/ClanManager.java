package br.com.zenix.core.spigot.player.clan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.clan.constructor.ClanConstructor;
import br.com.zenix.core.spigot.player.clan.data.ClanQueries;
import br.com.zenix.core.spigot.player.clan.groups.ClanHierarchy;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class ClanManager extends Management {

	private static final HashMap<String, Clan> clans = new HashMap<>();
	public static final HashMap<UUID, String> invites = new HashMap<>();

	public ClanManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return loadClans();
	}

	public HashMap<UUID, String> getInvites() {
		return invites;
	}

	public static HashMap<String, Clan> getClans() {
		return clans;
	}

	public boolean loadClans() {
		return true;
	}

	public boolean createClan(Clan clan) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.CREATE_CLAN.toString());
			preparedStatement.setString(1, clan.getName().toUpperCase());
			preparedStatement.setString(2, clan.getTag().toUpperCase());
			preparedStatement.setInt(3, 0);
			preparedStatement.setInt(4, 0);
			preparedStatement.setInt(5, 0);
			preparedStatement.setInt(6, 0);
			preparedStatement.executeUpdate();
			preparedStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Created the clan " + clan + ".");

			return true;
		} catch (Exception ex) {
			getLogger().error("Error when the plugin tried to create clan.");
		}
		return false;
	}

	public boolean deleteClan(String clan) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("DELETE FROM znx_clan_data WHERE name='" + clan + "'");
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception ex) {
			System.out.print("");
		}
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("DELETE FROM znx_clan_player_data WHERE name='" + clan + "'");
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception ex) {
			System.out.print("");
		}
		return true;
	}

	public ClanConstructor getClanPlayers(String clan) {
		List<String> playerList = new ArrayList<>();
		HashMap<String, String> playerRank = new HashMap<>();

		try {
			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("SELECT FROM znx_clan_player_data WHERE name='" + clan + "'");
			ResultSet resultSet = insertStatment.executeQuery();

			while (resultSet.next()) {
				playerList.add(getCoreManager().getNameFetcher().getName(resultSet.getString(2)));
				playerRank.put(resultSet.getString(3), getCoreManager().getNameFetcher().getName(resultSet.getString(2)));
			}

			resultSet.close();
			insertStatment.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ClanConstructor(clan, playerList, playerRank);
	}

	public boolean removePlayerClan(UUID uuid) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("DELETE FROM znx_clan_player_data WHERE uuid='" + uuid + "'");
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception ex) {
			System.out.print("");
		}
		return true;
	}

	public boolean checkPlayer(UUID uuid) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.CHECK_PLAYER_CLAN.toString());
			preparedStatement.setString(1, uuid.toString());

			ResultSet resultSet = preparedStatement.executeQuery();

			boolean user = resultSet.next();

			resultSet.close();
			resultSet.close();

			return user;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean checkClan(String string) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.CHECK_CLAN.toString());
			preparedStatement.setString(1, string);

			ResultSet resultSet = preparedStatement.executeQuery();

			boolean user = resultSet.next();

			resultSet.close();
			resultSet.close();

			return user;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean checkTag(String string) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.CHECK_TAG.toString());
			preparedStatement.setString(1, string);

			ResultSet resultSet = preparedStatement.executeQuery();

			boolean user = resultSet.next();

			resultSet.close();
			resultSet.close();

			return user;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean joinClan(Clan clan, UUID uuid, ClanHierarchy ch) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.ADD_PLAYER_CLAN.toString());
			preparedStatement.setString(1, clan.getName().toUpperCase());
			preparedStatement.setString(2, uuid.toString());
			preparedStatement.setString(3, ch.toString());
			preparedStatement.executeUpdate();
			preparedStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Created the clan " + clan + ".");

			return true;
		} catch (Exception ex) {
			getLogger().error("Error when the plugin tried to create clan.");
		}
		return false;
	}

	public String getClan(UUID uuid) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.GET_CLAN_PLAYER.toString());

			preparedStatement.setString(1, uuid.toString());

			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			String name = resultSet.getString("name");
			resultSet.close();
			preparedStatement.close();

			return name;

		} catch (Exception ex) {
			getLogger().error("Error when the plugin tried to create clan.");
		}
		return null;
	}

	public void addClan(String clan, String variable, int win) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("UPDATE znx_clan_data SET " + variable + "= ? WHERE name= ?");
			preparedStatement.setInt(1, getClan(clan, variable) + win);
			preparedStatement.setString(2, clan);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setClanImage(String clan, String image) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("UPDATE znx_clan_data SET imglink=? WHERE name= ?");
			preparedStatement.setString(1, image);
			preparedStatement.setString(2, clan);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void removeClan(String clan, String variable, int win) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("UPDATE znx_clan_data SET " + variable + "= ? WHERE name= ?");
			preparedStatement.setInt(1, ((getClan(clan, variable) - win) > 0 ? getClan(clan, variable) - win : 0));
			preparedStatement.setString(2, clan);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getClan(String clan, String variable) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("SELECT * FROM znx_clan_data WHERE name= ?");
			preparedStatement.setString(1, clan);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();

			int var = 0;

			switch (variable) {
			case "kills":
				var = resultSet.getInt("kills");
				break;
			case "wins":
				var = resultSet.getInt("wins");
				break;
			case "xp":
				var = resultSet.getInt("xp");
				break;
			case "deaths":
				var = resultSet.getInt("deaths");
				break;
			case "elo":
				var = resultSet.getInt("elo");
				break;
			case "tag":
				var = resultSet.getInt("tag");
				break;
			}

			resultSet.close();
			preparedStatement.close();
			return var;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public String getClanTag(String clanName) {
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("SELECT * FROM znx_clan_data WHERE name= ?");
			preparedStatement.setString(1, clanName);

			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();

			String var = resultSet.getString("tag");

			resultSet.close();
			preparedStatement.close();
			return var;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public String getClanGroup(UUID uuid) {
		try {
			PreparedStatement ps = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement("SELECT * FROM znx_clan_player_data WHERE UUID= ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			String Level = rs.getString("stat");
			rs.close();
			ps.close();
			return Level.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ClanHierarchy.MEMBER.toString();
	}

	public int getClanPlayerNumber(String clan) {
		int i = 0;

		try {

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(ClanQueries.GET_CLAN_PLAYER_NAME.toString());
			insertStatment.setString(1, clan);
			ResultSet resultSet = insertStatment.executeQuery();

			while (resultSet.next()) {
				i++;
			}

			resultSet.close();
			insertStatment.close();

		} catch (Exception e) {
		}

		return i;
	}

	public ClanHierarchy getClanHiearchy(int id) {
		for (ClanHierarchy clanHierarchy : ClanHierarchy.values())
			if (clanHierarchy.getId() == id)
				return clanHierarchy;
		return null;
	}

	public ClanHierarchy getClanHiearchy(String name) {
		for (ClanHierarchy clanHierarchy : ClanHierarchy.values())
			if (clanHierarchy.getName().equalsIgnoreCase(name))
				return clanHierarchy;
		return null;
	}

	public Clan getClan(int id) {
		for (Clan clan : clans.values())
			if (clan.getId() == id)
				return clan;
		return null;
	}

	public Clan getClan(String name) {
		for (Clan clan : clans.values())
			if (clan.getName().equalsIgnoreCase(name))
				return clan;
		return null;
	}

}
