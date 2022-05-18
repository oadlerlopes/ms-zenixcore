package br.com.zenix.core.spigot.commands.player;

import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class AccountCommand extends BukkitCommand {

	public AccountCommand() {
		super("account", "", "acc", "conta");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}
		if (args.length == 0) {

			Player player = (Player) commandSender;

			Account account = getCoreManager().getAccountManager().getAccount(player);
			if (account == null) {
				commandSender.sendMessage("§cUsuário inexistente.");
				return false;
			}

			buildMessage(player, account, account.getNickname());

		} else if (args.length == 1) {
			UUID uuid = (isUUID(args[0]) ? UUID.fromString(args[0])
					: getCoreManager().getNameFetcher().getUUID(args[0]));

			if (uuid == null) {
				commandSender.sendMessage("§cUsuário inexistente.");
				return true;
			}

			Account account = new Account(uuid);

			Player player = (Player) commandSender;

			if (!account.isLoaded()) {
				account.load(new Callback<Boolean>() {
					public void finish(Boolean bool) {
						if (bool)
							getCoreManager().getAccountManager().getAccounts().put(uuid, account);
						else {
							try {
								Thread.sleep(500L);
							} catch (InterruptedException exception) {
								exception.printStackTrace();
							}
						}
					}
				});
				account.updatePlayer(args[0]);
			}

			buildMessage(player, account, args[0]);

		} else {
			commandSender.sendMessage("§aUse: §f/account <player>");
		}

		return true;
	}

	private void buildMessage(Player player, Account account, String nickname) {
		Rank rank = account.getRank();

		player.sendMessage("§eInformações sobre a conta do player " + getCoreManager().getUtils().captalize(nickname)
				+ "(" + account.getUniqueId() + "):");

		player.sendMessage("§e-------------------------------------------------------");
		if (rank != null) {

			player.sendMessage("§eRank atual: " + rank.getName().toUpperCase());
			player.sendMessage("§eLiga atual: " + account.getLeague().getName().toUpperCase());
			player.sendMessage("§eXP atual: " + account.getDataHandler().getValue(DataType.GLOBAL_XP).getValue());
			player.sendMessage("§eMoedas: " + account.getDataHandler().getValue(DataType.GLOBAL_COINS).getValue());
			player.sendMessage("§eDoubleXP: " + account.getDoubleXP());
			if (account.isHaveClan())
				if (getCoreManager().getClanAccountManager().getClanAccount(account.getUniqueId()) != null)
				player.sendMessage("§eClan: " + getCoreManager().getClanAccountManager().getClanAccount(account.getUniqueId()).getClan().getName());

			if (rank.getId() != 1) {
				if (account.getRankTime() == 0 && rank.getId() < 13 || rank.getId() == 18
						|| account.getRankTime() == -1) {
					player.sendMessage("" + rank.getTag().getColor() + "§l" + account.getRank().getName().toUpperCase()
							+ " §eda NETWORK");
				} else {
					player.sendMessage(
							"" + rank.getTag().getColor() + "§l" + rank.getName().toUpperCase() + " §eexpira em "
									+ (account.getRankTime() > 10000
											? getCoreManager().getUtils().compareTime(account.getRankTime())
											: "NUNCA").toUpperCase());
				}
			}

		}

		if (!account.getRankSecondary().isEmpty()) {
			for (Map.Entry<Long, Rank> entry : account.getRankSecondary().entrySet()) {

				Long time = entry.getKey();
				Rank ranks = entry.getValue();

				if (ranks.getId() != 1) {
					if (time == 0 && ranks.getId() < 13 || ranks.getId() == 18 || time == -1) {
						player.sendMessage("" + ranks.getTag().getColor() + "§l" + ranks.getName().toUpperCase()
								+ " §eda NETWORK");
					} else {

						if (time < System.currentTimeMillis()) {

						} else {

							player.sendMessage("" + ranks.getTag().getColor() + "§l" + ranks.getName().toUpperCase()
									+ " §eexpira em " + (time > 10000 ? getCoreManager().getUtils().compareTime(time)
											: "NUNCA".toUpperCase()));
						}
					}
				}
			}
		}

		for (PunishRecord punish : account.getPunishRecords().values()) {
			if (punish.isActive()) {
				if (punish.getType().equals(PunishType.BAN)) {
					player.sendMessage("§eFoi banido permanentemente por " + punish.getMotive() + " pelo "
							+ getCoreManager().getNameFetcher().getName(punish.getStaff()) + " na data "
							+ getCoreManager().getUtils().formatDate(punish.getStart()));
				} else if (punish.getType().equals(PunishType.TEMPBAN)) {
					player.sendMessage("§eFoi banido temporariamente por " + punish.getMotive() + " pelo "
							+ getCoreManager().getNameFetcher().getName(punish.getStaff()) + " na data "
							+ getCoreManager().getUtils().formatDate(punish.getStart()) + " e expira em "
							+ getCoreManager().getUtils().formatDate(punish.getExpire()));
				} else if (punish.getType().equals(PunishType.MUTE)) {
					player.sendMessage("§eFoi mutado permanentemente por " + punish.getMotive() + " pelo "
							+ getCoreManager().getNameFetcher().getName(punish.getStaff()) + " na data "
							+ getCoreManager().getUtils().formatDate(punish.getStart()));
				} else if (punish.getType().equals(PunishType.TEMPMUTE)) {
					player.sendMessage("§eFoi mutado temporariamente por " + punish.getMotive() + " pelo "
							+ getCoreManager().getNameFetcher().getName(punish.getStaff()) + " na data "
							+ getCoreManager().getUtils().formatDate(punish.getStart()) + " e expira em "
							+ getCoreManager().getUtils().formatDate(punish.getExpire()));
				}
			}
		}

		player.sendMessage("§e-------------------------------------------------------");

	}

}
