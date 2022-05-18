package br.com.zenix.core.spigot.player.clan;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.clan.groups.ClanHierarchy;
import br.com.zenix.core.spigot.player.clan.player.ClanAccount;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class Clan {

	private final HashMap<Integer, ClanHierarchy> players;

	private final String name;
	private final String tag;
	private final int id;

	public Clan(String name, String tag, int id) {
		this.name = name;
		this.tag = tag;
		this.id = id;
		this.players = new HashMap<>();
	}

	public void addPlayers(int id, ClanHierarchy clan) {
		players.put(id, clan);
	}

	public String getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}

	public HashMap<Integer, ClanHierarchy> getPlayers() {
		return players;
	}

	public void delete(Player player, String clan) {
		CoreManager core = Core.getCoreManager();

		if (core.getClanAccountManager().getClanAccount(player).getClanHierarchy() != ClanHierarchy.OWNER) {
			player.sendMessage("§cVocê não tem poder para realizar essa ação.");
			return;
		}

		if (!core.getClanAccountManager().getClanAccount(player).getClan().getName().equalsIgnoreCase(clan))
			return;

		core.getClanManager().removePlayerClan(player.getUniqueId());
		core.getClanManager().deleteClan(clan);

		Account account = core.getAccountManager().getAccount(player);
		account.setHaveClan(false);
		account.setClanChat(false);

		for (Player players : Bukkit.getOnlinePlayers()) {
			Account accounts = core.getAccountManager().getAccount(players);

			if (account.isHaveClan())
				if (core.getClanAccountManager().getClanAccount(players).getClan().getName()
						.equalsIgnoreCase(core.getClanAccountManager().getClanAccount(player).getClan().getName())) {
					accounts.setHaveClan(false);
					accounts.setClanChat(false);
					core.getClanAccountManager().removeAccount(players.getUniqueId());
				}
		}

		core.getClanAccountManager().removeAccount(player.getUniqueId());

		player.sendMessage("§cClan deletada com total sucesso.");
		return;
	}

	public void leave(Player player) {
		CoreManager core = Core.getCoreManager();

		if (core.getClanAccountManager().getClanAccount(player).getClanHierarchy() == ClanHierarchy.OWNER) {
			player.sendMessage("§cVocê não pode fazer isso. Você pode apenas deletar sua clan.");
			return;
		}

		core.getClanManager().removePlayerClan(player.getUniqueId());

		Account account = core.getAccountManager().getAccount(player);
		account.setHaveClan(false);

		core.getClanAccountManager().removeAccount(player.getUniqueId());

		player.sendMessage("§cVocê saiu do seu clã!");
	}

	public void invite(Player player, Player invited) {
		CoreManager core = Core.getCoreManager();

		String clanName = core.getClanAccountManager().getClanAccount(player).getClan().getName();

		Account a2 = core.getAccountManager().getAccount(invited);

		if (a2.isHaveClan()) {
			player.sendMessage("§c" + invited.getName() + " já está em um clan.");
			return;
		}

		if (core.getClanManager().getClanPlayerNumber(clanName) > 10) {
			player.sendMessage("§cLimite de jogadores excedido.");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(player).getClanHierarchy() == ClanHierarchy.MEMBER) {
			player.sendMessage("§cVocê não tem poder para convidar alguem.");
			return;
		}

		if ((core.getClanManager().getInvites().containsKey(invited.getUniqueId()))
				&& (((String) core.getClanManager().getInvites().get(invited.getUniqueId()))
						.equals(player.getName()))) {
			player.sendMessage(ChatColor.RED + invited.getName() + " já foi convidado para esse clan.");
			return;
		}

		core.getClanManager().getInvites().put(invited.getUniqueId(), clanName);

		player.sendMessage(
				"§e" + invited.getName() + " (" + invited.getUniqueId() + ") foi convidado para a sua clan!");
		invited.sendMessage("§7Você foi convidado para o clan §c" + clanName + "§7" + " por " + player.getName() + "!");
		invited.sendMessage("§7Use /clan join §c" + clanName + "§7" + " para entrar.");

		invited.playSound(invited.getLocation(), Sound.NOTE_PLING, 10.0F, 10.0F);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, 10.0F);
	}

	public void join(Player player, String clanName) {
		CoreManager core = Core.getCoreManager();

		Account account = core.getAccountManager().getAccount(player);

		if (account.isHaveClan()) {
			player.sendMessage("§cVocê já está em um clã!");
			return;
		}

		if (!core.getClanManager().getInvites().containsKey(player.getUniqueId())) {
			player.sendMessage("§cVocê não tem nenhum convite para entrar!");
			return;
		}

		if (getInvite(player.getUniqueId(), clanName)) {
			core.getClanManager().getInvites().remove(player.getUniqueId());
			core.getClanManager().joinClan(new Clan(clanName, core.getClanManager().getClanTag(clanName), 1),
					player.getUniqueId(), ClanHierarchy.MEMBER);
			core.getClanAccountManager().addClanAccount(new ClanAccount(player));
			account.setHaveClan(true);
			account.setClanChat(false);

			account.updatePlayer(player);

			account.getDataHandler().getValue(DataType.CLAN_DEATH).setValue(0);
			account.getDataHandler().getValue(DataType.CLAN_ELO).setValue(0);
			account.getDataHandler().getValue(DataType.CLAN_KILL).setValue(0);
			account.getDataHandler().getValue(DataType.CLAN_WINS).setValue(0);
			account.getDataHandler().getValue(DataType.CLAN_XP).setValue(0);
			account.getDataHandler().update(DataType.CLAN_KILL);
			account.getDataHandler().update(DataType.CLAN_ELO);
			account.getDataHandler().update(DataType.CLAN_WINS);
			account.getDataHandler().update(DataType.CLAN_XP);
			account.getDataHandler().update(DataType.CLAN_DEATH);

			player.sendMessage("§7Você agora entrou para o clan §c" + clanName + "§7"
					+ "! Seja bem vindo á um novo reino de batalhas!");
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, 10.0F);
			return;
		}

		player.sendMessage("§cNão há nenhum convite registrado.");
	}

	public void promote(Player player, Player promoted) {
		CoreManager core = Core.getCoreManager();

		Account account = core.getAccountManager().getAccount(player);
		Account accountPromoted = core.getAccountManager().getAccount(promoted);

		if (!account.isHaveClan()) {
			player.sendMessage("§cVocê não está em nenhum clan!");
			return;
		}

		if (!accountPromoted.isHaveClan()) {
			player.sendMessage("§cVocê não está em nenhum clan!");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(player).getClanHierarchy() == ClanHierarchy.MEMBER) {
			player.sendMessage("§cVocê não tem poder para promover alguem.");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(promoted).getClanHierarchy().getId() < 3) {
			player.sendMessage("§cVocê não tem permissão para promover!");
			return;
		}

		try {
			PreparedStatement localPreparedStatement = core.getDataManager().getMySQL().getConnection()
					.prepareStatement("UPDATE `znx_clan_player_data` set `stat`='" + ClanHierarchy.ADMIN.toString()
							+ "' WHERE `uuid`='" + promoted.getUniqueId().toString() + "';");
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		} catch (Exception ex) {
			System.out.print("");
		}

		player.sendMessage("§e" + promoted.getName() + " (" + promoted.getUniqueId()
				+ ") foi promovido para ADMINISTRADOR da sua Clan!");
	}

	public void unPromote(Player player, Player promoted) {
		CoreManager core = Core.getCoreManager();

		Account account = core.getAccountManager().getAccount(player);
		Account accountPromoted = core.getAccountManager().getAccount(promoted);

		if (!account.isHaveClan()) {
			player.sendMessage("§cVocê não está em nenhum clan!");
			return;
		}

		if (!accountPromoted.isHaveClan()) {
			player.sendMessage("§cVocê não está em nenhum clan!");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(player).getClanHierarchy() == ClanHierarchy.MEMBER) {
			player.sendMessage("§cApenas admins e o donos do clan podem despromover players.");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(promoted).getClanHierarchy() == ClanHierarchy.OWNER) {
			player.sendMessage("§cVocê não pode promover ele!");
			return;
		}

		try {
			PreparedStatement localPreparedStatement = core.getDataManager().getMySQL().getConnection()
					.prepareStatement("UPDATE `znx_clan_player_data` set `stat`='" + ClanHierarchy.MEMBER.toString()
							+ "' WHERE `uuid`='" + promoted.getUniqueId().toString() + "';");
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		} catch (Exception ex) {
			System.out.print("");
		}

		player.sendMessage(
				"§e" + promoted.getName() + "(" + promoted.getUniqueId() + ") foi rebaixado para PLAYER da sua Clan!");
	}

	public void kick(Player player, Player kicked) {
		CoreManager core = Core.getCoreManager();

		Account account = core.getAccountManager().getAccount(player);
		Account accountKicked = core.getAccountManager().getAccount(kicked);

		if (!account.isHaveClan()) {
			player.sendMessage("§cVocê não está em nenhum clan!");
			return;
		}

		if (!accountKicked.isHaveClan()) {
			player.sendMessage("§cO player não está em nenhuma clan!");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(player).getClanHierarchy() == ClanHierarchy.MEMBER) {
			player.sendMessage("§cApenas admins e o donos do clan podem kickar players!");
			return;
		}

		if (core.getClanAccountManager().getClanAccount(kicked).getClanHierarchy() == ClanHierarchy.OWNER) {
			player.sendMessage("§cVocê não pode kickar ele!");
			return;
		}

		if (!core.getClanAccountManager().getClanAccount(kicked).getClan().getName()
				.equalsIgnoreCase(core.getClanAccountManager().getClanAccount(player).getClan().getName())) {
			return;
		}

		core.getClanManager().removePlayerClan(kicked.getUniqueId());
		core.getClanAccountManager().removeAccount(kicked.getUniqueId());
		accountKicked.setHaveClan(false);
		accountKicked.setClanChat(false);

		player.sendMessage("§e" + kicked.getName() + "(" + kicked.getUniqueId() + ") foi kickado da sua Clan!");
	}

	public boolean getInvite(UUID uuid, String string) {
		CoreManager core = Core.getCoreManager();

		if (!core.getClanManager().getInvites().containsKey(uuid)) {
			return false;
		}

		return ((String) core.getClanManager().getInvites().get(uuid)).toString().equalsIgnoreCase(string.toString());
	}

	public void give(Player player) {
		CoreManager core = Core.getCoreManager();

		int finalValue = 2;
		finalValue += Math.abs(new Random().nextInt(5));

		int elo = finalValue * 2;

		core.getClanManager().addClan(this.getName(), "xp", finalValue);
		core.getClanManager().addClan(this.getName(), "elo", elo);
		core.getClanManager().addClan(this.getName(), "kills", 1);

		player.sendMessage("§3§lCLAN §fGanhos para o clã: §3" + this.getName());
		player.sendMessage("  §b+" + finalValue + " §fXP");
		player.sendMessage("  §b+" + elo + " §fELO");
		player.sendMessage("  §b+" + 1 + " §fKILLS");

		Account account = core.getAccountManager().getAccount(player);
		DataHandler data = account.getDataHandler();

		data.getValue(DataType.CLAN_KILL).setValue(data.getValue(DataType.CLAN_KILL).getValue() + 1);
		data.getValue(DataType.CLAN_ELO).setValue(data.getValue(DataType.CLAN_ELO).getValue() + elo);
		data.getValue(DataType.CLAN_XP).setValue(data.getValue(DataType.CLAN_XP).getValue() + finalValue);

		data.update(DataType.CLAN_KILL);
		data.update(DataType.CLAN_ELO);
		data.update(DataType.CLAN_XP);
	}

	public void giveLost(Player player) {
		CoreManager core = Core.getCoreManager();

		int finalValue = 2;
		finalValue += Math.abs(new Random().nextInt(5));

		int elo = finalValue * 2;

		core.getClanManager().removeClan(this.getName(), "xp", finalValue);
		core.getClanManager().removeClan(this.getName(), "elo", elo);
		core.getClanManager().addClan(this.getName(), "deaths", 1);

		player.sendMessage("§3§lCLAN §fPerdas para o clã: §3" + this.getName());
		player.sendMessage("  §b-" + finalValue + " §fXP");
		player.sendMessage("  §b-" + elo + " §fELO");
		player.sendMessage("  §b+" + 1 + " §fDEATH");

		Account account = core.getAccountManager().getAccount(player);
		DataHandler data = account.getDataHandler();

		data.getValue(DataType.CLAN_DEATH).setValue(data.getValue(DataType.CLAN_DEATH).getValue() + 1);
		data.getValue(DataType.CLAN_ELO).setValue(data.getValue(DataType.CLAN_ELO).getValue() - elo);
		data.getValue(DataType.CLAN_XP).setValue(data.getValue(DataType.CLAN_XP).getValue() - finalValue);

		data.update(DataType.CLAN_DEATH);
		data.update(DataType.CLAN_ELO);
		data.update(DataType.CLAN_XP);
	}

	public void giveWin(Player player) {
		CoreManager core = Core.getCoreManager();

		int finalValue = 2;
		finalValue += Math.abs(new Random().nextInt(5));

		int elo = finalValue * 2;

		core.getClanManager().addClan(this.getName(), "xp", finalValue);
		core.getClanManager().addClan(this.getName(), "elo", elo);
		core.getClanManager().addClan(this.getName(), "wins", 1);

		player.sendMessage("§3§lCLAN §fGanhos para o clã: §3" + this.getName());
		player.sendMessage("  §b+" + finalValue + " §fXP");
		player.sendMessage("  §b+" + elo + " §fELO");
		player.sendMessage("  §b+" + 1 + " §fWINS");

		Account account = core.getAccountManager().getAccount(player);
		DataHandler data = account.getDataHandler();

		data.getValue(DataType.CLAN_WINS).setValue(data.getValue(DataType.CLAN_WINS).getValue() + 1);
		data.getValue(DataType.CLAN_ELO).setValue(data.getValue(DataType.CLAN_ELO).getValue() + elo);
		data.getValue(DataType.CLAN_XP).setValue(data.getValue(DataType.CLAN_XP).getValue() + finalValue);

		data.update(DataType.CLAN_WINS);
		data.update(DataType.CLAN_ELO);
		data.update(DataType.CLAN_XP);
	}

	public int getId() {
		return id;
	}

}
