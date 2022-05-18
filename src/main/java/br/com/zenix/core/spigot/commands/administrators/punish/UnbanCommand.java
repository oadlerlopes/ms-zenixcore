package br.com.zenix.core.spigot.commands.administrators.punish;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.networking.packet.direction.out.PacketOutPardon;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class UnbanCommand extends BukkitCommand {

	public UnbanCommand() {
		super("unban");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!hasPermission(commandSender, "unban")) {
			return false;
		}

		if (args.length < 1) {
			commandSender.sendMessage("§aUse: §f/unban <player/uniqueid>");
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
					new AsyncPardonTask(commandSender, args));
		}
		return false;
	}

	private final class AsyncPardonTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncPardonTask(CommandSender commandSender, String[] args) {
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

				PunishType type1 = PunishType.BAN;
				PunishType type2 = PunishType.TEMPBAN;

				if (!getCoreManager().getPunishManager().hasPunishActive(id, type1)
						&& !getCoreManager().getPunishManager().hasPunishActive(id, type2)) {
					commandSender.sendMessage("§cO jogador não tem nenhum banimento ativo para ser revogado.");
					return;
				}

				Account account = getCoreManager().getAccountManager().getAccount(args[0]);
				List<PunishRecord> records = account != null ? new ArrayList<>(account.getPunishRecords().values())
						: getCoreManager().getPunishManager().getPlayerPunishRecords(id);

				PunishRecord record = records.stream()
						.filter(rec -> rec.isActive() && (rec.getType() == type1 || rec.getType() == type2)).findFirst()
						.orElse(null);
				if (record == null) {
					commandSender.sendMessage("§cUsuário inexistente.");
					return;
				}

				if (record.getMotive().contains("Tentativa de Abuso de Poder")) {
					return;
				}

				if (getCoreManager().getPunishManager().unPunishPlayer(id, record.getId())) {
					commandSender.sendMessage("§aVocê desbaniu o jogador §f" + args[0]);
				} else {
					commandSender.sendMessage("§cAlgo de errado não está certo!");
					return;
				}

				UUID punished = (isUUID(args[0]) ? UUID.fromString(args[0])
						: getCoreManager().getNameFetcher().getUUID(args[0]));

				Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostPardonTask(record, punished));

				return;
			} else {
				commandSender.sendMessage("§cAlgo de errado não está certo!");
			}
		}
	}

	private final class PostPardonTask implements Runnable {

		private final PunishRecord punishRecord;
		private final UUID punishedUniqueId;

		private PostPardonTask(PunishRecord punishRecord, UUID punishedUniqueId) {
			this.punishRecord = punishRecord;
			this.punishedUniqueId = punishedUniqueId;
		}

		@Override
		public void run() {
			Player player = Bukkit.getPlayer(punishedUniqueId);
			if (player != null) {
				if (getCoreManager().getAccountManager().getAccount(player) != null)
					getCoreManager().getAccountManager().getAccount(player).getPunishRecords()
							.remove(punishRecord.getId());
			} else {
				getCoreManager().getPacketHandler()
						.sendGlobalPacket(new PacketOutPardon(punishRecord, punishedUniqueId));
			}

			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.hasPermission("commons.cmd.ban")) {
					if (player != null)
						player.sendMessage("§b§lUNBAN §fO player "
								+ getCoreManager().getNameFetcher().getName(punishedUniqueId.toString()) + "("
								+ punishedUniqueId + ") foi §3§lDESBANIDO§f por "
								+ getCoreManager().getNameFetcher().getName(punishRecord.getStaff()));
				}
			}
		}
	}

}
