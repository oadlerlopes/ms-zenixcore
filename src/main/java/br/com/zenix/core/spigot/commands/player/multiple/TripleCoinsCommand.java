package br.com.zenix.core.spigot.commands.player.multiple;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class TripleCoinsCommand extends BukkitCommand {

	public TripleCoinsCommand() {
		super("triplecoins", "");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;
		Account account = getCoreManager().getAccountManager().getAccount(player);
		if (args.length == 0) {
			if (account.isTripleCoinsRunning() == false) {
				if (account.getTripleCoins() == 0) {
					player.sendMessage("§cVocê não tem nenhum triple-coins!");
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

					account.setTripleCoinsStart(System.currentTimeMillis());
					account.setTripleCoinsEnd(end);
					account.setTripleCoins(account.getTripleCoins() - 1);
					account.setTripleCoinsRunning(true);

					account.getDataHandler().getValue(DataType.TRIPLECOINS_VALUE)
							.setValue(account.getDataHandler().getValue(DataType.TRIPLECOINS_VALUE).getValue() - 1);
					account.getDataHandler().getValue(DataType.TRIPLECOINS_END).setValue((int) end);
					account.getDataHandler().getValue(DataType.TRIPLECOINS_ACTIVE).setValue(1);
					account.getDataHandler().getValue(DataType.TRIPLECOINS_START)
							.setValue((int) System.currentTimeMillis());
				} catch (Exception e) {
					e.printStackTrace();
				}

				player.sendMessage("§aPacote de triple-coins ativado com sucesso!");
				player.sendMessage("§aAgora você tem §f"
						+ account.getDataHandler().getValue(DataType.TRIPLECOINS_VALUE).getValue()
						+ "§a doubles restantes!");
			} else {
				player.sendMessage("§cVocê já está com um pacote de triple-coins ativo!");
			}
		}

		return true;
	}

}
