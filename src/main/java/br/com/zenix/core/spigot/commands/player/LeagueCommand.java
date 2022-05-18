package br.com.zenix.core.spigot.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.league.player.PlayerLeague;
import br.com.zenix.core.spigot.player.league.type.LeagueType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class LeagueCommand extends BukkitCommand {

	public LeagueCommand() {
		super("liga", "", "rank");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;

		if (args.length == 0) {
			player.sendMessage(
					"§aA rede Zenix possui um sistema de liga único que garante aos players uma competição e mais destaque no servidor.");
			player.sendMessage(
					"§aAo matar alguém, ganhar uma partida ou fazer ações específicas de cada Modo de Jogo você recebe uma quantidade de XP calculada por nossa rede para upar de nível.");

			for (LeagueType leagueType : LeagueType.values()) {
				player.sendMessage(
						" " + leagueType.getColor() + leagueType.getSymbol() + " §l" + leagueType.getName().toUpperCase() + " §f");
			}

			Account account = getCoreManager().getAccountManager().getAccount(player);

			new PlayerLeague(player).checkRank(account);

			player.sendMessage(" ");
			player.sendMessage("§aLIGA ATUAL: §f" + account.getLeague().getName().toUpperCase());
			player.sendMessage("§aXP ATUAL: §f" + account.getDataHandler().getValue(DataType.GLOBAL_XP).getValue());
			player.sendMessage("§aXP para a PROXIMA LIGA: §f" + nextRank(account));
		}

		return true;
	}

	public Integer nextRank(Account account) {

		int xp = account.getDataHandler().getValue(DataType.GLOBAL_XP).getValue();

		if (account.getLeague().equals(LeagueType.UNRANKED)) {
			return LeagueType.PRIMARY.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.PRIMARY)) {
			return LeagueType.ADVANCED.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.ADVANCED)) {
			return LeagueType.EXPERT.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.EXPERT)) {
			return LeagueType.SILVER.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.SILVER)) {
			return LeagueType.GOLD.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.GOLD)) {
			return LeagueType.DIAMOND.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.DIAMOND)) {
			return LeagueType.EMERALD.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.EMERALD)) {
			return LeagueType.CRYSTAL.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.CRYSTAL)) {
			return LeagueType.SAPPHIRE.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.SAPPHIRE)) {
			return LeagueType.ELITE.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.ELITE)) {
			return LeagueType.MASTER.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.MASTER)) {
			return LeagueType.LEGENDARY.getXpNumber() - xp;
		}
		if (account.getLeague().equals(LeagueType.LEGENDARY)) {
			return 0;
		}
		return 0;
	}

}
