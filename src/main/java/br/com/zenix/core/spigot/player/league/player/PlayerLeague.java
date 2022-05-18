package br.com.zenix.core.spigot.player.league.player;

import java.util.Random;

import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.commands.base.MessagesConstructor;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.league.type.LeagueType;
import br.com.zenix.core.spigot.server.type.ServerType;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class PlayerLeague {

	private Account account, adversary;

	private Player player, player2;

	private int minimalValue, winValue, lostValue;

	private DataHandler dataAccount, dataAdversary;

	private LeagueType leagueFirst, leagueSecondary;

	public PlayerLeague(Player player) {
		CoreManager coreManager = Core.getCoreManager();

		this.account = coreManager.getAccountManager().getAccount(player);

		this.leagueFirst = this.account.getLeague();

		this.dataAccount = this.account.getDataHandler();

		this.minimalValue = 0;
		this.winValue = 0;
		this.lostValue = 0;
	}

	public PlayerLeague(Player player, Player adversary) {
		CoreManager coreManager = Core.getCoreManager();

		this.account = coreManager.getAccountManager().getAccount(player);
		this.adversary = coreManager.getAccountManager().getAccount(adversary);

		this.leagueFirst = this.account.getLeague();
		this.leagueSecondary = this.adversary.getLeague();

		this.dataAccount = this.account.getDataHandler();
		this.dataAdversary = this.adversary.getDataHandler();

		this.minimalValue = 0;
		this.winValue = 0;
		this.lostValue = 0;

		this.player = player;
		this.player2 = adversary;
	}

	public void prizeLeague() {
		int finalDebit = minimalValue, finalValue = minimalValue, accountKillstreak = 0, adversaryKillstreak = 0;

		CoreManager coreManager = Core.getCoreManager();

		if (account == adversary) {
			coreManager.getLogger()
					.log("Não é possível continuar o processo de league, já que a conta é igual o adversário");
			return;
		}

		if (account == null) {
			coreManager.getLogger()
					.log("Não é possível continuar o processo de league, já que a conta tem um valor nulo");
			return;
		}

		if (adversary == null) {
			coreManager.getLogger().log(
					"Não é possível continuar o processo de league, já que a conta do adversário tem um valor nulo");
			return;
		}

		if (dataAccount == null) {
			coreManager.getLogger().log(
					"Não é possível continuar o processo de league, já que a conta do adversário tem um valor nulo");
			return;
		}

		if (dataAdversary == null) {
			coreManager.getLogger().log(
					"Não é possível continuar o processo de league, já que a conta do adversário tem um valor nulo");
			return;
		}

		if (coreManager.getServerType().equals(ServerType.PVP)) {
			accountKillstreak = dataAccount.getValue(DataType.PVP_KILLSTREAK).getValue();
			adversaryKillstreak = dataAdversary.getValue(DataType.PVP_KILLSTREAK).getValue();
		} else if (coreManager.getServerType().equals(ServerType.HG)) {
			accountKillstreak = dataAccount.getValue(DataType.HG_KILLSTREAK).getValue();
			adversaryKillstreak = dataAdversary.getValue(DataType.HG_KILLSTREAK).getValue();
		}

		if (leagueFirst == null || leagueSecondary == null)
			return;

		if (leagueFirst.getId() <= leagueSecondary.getId()) {
			finalValue = finalValue + 2;

			if (accountKillstreak > adversaryKillstreak) {
				finalValue = finalValue + ((accountKillstreak / 3) >= 0 ? (accountKillstreak / 3) : 3);
			} else if (adversaryKillstreak > accountKillstreak) {
				finalValue = finalValue + ((adversaryKillstreak / 2) >= 0 ? (adversaryKillstreak / 2) : 5);
			} else {
				finalValue = finalValue + 2 + (int) new Random().nextInt(5);
			}

		} else {
			finalValue = finalValue + 1;

			if (accountKillstreak > adversaryKillstreak) {
				finalValue = finalValue + ((accountKillstreak / 3) >= 0 ? (accountKillstreak / 3) : 2);
			} else if (adversaryKillstreak > accountKillstreak) {
				finalValue = finalValue + ((adversaryKillstreak / 2) >= 0 ? (adversaryKillstreak / 2) : 3);
			} else {
				finalValue = finalValue + 1 + (int) new Random().nextInt(4);
			}
		}

		if (coreManager.getServerType() == ServerType.PVP) {
			finalValue = finalValue + (int) new Random().nextInt(4);
		} else {
			finalValue = finalValue + (int) new Random().nextInt(10);
		}

		if (account.isDoubleRunning()) {
			finalValue = finalValue * 2;
		}

		this.winValue = finalValue;
		this.player.sendMessage("§9§lXP §fVocê ganhou §9§l" + this.winValue + "XP "
				+ (account.isDoubleRunning() ? "§7(DoubleXP)" : ""));
		dataAccount.getValue(DataType.GLOBAL_XP)
				.setValue(dataAccount.getValue(DataType.GLOBAL_XP).getValue() + this.winValue);
		dataAccount.update(DataType.GLOBAL_XP);

		finalDebit = finalValue / 2;

		this.lostValue = finalDebit;
		this.player2.sendMessage("§9§lXP §fVocê perdeu §c§l" + this.lostValue + "XP");

		if (dataAdversary.getValue(DataType.GLOBAL_XP).getValue() > 0) {
			dataAdversary.getValue(DataType.GLOBAL_XP)
					.setValue(dataAdversary.getValue(DataType.GLOBAL_XP).getValue() - this.lostValue);
			dataAdversary.update(DataType.GLOBAL_XP);
		}

		this.checkRank(account);
		this.checkRank(adversary);

		String message = "";

		if (account.isTripleCoinsRunning()) {
			message = "§7(TripleCoins)";
		} else if (account.isDoubleCoinsRunning()) {
			message = "§7(DoubleCoins)";
		}

		int valueToCoins = (this.winValue / 2);

		if (account.isTripleCoinsRunning()) {
			valueToCoins = (this.winValue / 2) * 3;
		} else if (account.isDoubleCoinsRunning()) {
			valueToCoins = (this.winValue / 2) * 2;
		}

		this.player.sendMessage("§6§lMOEDAS §fVocê ganhou §e§l" + valueToCoins + "§f moedas " + message);
		account.getDataHandler().getValue(DataType.GLOBAL_COINS)
				.setValue(account.getDataHandler().getValue(DataType.GLOBAL_COINS).getValue() + valueToCoins);
		account.getDataHandler().update(DataType.GLOBAL_COINS);
		
		if (account.isHaveClan()) {
			Core.getCoreManager().getClanAccountManager().getClanAccount(player).getClan().give(this.player);
		}
		
		if (adversary.isHaveClan()) {
			Core.getCoreManager().getClanAccountManager().getClanAccount(player2).getClan().giveLost(this.player2);
		}

	}

	public LeagueType getNext(LeagueType leagueType) {
		if (leagueType.equals(LeagueType.UNRANKED)) {
			return LeagueType.PRIMARY;
		} else if (leagueType.equals(LeagueType.PRIMARY)) {
			return LeagueType.ADVANCED;
		} else if (leagueType.equals(LeagueType.ADVANCED)) {
			return LeagueType.EXPERT;
		} else if (leagueType.equals(LeagueType.EXPERT)) {
			return LeagueType.SILVER;
		} else if (leagueType.equals(LeagueType.SILVER)) {
			return LeagueType.GOLD;
		} else if (leagueType.equals(LeagueType.GOLD)) {
			return LeagueType.DIAMOND;
		} else if (leagueType.equals(LeagueType.DIAMOND)) {
			return LeagueType.EMERALD;
		} else if (leagueType.equals(LeagueType.EMERALD)) {
			return LeagueType.CRYSTAL;
		} else if (leagueType.equals(LeagueType.CRYSTAL)) {
			return LeagueType.SAPPHIRE;
		} else if (leagueType.equals(LeagueType.SAPPHIRE)) {
			return LeagueType.ELITE;
		} else if (leagueType.equals(LeagueType.ELITE)) {
			return LeagueType.MASTER;
		} else if (leagueType.equals(LeagueType.MASTER)) {
			return LeagueType.LEGENDARY;
		} else if (leagueType.equals(LeagueType.LEGENDARY)) {
			return LeagueType.UNRANKED;
		} else
			return LeagueType.UNRANKED;
	}

	public boolean allowNextRank(Account account, int xp) {
		if (account.getLeague().equals(LeagueType.UNRANKED)) {
			if (xp >= LeagueType.PRIMARY.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.PRIMARY)) {
			if (xp >= LeagueType.ADVANCED.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.ADVANCED)) {
			if (xp >= LeagueType.EXPERT.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.EXPERT)) {
			if (xp >= LeagueType.SILVER.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.SILVER)) {
			if (xp >= LeagueType.GOLD.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.GOLD)) {
			if (xp >= LeagueType.DIAMOND.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.DIAMOND)) {
			if (xp >= LeagueType.EMERALD.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.EMERALD)) {
			if (xp >= LeagueType.CRYSTAL.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.CRYSTAL)) {
			if (xp >= LeagueType.SAPPHIRE.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.SAPPHIRE)) {
			if (xp >= LeagueType.ELITE.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.ELITE)) {
			if (xp >= LeagueType.MASTER.getXpNumber()) {
				return true;
			}
		} else if (account.getLeague().equals(LeagueType.MASTER)) {
			if (xp >= LeagueType.LEGENDARY.getXpNumber()) {
				return true;
			}
		}
		return false;
	}

	public void checkRank(Account account) {
		if (allowNextRank(account, account.getDataHandler().getValue(DataType.GLOBAL_XP).getValue())) {
			account.setXp(account.getDataHandler().getValue(DataType.GLOBAL_XP).getValue() + 1);
			account.setLeague(getNext(account.getLeague()));
			account.update();

			account.getPlayer().sendMessage("§6§lLIGA §fParabéns, você §9§lUPOU§f sua §6§LLIGA!§f Agora você é um: "
					+ account.getLeague().getColor() + "§l" + account.getLeague().getName().toUpperCase());

			account.getPlayer().sendMessage("§5§lCAIXA §fParabéns, você §5§lGANHOU§f uma caixa DIAMOND");

			account.getDataHandler().getValue(DataType.CRATE_DIAMOND)
					.setValue(account.getDataHandler().getValue(DataType.CRATE_DIAMOND).getValue() + 1);
			account.getDataHandler().update(DataType.CRATE_DIAMOND);
			
			MessagesConstructor.sendTitleMessage(account.getPlayer(), "§6§lLIGA", "§fVocê upou para "
					+ account.getLeague().getColor() + "§l" + account.getLeague().getName().toUpperCase());

			account.getCoreManager().getTagManager().updateTagCommand(account.getPlayer());
			account.getCoreManager().getTagManager().updateTag(account.getPlayer());
		}
	}

}
