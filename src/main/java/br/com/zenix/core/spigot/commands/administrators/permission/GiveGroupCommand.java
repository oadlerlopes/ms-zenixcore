package br.com.zenix.core.spigot.commands.administrators.permission;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.networking.packet.direction.out.PacketOutRank;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;

public class GiveGroupCommand extends BukkitCommand {

	public GiveGroupCommand() {
		super("groupset");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (isPlayer(commandSender)) {
			if (!((Player) commandSender).hasPermission("commons.cmd.groups")) {
				sendPermissionMessage(commandSender);
				return true;
			}
		}

		if (args.length == 3 || args.length == 2) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncGroupTask(commandSender, args));
		} else {
			commandSender.sendMessage("§aUse: §f/groupset <player> <rank> <time>");
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
			String groupName = args[1];
			String timeString = args.length == 2 ? "-1" : args[2];

			UUID uuid = isUUID(playerName) ? UUID.fromString(playerName) : getCoreManager().getNameFetcher().getUUID(playerName);
			
			if (uuid == null) {
				sendMessage(commandSender, "§cUsuário inexistente.");
				return;
			}

			Rank rank = getCoreManager().getPermissionManager().getRank(groupName);

			if (rank == null) {
				sendMessage(commandSender, "§cEsse rank é inexistente!");
				return;
			} else {
				if (isPlayer(commandSender)) {
					if (!(getCoreManager().getAccountManager().getAccount((Player) commandSender).getRank().getId() < 3)) {
						if (getCoreManager().getAccountManager().getAccount((Player) commandSender).getRank().getId() > rank.getId()) {
							sendMessage(commandSender, "§cVocê não tem permissão para setar esse rank!");
							return;
						}
					}
				}
			}

			if (isPlayer(commandSender)) {
				if (rank == getCoreManager().getAccountManager().getAccount((Player) commandSender).getRank()) {
					sendMessage(commandSender, "§cVocê não pode ser um grupo igual ao seu!");
					return;
				}
			}

			long time;
			if (timeString.equalsIgnoreCase("-1")) {
				time = -1;
			} else {
				try {
					time = getCoreManager().getUtils().parseDateDiff(timeString, true);
				} catch (Exception e) {
					sendNumericMessage(commandSender);
					return;
				}
			}

			int id = getCoreManager().getNameFetcher().getId(uuid);

			Account accountPlayer = new Account(uuid);

			if (!accountPlayer.isLoaded()) {
				accountPlayer.load(new Callback<Boolean>() {
					public void finish(Boolean bool) {
						if (bool)
							getCoreManager().getAccountManager().getAccounts().put(uuid, accountPlayer);
						else {
							try {
								Thread.sleep(500L);
							} catch (InterruptedException exception) {
								exception.printStackTrace();
							}
						}
					}
				});
				accountPlayer.updatePlayer(args[0]);
			}

			if (getCoreManager().getPermissionManager().giveRankPlayer(id, rank, time)) {
				if (time == -1) {
					sendMessage(commandSender, "§aO jogador §f" + playerName + "(" + uuid + ") §ateve seu rank alterado para §f" + rank.getName().toUpperCase() + "§a sem data para a §fexpiracao do rank!"
							+ getCoreManager().getUtils().compareTime(System.currentTimeMillis(), time));
				} else {
					sendMessage(commandSender, "§aO jogador §f" + playerName + "(" + uuid + ") §ateve seu rank alterado para §f" + rank.getName().toUpperCase() + "§a para expirar em §f"
							+ getCoreManager().getUtils().compareTime(System.currentTimeMillis(), time));
				}

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
					account.updatePlayer(playerName);
				}

				if (Bukkit.getPlayer(playerName) != null) {
					account.setPlayer(Bukkit.getPlayer(playerName));
				}

				account.setRank(rank, time);

				getCoreManager().getPacketHandler().sendPacketToUniqueId(uuid, new PacketOutRank(uuid, rank, (int) time));

			} else {
				sendMessage(commandSender, "§cOcorreu um problema ao setar o rank.");
			}
		}
	}

}
