package br.com.zenix.core.spigot.commands.administrators.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.cup.CupGroupType;

public class GiveTicketCommand extends BukkitCommand {

	public GiveTicketCommand() {
		super("giveticket");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			if (!((Player) commandSender).hasPermission("commons.cmd.cup")) {
				sendPermissionMessage(commandSender);
				return true;
			}
		}

		if (args.length == 0 || args.length == 1) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncGroupTask(commandSender, args));
		} else {
			commandSender.sendMessage("§aUse: §f/giveticket <player> <type>");
		}

		return false;

	}

	private final class AsyncGroupTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncGroupTask(CommandSender commandSender, String[] args) {
			this.commandSender = commandSender;
			this.args = args;
		}

		public void run() {
			String playerName = args[0];

			UUID uuid = isUUID(playerName) ? UUID.fromString(playerName)
					: getCoreManager().getNameFetcher().getUUID(playerName);

			if (uuid == null) {
				commandSender.sendMessage("§aUsuário inexistente.");
				return;
			}

			boolean continueProcess = false;

			for (CupGroupType cupGroupType : CupGroupType.values()) {
				if (args[1].equalsIgnoreCase(cupGroupType.getSigla())) {
					continueProcess = true;
				}
			}

			if (continueProcess == false) {

				List<String> groupsName = new ArrayList<String>();

				for (CupGroupType cupGroupType : CupGroupType.values()) {
					groupsName.add(cupGroupType.getName());
				}

				commandSender.sendMessage("§cA sigla do grupo está incorreta, tente: " + groupsName.toString());
				return;
			}

			CupGroupType cupGroupType = CupGroupType.getCupGroupTypeBySigla(args[2]);

			int id = cupGroupType.getId();

			commandSender.sendMessage("§aO player §f" + playerName + "(" + uuid
					+ ") §arecebeu 1 ingresso para a copa no §f" + cupGroupType.getName().toUpperCase()
					+ "§a para o jogo ás §f" + cupGroupType.getInformation().toUpperCase());

			Account account = new Account(uuid);

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


			DataHandler dataHandler = account.getDataHandler();
			dataHandler.getValue(DataType.CUP_GROUP).setValue(id);
			dataHandler.update(DataType.CUP_GROUP);

			dataHandler.getValue(DataType.GLOBAL_COINS)
					.setValue(dataHandler.getValue(DataType.GLOBAL_COINS).getValue() + 15000);
			dataHandler.update(DataType.GLOBAL_COINS);

			account.setDoublexp(account.getDoubleXP() + 5);
			account.update();

			if (Bukkit.getPlayer(playerName) != null) {
				Bukkit.getPlayer(playerName).sendMessage("");
				Bukkit.getPlayer(playerName).sendMessage("§aAgradecemos pela compra do ingresso para a CopaHG!");
				Bukkit.getPlayer(playerName).sendMessage(
						"§aPrepare-se para a batalha, ela te espera no dia §f" + cupGroupType.getInformation());
				Bukkit.getPlayer(playerName)
						.sendMessage("§aVocê está participando do grupo §f" + cupGroupType.getName());
				Bukkit.getPlayer(playerName).sendMessage(
						"§aVocê GANHOU 15000 coins, 5 doublexps, todos os kits do hg e todos os kits do pvp até o final da copa!");
			}
		}
	}

}
