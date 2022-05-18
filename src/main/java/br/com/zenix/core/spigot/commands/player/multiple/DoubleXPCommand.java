package br.com.zenix.core.spigot.commands.player.multiple;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class DoubleXPCommand extends BukkitCommand {

	public DoubleXPCommand() {
		super("doublexp", "");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;
		Account account = getCoreManager().getAccountManager().getAccount(player);
		if (args.length == 0) {
			if (account.isDoubleRunning() == false) {
				if (account.getDoubleXP() == 0) {
					player.sendMessage("§cVocê não tem nenhum double-xp!");
					return false;
				}

				try {
					int time = 3600;
					long end = 0L;
					if (time == -1L) {
						end = -1L;
					} else {
						long current = System.currentTimeMillis();
						long millis = time * 1000L;
						end = current + millis;
					}

					account.setDoubleStart(System.currentTimeMillis());
					account.setDoubleEnd(end);
					account.setDoublexp(account.getDoubleXP() - 1);
					account.setDoubleRunning(true);
					account.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
				player.sendMessage("§aPacote de double-xp ativado com sucesso!");
				player.sendMessage("§aAgora você tem §f" + account.getDoubleXP() + "§a doubles restantes!");
			} else {
				player.sendMessage("§cVocê já está com um pacote de double-xp ativo!");
			}
		}

		return true;
	}

}
