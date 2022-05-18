package br.com.zenix.core.spigot.commands.administrators.addons;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

public class GiveDoubleXPCommand extends BukkitCommand {

	public GiveDoubleXPCommand() {
		super("givedoublexp");
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
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
					new AsyncVipTask(commandSender, args));
		} else {
			commandSender.sendMessage("§aUse: §f/givedoublexp <player> <quantity>");
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
				commandSender.sendMessage("§cUsuário inexistente.");
				return;
			}

			if (!isInteger(args[1])) {
				sendNumericMessage(commandSender);
				return;
			}
			commandSender.sendMessage("§aVocê adicionou §f" + Integer.valueOf(args[1]) + "§a doublexps"
					+ " §fpara o player §a" + playerName + "(" + uuid + ")");

			Account account = getCoreManager().getAccountManager().getAccount(uuid);
			if (account != null) {
				account.setDoublexp(account.getDoubleXP() + Integer.valueOf(args[1]));
				account.update();
			}
		}
	}

}
