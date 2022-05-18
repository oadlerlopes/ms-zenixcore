package br.com.zenix.core.spigot.commands.administrators.addons.cases;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.DataHandler;
import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

public class GiveBoxesCommand extends BukkitCommand {

	public GiveBoxesCommand() {
		super("giveboxes");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			if (!((Player) commandSender).hasPermission("commons.cmd.groupsmaster")) {
				sendPermissionMessage(commandSender);
				return true;
			}
		}

		if (args.length == 4 || args.length == 3 || args.length == 2) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
					new AsyncVipTask(commandSender, args));
		} else {
			commandSender.sendMessage("§3§lGIVEBOXES §fUse: /giveboxes <player> <type> <quantity>");
		}

		return false;

	}

	private final class AsyncVipTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncVipTask(CommandSender commandSender, String[] args) {
			this.commandSender = commandSender;
			this.args = args;
		}

		public void run() {

			String playerName = args[0];

			UUID uuid = isUUID(playerName) ? UUID.fromString(playerName)
					: getCoreManager().getNameFetcher().getUUID(playerName);

			if (uuid == null) {
				commandSender.sendMessage("§3§lGIVEBOXES §fO player NUNCA entrou no servidor.");
				return;
			}

			if (!args[1].equalsIgnoreCase("coal") && !args[1].equalsIgnoreCase("diamond")
					&& !args[1].equalsIgnoreCase("gold") && !args[1].equalsIgnoreCase("silver")) {
				commandSender.sendMessage("Argumente a caixa.  -" + args[1]);
				return;
			}

			String box = args[1];

			if (!isInteger(args[2])) {
				sendNumericMessage(commandSender);
				return;
			}

			int ammount = Integer.valueOf(args[2]);
			commandSender.sendMessage("§3§lGIVEBOXES §fO player " + playerName + "(" + uuid + ") teve " + ammount + " boxes do tipo " + args[1].toUpperCase() + " adicionados em sua conta.");

			Account account = getCoreManager().getAccountManager().getAccounts().containsKey(uuid)
					? getCoreManager().getAccountManager().getAccount(uuid)
					: getCoreManager().getAccountManager().craftAccount(uuid);
					
			DataHandler dataHandler = account.getDataHandler();

			switch (box.toLowerCase()) {
			case "coal":
				dataHandler.getValue(DataType.CRATE_COAL)
						.setValue(dataHandler.getValue(DataType.CRATE_COAL).getValue() + ammount);
				dataHandler.update(DataType.CRATE_COAL);
				dataHandler.getValue(DataType.KEY).setValue(dataHandler.getValue(DataType.KEY).getValue() + ammount);
				dataHandler.update(DataType.KEY);
				break;

			case "diamond":
				dataHandler.getValue(DataType.CRATE_DIAMOND)
						.setValue(dataHandler.getValue(DataType.CRATE_DIAMOND).getValue() + ammount);
				dataHandler.update(DataType.CRATE_DIAMOND);
				dataHandler.getValue(DataType.KEY).setValue(dataHandler.getValue(DataType.KEY).getValue() + ammount);
				dataHandler.update(DataType.KEY);
				break;

			case "gold":
				dataHandler.getValue(DataType.CRATE_GOLD)
						.setValue(dataHandler.getValue(DataType.CRATE_GOLD).getValue() + ammount);
				dataHandler.update(DataType.CRATE_GOLD);
				dataHandler.getValue(DataType.KEY).setValue(dataHandler.getValue(DataType.KEY).getValue() + ammount);
				dataHandler.update(DataType.KEY);
				break;

			case "silver":
				dataHandler.getValue(DataType.CRATE_SILVER)
						.setValue(dataHandler.getValue(DataType.CRATE_SILVER).getValue() + ammount);
				dataHandler.update(DataType.CRATE_SILVER);
				dataHandler.getValue(DataType.KEY).setValue(dataHandler.getValue(DataType.KEY).getValue() + ammount);
				dataHandler.update(DataType.KEY);
				break;
			default:
				break;
			}

		}
	}

}
