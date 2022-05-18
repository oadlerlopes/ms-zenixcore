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
public class DoubleCoinsCommand extends BukkitCommand {

	public DoubleCoinsCommand() {
		super("doublecoins", "");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;
		Account account = getCoreManager().getAccountManager().getAccount(player);
		if (args.length == 0) {
			if (account.isDoubleCoinsRunning() == false) {
				if (account.getDoubleCoins() == 0) {
					player.sendMessage("§cVocê não tem nenhum double-coins!");
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

					account.setDoubleCoinsStart(System.currentTimeMillis());
					account.setDoubleCoinsEnd(end);
					account.setDoubleCoins(account.getDoubleCoins() - 1);
					account.setDoubleRunning(true);

					account.getDataHandler().getValue(DataType.DOUBLECOINS_VALUE)
							.setValue(account.getDataHandler().getValue(DataType.DOUBLECOINS_VALUE).getValue() - 1);
					account.getDataHandler().getValue(DataType.DOUBLECOINS_ACTIVE).setValue(1);
					account.getDataHandler().getValue(DataType.DOUBLECOINS_END).setValue((int) end);
					account.getDataHandler().getValue(DataType.DOUBLECOINS_START)
							.setValue((int) System.currentTimeMillis());
				} catch (Exception e) {
					e.printStackTrace();
				}

				player.sendMessage("§aPacote de double-coins ativado com sucesso!");
				player.sendMessage("§aAgora você tem §f"
						+ account.getDataHandler().getValue(DataType.DOUBLECOINS_VALUE).getValue()
						+ "§a doubles restantes!");
			} else {
				player.sendMessage("§cVocê já está com um pacote de double-coins ativo!");
			}
		}

		return true;
	}

}
