package br.com.zenix.core.spigot.commands.moderators;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.networking.packet.direction.out.PacketOutPunishment;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;
import br.com.zenix.core.spigot.twitter.TwitterAccount;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class PunishCommands extends BukkitCommand {

	public PunishCommands() {
		super("tempban");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!hasPermission(commandSender, "tempban")) {
			return false;
		}

		if (args.length < 3) {
			commandSender.sendMessage("§aUse: §f/tempban <player/uniqueid> <time> <motive>");
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
					new AsyncBanTask(commandSender, args));
		}
		return false;
	}

	private final class AsyncBanTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncBanTask(CommandSender commandSender, String[] args) {
			this.commandSender = commandSender;
			this.args = args;
		}

		public void run() {

			if (getCoreManager().getPunishManager().canPunish(args[0])) {

				int id = (isUUID(args[0]) ? getCoreManager().getNameFetcher().getId(UUID.fromString(args[0]))
						: getCoreManager().getNameFetcher().getId(args[0]));

				if (id == -1) {
					commandSender.sendMessage("§cUsuário inexistente.");
					return;
				}

				int staff = commandSender instanceof Player
						? getCoreManager().getNameFetcher().getId(((Player) commandSender).getUniqueId()) : 1;
				String motive = getArgs(args, 2);

				long time;
				try {
					time = getCoreManager().getUtils().parseDateDiff(args[1], true);
				} catch (Exception e) {
					sendNumericMessage(commandSender);
					return;
				}

				if (getCoreManager().getPunishManager().hasPunishActive(id, PunishType.BAN, PunishType.TEMPBAN)) {
					commandSender.sendMessage("§cO jogador já está punido.");
					return;
				}

				PunishRecord record = getCoreManager().getPunishManager().punishPlayer(id, staff, motive, time,
						PunishType.TEMPBAN);

				if (record != null) {
					String timePass = getCoreManager().getUtils().compareSimpleTime(System.currentTimeMillis(),
							record.getExpire());
					commandSender.sendMessage(
							"§aVocê baniu o jogador §f" + args[0] + "§a por §f" + motive + "§a durante §f" + timePass);
				} else {
					commandSender.sendMessage("§cErro ao tentar efetuar a operação, tente novamente mais tarde.");
					return;
				}

				UUID punished = (isUUID(args[0]) ? UUID.fromString(args[0])
						: getCoreManager().getNameFetcher().getUUID(args[0]));

				Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostBanTask(record, punished));
			} else {
				commandSender.sendMessage("§cErro ao tentar efetuar a operação, tente novamente mais tarde.");
			}
		}
	}

	private final class PostBanTask implements Runnable {

		private final PunishRecord punishRecord;
		private final UUID punishedUniqueId;

		private PostBanTask(PunishRecord punishRecord, UUID punishedUniqueId) {
			this.punishRecord = punishRecord;
			this.punishedUniqueId = punishedUniqueId;
		}

		@Override
		public void run() {
			Player player = Bukkit.getPlayer(punishedUniqueId);

			String ipString = "0.0.0.0/";

			if (player != null) {
				ipString = player.getAddress().getAddress().toString();
				player.kickPlayer(" §fVocê foi §3§lBANIDO TEMPORARIAMENTE§f\nPor "
						+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff()) + " na data "
						+ getCoreManager().getUtils().formatDate(punishRecord.getStart()) + "\n§c§lMotivo: §f"
						+ punishRecord.getMotive() + "\n§fExpira em: §c"
						+ getCoreManager().getUtils().compareSimpleTime(punishRecord.getExpire())
						+ "\n\n§fFoi §e§lbanido incorretamente§f? Peça §e§lAPPEAL §fem:" + "\nhttp://forum.zenix.cc\n"
						+ "Compre seu §3§lUNBAN§f em http://loja.zenix.cc para ter o §3§lACESSO§f liberado.");
			}

			getCoreManager().getPacketHandler()
					.sendGlobalPacket(new PacketOutPunishment(punishRecord, punishedUniqueId, ipString));

			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.hasPermission("commons.cmd.ban")) {

					players.sendMessage(
							"§5§lTEMPBAN §f" + getCoreManager().getNameFetcher().getName(punishRecord.getPunished())
									+ "(" + punishedUniqueId + ") foi §5§lBANIDO TEMPORARIAMENTE§f por "
									+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff()) + " durante "
									+ getCoreManager().getUtils().compareSimpleTime(punishRecord.getExpire())
									+ "! Motivo: " + punishRecord.getMotive());
				}
			}

			Bukkit.broadcastMessage("§f" + getCoreManager().getNameFetcher().getName(punishRecord.getPunished())
					+ " foi " + "§c§lbanido temporariamente".toUpperCase() + "§f do servidor.");
		}
	}

	public static class TempMuteCommand extends BukkitCommand {

		public TempMuteCommand() {
			super("tempmute");
		}

		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!hasPermission(commandSender, "tempmute")) {
				return false;
			}

			if (args.length < 3) {
				commandSender.sendMessage("§aUse: §f/tempmute <player/uniqueid> <time> <motive>");
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
						new AsyncMuteTask(commandSender, args));
			}
			return false;
		}

		private final class AsyncMuteTask implements Runnable {

			private final CommandSender commandSender;
			private final String[] args;

			private AsyncMuteTask(CommandSender commandSender, String[] args) {
				this.commandSender = commandSender;
				this.args = args;
			}

			public void run() {
				if (getCoreManager().getPunishManager().canPunish(args[0])) {

					int id = (isUUID(args[0]) ? getCoreManager().getNameFetcher().getId(UUID.fromString(args[0]))
							: getCoreManager().getNameFetcher().getId(args[0]));
					if (id == -1) {
						commandSender.sendMessage("§cUsuário inexistente!");
						return;
					}

					int staff = commandSender instanceof Player
							? getCoreManager().getNameFetcher().getId(((Player) commandSender).getUniqueId()) : 1;
					String motive = getArgs(args, 2);

					long time;
					try {
						time = getCoreManager().getUtils().parseDateDiff(args[1], true);
					} catch (Exception e) {
						sendNumericMessage(commandSender);
						return;
					}

					if (getCoreManager().getPunishManager().hasPunishActive(id, PunishType.MUTE, PunishType.TEMPMUTE)) {
						commandSender.sendMessage("§cO jogador já está punido!");
						return;
					}

					PunishRecord record = getCoreManager().getPunishManager().punishPlayer(id, staff, motive, time,
							PunishType.TEMPMUTE);

					if (record != null) {
						commandSender.sendMessage("§aVocê mutou o jogador §f" + args[0] + "§a por §f" + motive
								+ "§a durante §f" + getCoreManager().getUtils()
										.compareSimpleTime(System.currentTimeMillis(), record.getExpire()));
					} else {
						commandSender.sendMessage("§cErro ao tentar efetuar a operação, tente novamente mais tarde.");
						return;
					}

					UUID punished = (isUUID(args[0]) ? UUID.fromString(args[0])
							: getCoreManager().getNameFetcher().getUUID(args[0]));
					Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostMuteTask(record, punished));
				} else {
					commandSender.sendMessage("§cErro ao tentar efetuar a operação, tente novamente mais tarde.");
				}
			}
		}

		private final class PostMuteTask implements Runnable {

			private final PunishRecord punishRecord;
			private final UUID punishedUniqueId;

			private PostMuteTask(PunishRecord punishRecord, UUID punishedUniqueId) {
				this.punishRecord = punishRecord;
				this.punishedUniqueId = punishedUniqueId;
			}

			@Override
			public void run() {
				Player player = Bukkit.getPlayer(punishedUniqueId);
				if (player != null) {

					Account account = getCoreManager().getAccountManager().getAccount(player);
					if (account != null)
						account.getPunishRecords().put(punishRecord.getId(), punishRecord);
				}

				getCoreManager().getPacketHandler()
						.sendGlobalPacket(new PacketOutPunishment(punishRecord, punishedUniqueId,
								(player != null ? "" + player.getAddress().getAddress().toString() : "0.0.0.0/")));

				for (Player players : Bukkit.getOnlinePlayers()) {
					if (players.hasPermission("commons.cmd.ban")) {

						players.sendMessage("§3§lTEMPMUTE §f"
								+ getCoreManager().getNameFetcher().getName(punishedUniqueId.toString()) + "("
								+ punishedUniqueId + ") foi §3§lMUTADO TEMPORARIAMENTE§f por "
								+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff()) + "! Motivo: "
								+ punishRecord.getMotive());

					}
				}
			}
		}
	}

	public static class KickCommand extends BukkitCommand {

		public KickCommand() {
			super("kick");
		}

		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!hasPermission(commandSender, "kick")) {
				return false;
			}

			if (args.length < 2) {
				commandSender.sendMessage("§aUse: §f/kick <player/uniqueid> <motive>");
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
						new AsyncKickTask(commandSender, args));
			}
			return false;
		}

		private final class AsyncKickTask implements Runnable {

			private final CommandSender commandSender;
			private final String[] args;

			private AsyncKickTask(CommandSender commandSender, String[] args) {
				this.commandSender = commandSender;
				this.args = args;
			}

			public void run() {
				if (getCoreManager().getPunishManager().canPunish(args[0])) {
					Player player = Bukkit.getPlayer(args[0]);
					if (player == null) {
						commandSender.sendMessage("§cPlayer offline.");
						return;
					}

					int id = getCoreManager().getNameFetcher().getId(player.getUniqueId());
					if (id == -1) {
						commandSender.sendMessage("§cUsuário inexistente.");
						return;
					}

					int staff = commandSender instanceof Player
							? getCoreManager().getNameFetcher().getId(((Player) commandSender).getUniqueId()) : 1;
					String motive = getArgs(args, 1);

					PunishRecord record = getCoreManager().getPunishManager().punishPlayer(id, staff, motive, -1,
							PunishType.KICK);

					if (record != null) {
						commandSender.sendMessage("§aVocê kickou o jogador §f" + args[0] + "§a por §f" + motive + "§a");
					} else {
						commandSender.sendMessage("§cAlguma coisa errada acabou de acontecer.");
						return;
					}

					UUID uuid = (isUUID(args[0]) ? UUID.fromString(args[0])
							: getCoreManager().getNameFetcher().getUUID(args[0]));

					Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostKickTask(record, uuid));
				} else {
					commandSender.sendMessage("§cAlguma coisa errada acabou de acontecer.");
				}
			}
		}

		private final class PostKickTask implements Runnable {

			private final PunishRecord punishRecord;
			private final UUID uuid;

			private PostKickTask(PunishRecord punishRecord, UUID punishedUniqueId) {
				this.punishRecord = punishRecord;
				this.uuid = punishedUniqueId;
			}

			@Override
			public void run() {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) {
					player.kickPlayer("§4§lKICK\n§FVocê foi §4§LEXPULSO§f do servidor.\nMotivo da §4§lKICK§f: §f"
							+ punishRecord.getMotive() + "\n \n§czenix.cc");
				}
			}
		}

	}

	public static class BanCommand extends BukkitCommand {

		public BanCommand() {
			super("ban");
		}

		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!hasPermission(commandSender, "ban")) {
				return false;
			}

			if (args.length < 2) {
				commandSender.sendMessage("§aUse: §f/ban <player/uniqueid> <motive>");
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
						new AsyncBanTask(commandSender, args));
			}
			return false;
		}

		private final class AsyncBanTask implements Runnable {

			private final CommandSender commandSender;
			private final String[] args;

			private AsyncBanTask(CommandSender commandSender, String[] args) {
				this.commandSender = commandSender;
				this.args = args;
			}

			public void run() {

				if (getCoreManager().getPunishManager().canPunish(args[0])) {

					int id = (isUUID(args[0]) ? getCoreManager().getNameFetcher().getId(UUID.fromString(args[0]))
							: getCoreManager().getNameFetcher().getId(args[0]));
					int staff = commandSender instanceof Player
							? getCoreManager().getNameFetcher().getId(((Player) commandSender).getUniqueId()) : 1;
					String motive = getArgs(args, 1);

					if (id == -1) {
						commandSender.sendMessage("§cUsuário inexistente.");
						return;
					}

					if (getCoreManager().getPunishManager().hasPunishActive(id, PunishType.BAN, PunishType.TEMPBAN)) {
						commandSender.sendMessage("§cUsuário se encontra banido.");
						return;
					}

					if (args[0].equalsIgnoreCase("Huffass") || args[0].equalsIgnoreCase("AdlerLopes")
							|| args[0].equalsIgnoreCase("Tractions") || args[0].equalsIgnoreCase("Eduardow")
							|| args[0].equalsIgnoreCase("Adler") || args[0].equalsIgnoreCase("Lopsx")
							|| args[0].equalsIgnoreCase("Faane")) {
						if (isPlayer(commandSender)) {

							Player sujeito = (Player) commandSender;

							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"ban " + sujeito.getName() + " Tentativa de Abuso de Poder");
							return;
						}
					}

					PunishRecord record = getCoreManager().getPunishManager().punishPlayer(id, staff, motive, -1,
							PunishType.BAN);

					if (record != null) {
						commandSender.sendMessage("§aVocê baniu o player §f" + args[0] + "§a por §f" + motive + "§f");
					} else {
						commandSender.sendMessage("§cAlguma coisa errada acabou de acontecer.");
						return;
					}

					UUID punished = (isUUID(args[0]) ? UUID.fromString(args[0])
							: getCoreManager().getNameFetcher().getUUID(args[0]));

					Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostBanTask(record, punished));
				} else {
					commandSender.sendMessage("§cAlguma coisa errada acabou de acontecer.");
				}
			}
		}

		private final class PostBanTask implements Runnable {

			private final PunishRecord punishRecord;
			private final UUID punishedUniqueId;

			private PostBanTask(PunishRecord punishRecord, UUID punishedUniqueId) {
				this.punishRecord = punishRecord;
				this.punishedUniqueId = punishedUniqueId;
			}

			@Override
			public void run() {
				Player player = Bukkit.getPlayer(punishedUniqueId);

				String ipString = "0.0.0.0/";

				if (player != null) {
					ipString = player.getAddress().getAddress().toString();
					player.kickPlayer(" §fVocê foi §4§lBANIDO PERMANENTEMENTE§f\nPor "
							+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff()) + " na data "
							+ getCoreManager().getUtils().formatDate(punishRecord.getStart()) + "\n§c§lMotivo: §f"
							+ punishRecord.getMotive() + "\n\nFoi §e§lbanido incorretamente§f? Peça §e§lAPPEAL §fem:"
							+ "\nhttp://forum.zenix.cc\n"
							+ "Compre seu §3§lUNBAN§f em http://loja.zenix.cc para ter o §3§lACESSO§f liberado.");
				}

				getCoreManager().getPacketHandler()
						.sendGlobalPacket(new PacketOutPunishment(punishRecord, punishedUniqueId, ipString));

				for (Player players : Bukkit.getOnlinePlayers()) {
					if (players.hasPermission("commons.cmd.ban")) {
						players.sendMessage(
								"§4§lBAN §f" + getCoreManager().getNameFetcher().getName(punishedUniqueId.toString())
										+ "(" + punishedUniqueId + ") foi §c§lBANIDO§f por "
										+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff())
										+ "! Motivo: " + punishRecord.getMotive());
					}
				}

				Bukkit.broadcastMessage("§f" + getCoreManager().getNameFetcher().getName(punishRecord.getPunished())
						+ " foi " + "§c§lbanido permanentemente".toUpperCase() + "§f do servidor.");

				getCoreManager().getTwitterManager().handleTweet(TwitterAccount.ZENIX_BANS,
						"Jogador banido: " + getCoreManager().getNameFetcher().getName(punishRecord.getPunished())
								+ "\nBanido por: " + getCoreManager().getNameFetcher().getName(punishRecord.getStaff())
								+ "\nMotivo: " + punishRecord.getMotive() + "\n\nServidor: "
								+ getCoreManager().getServerIP());
			}
		}

	}

	public static class MuteCommand extends BukkitCommand {

		public MuteCommand() {
			super("mute");
		}

		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!hasPermission(commandSender, "mute")) {
				return false;
			}

			if (args.length < 1) {
				commandSender.sendMessage("§aUse: §f/mute <player/uniqueid> <motive>");
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
						new AsyncMuteTask(commandSender, args));
			}
			return false;
		}

		private final class AsyncMuteTask implements Runnable {

			private final CommandSender commandSender;
			private final String[] args;

			private AsyncMuteTask(CommandSender commandSender, String[] args) {
				this.commandSender = commandSender;
				this.args = args;
			}

			public void run() {
				if (getCoreManager().getPunishManager().canPunish(args[0])) {

					int id = (isUUID(args[0]) ? getCoreManager().getNameFetcher().getId(UUID.fromString(args[0]))
							: getCoreManager().getNameFetcher().getId(args[0]));

					if (id == -1) {
						commandSender.sendMessage("§cUsuário inexistente.");
						return;
					}

					int staff = commandSender instanceof Player
							? getCoreManager().getNameFetcher().getId(((Player) commandSender).getUniqueId()) : 1;
					String motive = getArgs(args, 1);

					if (getCoreManager().getPunishManager().hasPunishActive(id, PunishType.MUTE, PunishType.TEMPMUTE)) {
						commandSender.sendMessage("§cO usuário já se encontra silenciado.");
						return;
					}

					PunishRecord record = getCoreManager().getPunishManager().punishPlayer(id, staff, motive, -1,
							PunishType.MUTE);

					if (record != null) {
						commandSender.sendMessage("§aVocê mutou o jogador §f" + args[0] + "§a por §f" + motive + "");
					} else {
						commandSender.sendMessage("§cAlguma coisa errada acabou de acontecer.");
						return;
					}

					UUID punished = (isUUID(args[0]) ? UUID.fromString(args[0])
							: getCoreManager().getNameFetcher().getUUID(args[0]));

					Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostMuteTask(record, punished));
				} else {
					commandSender.sendMessage("§cAlguma coisa errada acabou de acontecer.");
				}
			}
		}

		private final class PostMuteTask implements Runnable {

			private final PunishRecord punishRecord;
			private final UUID punishedUniqueId;

			private PostMuteTask(PunishRecord punishRecord, UUID punishedUniqueId) {
				this.punishRecord = punishRecord;
				this.punishedUniqueId = punishedUniqueId;
			}

			@Override
			public void run() {
				Player player = Bukkit.getPlayer(punishedUniqueId);
				if (player != null) {
					player.sendMessage("§3§lMUTE §fVocê foi §b§lMUTADO PERMANENTEMENTE.");

					Account account = getCoreManager().getAccountManager().getAccount(player);
					if (account != null)
						account.getPunishRecords().put(punishRecord.getId(), punishRecord);

				}

				getCoreManager().getPacketHandler()
						.sendGlobalPacket(new PacketOutPunishment(punishRecord, punishedUniqueId,
								(player != null ? "" + player.getAddress().getAddress().toString() : "0.0.0.0/")));

				for (Player players : Bukkit.getOnlinePlayers()) {
					if (players.hasPermission("commons.cmd.ban")) {
						players.sendMessage(
								"§3§lMUTE §f" + getCoreManager().getNameFetcher().getName(punishedUniqueId.toString())
										+ "(" + punishedUniqueId + ") foi §3§lMUTADO§f por "
										+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff())
										+ "! Motivo: " + punishRecord.getMotive());
					}
				}

			}
		}

	}

}
