package br.com.zenix.core.spigot.commands.administrators.addons.cases;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

public class GiveKeysCommand extends BukkitCommand {

	public GiveKeysCommand() {
		super("givekeys");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			if (!((Player) commandSender).hasPermission("commons.cmd.groupsmaster")) {
				sendPermissionMessage(commandSender);
				return true;
			}
		}

		if (args.length == 3 || args.length == 2) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncVipTask(commandSender, args));
		} else {
			commandSender.sendMessage("§3§lGIVEKEYS §fUse: /givekeys <player> <quantity>");
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

			UUID uuid = isUUID(playerName) ? UUID.fromString(playerName) : getCoreManager().getNameFetcher().getUUID(playerName);

			if (uuid == null) {
				commandSender.sendMessage("§3§lGIVEKEYS §fO player NUNCA entrou no servidor.");
				return;
			}

			if (!isInteger(args[1])) {
				sendNumericMessage(commandSender);
				return;
			}

			commandSender.sendMessage("§3§lGIVEKEYS §fVocê adicionou " + Integer.valueOf(args[1]) + " keys" + " §fpara o player §3" + playerName + "(" + uuid + ")");

			Account account = getCoreManager().getAccountManager().getAccount(uuid);
			if (account != null)
				account.getDataHandler().getValue(DataType.KEY).setValue(account.getDataHandler().getValue(DataType.KEY).getValue() + Integer.valueOf(args[1]));

			account.getDataHandler().update(DataType.KEY);
		}
	}

}
