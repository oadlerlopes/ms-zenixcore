package br.com.zenix.core.spigot.commands.administrators.addons;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

public class ResetKDCommand extends BukkitCommand {

	public ResetKDCommand() {
		super("resetkd", "");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			if (!((Player) commandSender).hasPermission("commons.cmd.resetkd")) {
				sendPermissionMessage(commandSender);
				return true;
			}
		}

		if (args.length == 1) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncKSResetTask(commandSender, args));
		} else {
			commandSender.sendMessage("§aUse: §f/resetkd (player)");
		}

		return false;

	}

	private final class AsyncKSResetTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;
		private AsyncKSResetTask(CommandSender commandSender, String[] args) {
			this.commandSender = commandSender;
			this.args = args;
		}

		public void run() {

			String playerName = args[0];

			UUID uuid = isUUID(playerName) ? UUID.fromString(playerName) : getCoreManager().getNameFetcher().getUUID(playerName);
			if (uuid == null) {
				commandSender.sendMessage("§cUsuário inexistente.");
				return;
			}

			int id = getCoreManager().getNameFetcher().getId(uuid);

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
			
			account.getDataHandler().getValue(DataType.PVP_KILL).setValue(0);
			account.getDataHandler().getValue(DataType.PVP_DEATH).setValue(0);
			account.getDataHandler().getValue(DataType.PVP_KILLSTREAK).setValue(0);
			account.getDataHandler().getValue(DataType.PVP_LOSE).setValue(0);
			account.getDataHandler().getValue(DataType.PVP_WIN).setValue(0);
			account.getDataHandler().getValue(DataType.HG_KILL).setValue(0);
			account.getDataHandler().getValue(DataType.HG_DEATH).setValue(0);
			account.getDataHandler().getValue(DataType.HG_KILLSTREAK).setValue(0);
			account.getDataHandler().getValue(DataType.HG_MOST_KILLSTREAK).setValue(0);
			account.getDataHandler().getValue(DataType.HG_WINS).setValue(0);
			account.getDataHandler().getValue(DataType.GLOBAL_XP).setValue(0);
			account.getDataHandler().getValue(DataType.GLOBAL_COINS).setValue(0);
			
			account.getDataHandler().update(DataType.PVP_KILL);
			account.getDataHandler().update(DataType.PVP_DEATH);
			account.getDataHandler().update(DataType.PVP_KILLSTREAK);
			account.getDataHandler().update(DataType.PVP_LOSE);
			account.getDataHandler().update(DataType.PVP_WIN);
			account.getDataHandler().update(DataType.HG_KILL);
			account.getDataHandler().update(DataType.HG_DEATH);
			account.getDataHandler().update(DataType.HG_KILLSTREAK);
			account.getDataHandler().update(DataType.HG_MOST_KILLSTREAK);
			account.getDataHandler().update(DataType.HG_WINS);
			account.getDataHandler().update(DataType.GLOBAL_COINS);
			account.getDataHandler().update(DataType.GLOBAL_XP);
			
			commandSender.sendMessage("§aVocê aplicou o ResetKD no usuário: §f" + getCoreManager().getNameFetcher().getName(id));

		}
	}

}
