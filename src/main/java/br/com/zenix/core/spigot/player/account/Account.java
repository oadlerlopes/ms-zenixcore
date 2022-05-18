package br.com.zenix.core.spigot.player.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.lang.Lang;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.player.account.AccountManager.MacroStatus;
import br.com.zenix.core.spigot.player.clan.player.ClanAccount;
import br.com.zenix.core.spigot.player.cup.CupGroupType;
import br.com.zenix.core.spigot.player.league.type.LeagueType;
import br.com.zenix.core.spigot.player.permissions.Permissible;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.permissions.type.RankType;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;
import br.com.zenix.core.spigot.player.scoreboard.ScoreboardConstructor;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class Account {

	private static final Executor loadAsyncExecutor = Executors
			.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Load Async Thread").build());
	private static final Executor saveAsyncExecutor = Executors
			.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Save Async Thread").build());

	public PermissionAttachment permissionAttachment;

	private ScoreboardConstructor scoreboardHandler;

	private final HashMap<String, Boolean> permissions = new HashMap<>();
	private final HashMap<Integer, PunishRecord> punishRecords = new HashMap<>();
	private final HashMap<Long, Rank> rankSecondary = new HashMap<>();

	private final DataHandler dataHandler = new DataHandler(this);

	private Player player;

	private String nickname, lastIp, ip;

	private UUID uniqueId;

	private long rankTime;
	private int xp, doublexp, doublecoins, triplecoins;

	private boolean autoBan;
	private int alerts, time;

	private MacroStatus macroStatus;

	private long doubleStart;
	private long doubleEnd;

	private long doubleCoinsStart;
	private long doubleCoinsEnd;
	private long tripleCoinsStart;
	private long tripleCoinsEnd;

	private Player tellLast;

	private boolean doubleRunning;
	private boolean doubleCoinsRunning;
	private boolean tripleCoinsRunning;
	private boolean participatingCup;

	private boolean haveClan = false;
	private boolean clanChat = false;

	private CupGroupType cupGroupType;
	private Rank rank;

	private int coins, id, rankId;
	private LeagueType leagueType;

	private boolean loaded, tell, warnings, scoreboard;

	public Account(UUID uuid) {
		this.uniqueId = uuid;
	}

	public void load(Callback<Boolean> callback) {
		this.id = 1;
		this.loaded = false;
		this.nickname = "";
		this.doubleRunning = false;

		long start = System.currentTimeMillis();
		try {
			Connection mainConnection = getCoreManager().getDataManager().getMySQL().getConnection();

			PreparedStatement accountQuery = mainConnection.prepareStatement(CoreQueries.ACCOUNT_QUERY.toString());

			accountQuery.setString(1, uniqueId.toString());

			ResultSet accountData = accountQuery.executeQuery();

			if (accountData.next()) {
				getLogger().log("[" + (System.currentTimeMillis() - start) + "ms] Getting the status of the player "
						+ uniqueId + "!");

				this.id = accountData.getInt(1);
				this.nickname = accountData.getString(3);
				this.rankId = accountData.getInt(4);
				this.rankTime = accountData.getLong(5) * 1000;
				this.lastIp = accountData.getString(8);

				PreparedStatement statusXpQuery = mainConnection
						.prepareStatement(CoreQueries.ACCOUNT_XP_STATUS_QUERY.toString());

				statusXpQuery.setInt(1, id);

				ResultSet statusXp = statusXpQuery.executeQuery();
				if (statusXp.next()) {
					this.doublexp = statusXp.getInt(2);
					this.doubleStart = statusXp.getLong(3);
					this.doubleEnd = statusXp.getLong(4);
					if (statusXp.getLong(5) == 1) {
						this.doubleRunning = true;
					} else {
						this.doubleRunning = false;
					}
				}

				statusXp.close();
				statusXpQuery.close();

				accountData.close();
				accountQuery.close();

				PreparedStatement rankQuery = mainConnection
						.prepareStatement(CoreQueries.ACCOUNT_SECONDARY_RANK_QUERY.toString());

				rankQuery.setInt(1, getId());

				ResultSet rankData = rankQuery.executeQuery();

				while (rankData.next()) {
					// getLogger().log("[" + (System.currentTimeMillis() - start) + "ms] Getting the
					// group secondary "
					// + getId() + "!");

					rankSecondary.put((rankData.getLong(4) * 1000),
							getCoreManager().getPermissionManager().getRank(rankData.getInt(3)));

					// getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] Rank
					// getted of " + nickname
					// + ": " +
					// getCoreManager().getPermissionManager().getRank(rankData.getInt(3)));
				}

				rankQuery.close();

				for (PunishRecord punish : getCoreManager().getPunishManager().getPlayerPunishRecords(id)) {
					punishRecords.put(punish.getId(), punish);
				}

			} else {
				this.rankId = RankType.MEMBRO.getId();
				this.rankTime = -1;
				this.coins = 0;
				this.xp = 0;
				this.rank = getCoreManager().getPermissionManager().getDefaultRank();

				PreparedStatement accountInsert = mainConnection
						.prepareStatement(CoreQueries.ACCOUNT_INSERT.toString());
				accountInsert.setString(1, uniqueId.toString());
				accountInsert.setString(2, nickname);
				accountInsert.setInt(3, 18);
				accountInsert.setInt(4, -1);
				accountInsert.setLong(5, System.currentTimeMillis() / 1000);
				accountInsert.setLong(6, System.currentTimeMillis() / 1000);
				accountInsert.execute();
				accountInsert.close();

				PreparedStatement idQuery = mainConnection.prepareStatement(CoreQueries.ACCOUNT_QUERY.toString());
				idQuery.setString(1, uniqueId.toString());
				ResultSet idData = accountQuery.executeQuery();

				if (idData.next()) {
					this.id = idData.getInt(1);
				}

				idData.close();
				idQuery.close();

				PreparedStatement statusXp = mainConnection
						.prepareStatement(CoreQueries.ACCOUNT_XP_STATUS_INSERT.toString());
				statusXp.setInt(1, id);
				statusXp.execute();
				statusXp.close();
			}

			this.rank = getCoreManager().getPermissionManager().getRank(rankId);

			if (rankTime > 10000 && new Date().after(new Date(rankTime)))
				updateDefaultRank();

			if (this.rank == null)
				updateDefaultRank();

			if (!dataHandler.load())
				callback.finish(false);

			this.xp = dataHandler.getValue(DataType.GLOBAL_XP).getValue();
			this.coins = dataHandler.getValue(DataType.GLOBAL_COINS).getValue();

			this.cupGroupType = CupGroupType.getCupGroupType(dataHandler.getValue(DataType.CUP_GROUP).getValue());

			if (this.cupGroupType != CupGroupType.NONE) {
				participatingCup = true;
				this.participatingCup = true;
			}

			this.scoreboardHandler = null;

			loaded = true;
			tell = true;
			warnings = true;
			scoreboard = false;
			this.leagueType = LeagueType.getRanked(getXp());

			this.alerts = 0;
			this.time = 0;
			this.autoBan = false;
			alerts = 0;
			time = 0;

			this.macroStatus = new MacroStatus(this);
			this.autoBan = false;
			this.alerts = 0;
			this.time = 0;

			if (getLeaguePrefix() == null || leagueType == null) {
				leagueType = LeagueType.UNRANKED;
			}

			setDoubleCoins(getDataHandler().getValue(DataType.DOUBLECOINS_VALUE).getValue());
			setTripleCoins(getDataHandler().getValue(DataType.TRIPLECOINS_VALUE).getValue());

			if (dataHandler.getValue(DataType.DOUBLECOINS_ACTIVE).getValue() == 1) {
				setDoubleCoinsRunning(true);
				setDoubleCoinsEnd(getDataHandler().getValue(DataType.DOUBLECOINS_END).getValue());
				setDoubleCoinsStart(getDataHandler().getValue(DataType.DOUBLECOINS_START).getValue());
			}

			if (dataHandler.getValue(DataType.TRIPLECOINS_ACTIVE).getValue() == 1) {
				setTripleCoinsRunning(true);
				setTripleCoinsEnd(getDataHandler().getValue(DataType.TRIPLECOINS_END).getValue());
				setTripleCoinsStart(getDataHandler().getValue(DataType.TRIPLECOINS_START).getValue());
			}

			getLogger().log("[" + (System.currentTimeMillis() - start) + "ms] The player " + uniqueId
					+ " have your account loaded with success!");

			callback.finish(true);
		} catch (Exception exception) {
			exception.printStackTrace();
			getLogger().error("[" + (System.currentTimeMillis() - start)
					+ "ms] Error when the plugin tried to load the data of player with the uuid: " + uniqueId + ".",
					exception);
			callback.finish(false);
		}
	}

	public String getMessage(String key) {
		return getCoreManager().getLangManager().getMessage(key,
				Lang.getLang(getDataHandler().getValue(DataType.LANG).getValue()));
	}

	public void setAutoban(boolean autoban) {
		this.autoBan = autoban;
	}

	public boolean isAutoban() {
		return autoBan;
	}

	public int getDoubleCoins() {
		return doublecoins;
	}

	public int getTripleCoins() {
		return triplecoins;
	}

	public boolean isParticipatingCup() {
		return participatingCup;
	}

	public CupGroupType getCupGroupType() {
		return cupGroupType;
	}

	public void setDoubleCoins(int doublecoins) {
		this.doublecoins = doublecoins;
	}

	public void setTripleCoins(int triplecoins) {
		this.triplecoins = triplecoins;
	}

	public boolean isDoubleCoinsRunning() {
		return doubleCoinsRunning;
	}

	public boolean isTripleCoinsRunning() {
		return tripleCoinsRunning;
	}

	public void setTripleCoinsRunning(boolean tripleCoinsRunning) {
		this.tripleCoinsRunning = tripleCoinsRunning;
	}

	public void setDoubleCoinsRunning(boolean doubleCoinsRunning) {
		this.doubleCoinsRunning = doubleCoinsRunning;
	}

	public HashMap<Long, Rank> getRankSecondary() {
		return rankSecondary;
	}

	public void update() {
		if (!loaded)
			return;

		long start = System.currentTimeMillis();
		saveAsyncExecutor.execute(() -> {
			try {
				PreparedStatement statement = getCoreManager().getDataManager().getMySQL().getSlaveConnection()
						.prepareStatement(CoreQueries.ACCOUNT_UPDATE.toString());

				if (player != null && !getCoreManager().getSkinManager().usingFake(uniqueId)) {
					statement.setString(1, player.getName());
				} else {
					statement.setString(1, nickname);
				}

				statement.setString(2, getIp());
				statement.setInt(3, rankId);
				statement.setLong(4, rankTime / 1000);
				statement.setLong(5, System.currentTimeMillis() / 1000);
				statement.setInt(6, id);
				statement.execute();

				PreparedStatement statementXp = getCoreManager().getDataManager().getMySQL().getSlaveConnection()
						.prepareStatement(CoreQueries.ACCOUNT_XP_STATUS_UPDATE.toString());
				statementXp.setDouble(1, doublexp);
				statementXp.setLong(2, doubleStart);
				statementXp.setLong(3, doubleEnd);
				if (this.doubleRunning == true) {
					statementXp.setInt(4, 1);
				} else {
					statementXp.setInt(4, 0);
				}
				statementXp.setInt(5, id);
				statementXp.execute();

				getLogger().debug(
						"[" + (System.currentTimeMillis() - start) + "ms] The player " + nickname + " was updated.");
			} catch (Exception exception) {
				getLogger().error("[" + (System.currentTimeMillis() - start)
						+ "ms] Error when the plugin tried to save the data of player with the id: " + uniqueId + "("
						+ id + ").", exception);
			}
		});

	}

	public void updatePermissions() {
		for (Permissible string : getCoreManager().getPermissionManager().getPlayerPermissions(getId())) {
			permissionAttachment.setPermission(string.getName(), true);
		}

		getCoreManager().getPermissionManager().updateAttachment(player, rank,
				permissionAttachment = player.addAttachment(getCoreManager().getPlugin()));
	}

	public void setDoubleRunning(boolean doubleRunning) {
		this.doubleRunning = doubleRunning;
	}

	public void setDoublexp(int doublexp) {
		this.doublexp = doublexp;
	}

	public boolean isDoubleRunning() {
		return doubleRunning;
	}

	public Player getTellLast() {
		return tellLast;
	}

	public void setTellLast(Player tellLast) {
		this.tellLast = tellLast;
	}

	private void updateDefaultRank() {
		this.rank = getCoreManager().getPermissionManager().getDefaultRank();

		getCoreManager().getPermissionManager().giveRankPlayer(id, rank, -1);
	}

	// private void removeSecondaryRank(Rank rank) {
	// rankSecondary.remove(rank.getTime());
	//
	// getCoreManager().getPermissionManager().removeSecoundaryRank(id, rank);
	// }

	public void updatePlayer(Player player) {
		this.player = player;

		if (getCoreManager().getClanManager().checkPlayer(player.getUniqueId())) {
			haveClan = true;

			getCoreManager().getClanAccountManager().addClanAccount(new ClanAccount(player));
		}

		if (!getCoreManager().getSkinManager().usingFake(uniqueId)) {
			this.nickname = player.getName();
		}

		if (rank == null) {
			this.rank = getCoreManager().getPermissionManager().getRank(rankId);
		}

		if (this.isParticipatingCup()) {
			PermissionAttachment attachment = player.addAttachment(getCoreManager().getPlugin());
			attachment.setPermission("hgkit.*", true);
			attachment.setPermission("pvp.kit.*", true);

			for (Entry<String, Boolean> s : getCoreManager().getPermissionManager().getRank(632).getPermissions()
					.entrySet()) {
				if (!s.getKey().contains("commons.tag"))
					attachment.setPermission(s.getKey(), s.getValue());
			}

			attachment.setPermission("commons.tag.copa", true);
		}

		getCoreManager().getPermissionManager().updateAttachment(player, rank,
				permissionAttachment = player.addAttachment(getCoreManager().getPlugin()));

		scoreboardHandler = null;
		ip = "0.0.0.0";

	}

	public void updatePlayer(String player) {
		this.nickname = player;
		scoreboardHandler = null;
	}

	public void unload() {
		getCoreManager().getAccountManager().unloadAccount(uniqueId);
	}

	public boolean canJoin() {
		return getLastActiveBan() == null;
	}

	public long getDoubleEnd() {
		return doubleEnd;
	}

	public long getDoubleStart() {
		return doubleStart;
	}

	public void setDoubleEnd(long doubleEnd) {
		this.doubleEnd = doubleEnd;
	}

	public void setDoubleStart(long doubleStart) {
		this.doubleStart = doubleStart;
	}

	public long getDoubleCoinsEnd() {
		return doubleCoinsEnd;
	}

	public long getDoubleCoinsStart() {
		return doubleCoinsStart;
	}

	public boolean isHaveClan() {
		return haveClan;
	}

	public void setDoubleCoinsEnd(long doubleCoinsEnd) {
		this.doubleCoinsEnd = doubleCoinsEnd;
	}

	public void setDoubleCoinsStart(long doubleCoinsStart) {
		this.doubleCoinsStart = doubleCoinsStart;
	}

	public void setTripleCoinsEnd(long tripleCoinsEnd) {
		this.tripleCoinsEnd = tripleCoinsEnd;
	}

	public void setTripleCoinsStart(long tripleCoinsStart) {
		this.tripleCoinsStart = tripleCoinsStart;
	}

	public long getTripleCoinsEnd() {
		return tripleCoinsEnd;
	}

	public long getTripleCoinsStart() {
		return tripleCoinsStart;
	}

	public int getDoubleXP() {
		return doublexp;
	}

	public void cleanUp() {
		player = null;
		lastIp = null;
		rank = null;
		permissionAttachment = null;
		loaded = false;
		punishRecords.clear();
		scoreboardHandler = null;
		permissions.clear();
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public int getId() {
		return id;
	}

	public LeagueType getLeague() {
		return leagueType;
	}

	public String getNickname() {
		return nickname;
	}

	public ScoreboardConstructor getScoreboardHandler() {
		return scoreboardHandler;
	}

	public long getRankTime() {
		return rankTime;
	}

	public String getIp() {
		return ip == null ? "" : ip;
	}

	public HashMap<Integer, PunishRecord> getPunishRecords() {
		return punishRecords;
	}

	public PunishRecord getLastActiveBan() {
		for (PunishRecord punish : getPunishRecords().values()) {
			if (punish.getType().equals(PunishType.BAN) || punish.getType().equals(PunishType.TEMPBAN))
				if (punish.isActive())
					return punish;
		}
		return null;
	}

	public String getLeaguePrefix() {
		String result = "§7(" + leagueType.getColor() + leagueType.getSymbol() + "§7)";
		return result;
	}

	public boolean isTell() {
		return tell;
	}

	public Rank getRank() {
		return rank;
	}

	public int getCoins() {
		return coins;
	}

	public int getXp() {
		return xp;
	}

	public String getLastIP() {
		return lastIp;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isWarnings() {
		return warnings;
	}

	public boolean isClanChat() {
		return clanChat;
	}

	public boolean isScoreboard() {
		return scoreboard;
	}

	public PermissionAttachment getPermissionAttachment() {
		return permissionAttachment;
	}

	public DataHandler getDataHandler() {
		return dataHandler;
	}

	public Executor getSaveAsyncExecutor() {
		return saveAsyncExecutor;
	}

	public void setHaveClan(boolean haveClan) {
		this.haveClan = haveClan;
	}

	public Executor getLoadAsyncExecutor() {
		return loadAsyncExecutor;
	}

	public Logger getLogger() {
		return getCoreManager().getAccountManager().getLogger();
	}

	public HashMap<String, Boolean> getPermissions() {
		return permissions;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public CoreManager getCoreManager() {
		return Core.getCoreManager();
	}

	public void setScoreboard(boolean scoreboard) {
		this.scoreboard = scoreboard;
	}

	public void setClanChat(boolean clanChat) {
		this.clanChat = clanChat;
	}

	public void setLeague(LeagueType leagueType) {
		this.leagueType = leagueType;
	}

	public void setScoreboardHandler(ScoreboardConstructor scoreboardHandler) {
		this.scoreboardHandler = scoreboardHandler;
	}

	public void setRank(Rank rank, long time) {
		this.rank = rank;
		this.rankId = rank.getId();
		this.rankTime = time;

		if (this.player != null) {

			getCoreManager().getTagManager().updateTagCommand(this.player);

			this.player.sendMessage(" ");
			this.player.sendMessage("§6§lRANKS §fO seu rank principal foi alterado para " + rank.getName().toUpperCase()
					+ "§f que expira em "
					+ (getRankTime() > 10000 ? getCoreManager().getUtils().compareTime(getRankTime()) : "NUNCA"));

			this.player.sendMessage(" ");
			updatePlayer(this.player);

			getCoreManager().getPermissionManager().updateAttachment(this.player, rank,
					permissionAttachment = this.player.addAttachment(getCoreManager().getPlugin()));

			getCoreManager().getTagManager().updateTagCommand(this.player);
			getCoreManager().getTagManager().updateTag(this.player);

		}
	}

	public int getAlerts() {
		return alerts;
	}

	public MacroStatus getMacroStatus() {
		return macroStatus;
	}

	public int getTime() {
		return time;
	}

	public boolean hasAutoBan() {
		return autoBan;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setAlerts(int alerts) {
		this.alerts = alerts;
	}

	public void setAutoBan(boolean autoBan) {
		this.autoBan = autoBan;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public void setXp(int d) {
		this.xp = (d < 0) ? 0 : d;
	}

	public void sendMessage(String string) {
		getPlayer().sendMessage(string);
	}

	public void setTell(boolean tell) {
		this.tell = tell;
	}

	public void setWarnings(boolean warnings) {
		this.warnings = warnings;
	}

}
