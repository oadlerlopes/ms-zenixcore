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
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class UnPunishCommand extends BukkitCommand {

	public UnPunishCommand() {
		super("unpunish");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!hasPermission(commandSender, "unpunish")) {
			return false;
		}
		
		if (isPlayer(commandSender))
			return false;

		if (args.length < 2) {
			commandSender.sendMessage("§aUse: §f/unpunish <player/uuid> <type>");
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncPardonTask(commandSender, args));
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

				int id = (isUUID(args[0]) ? getCoreManager().getNameFetcher().getId(UUID.fromString(args[0])) : getCoreManager().getNameFetcher().getId(args[0]));
				if (id == -1) {
					commandSender.sendMessage("§cUsuário inexistente!");
					return;
				}

				PunishType type = PunishType.getType(args[1]);
				if (!getCoreManager().getPunishManager().hasPunishActive(id, type)) {
					commandSender.sendMessage("§cUsuário não tem nenhuma punição para ser revogada.");
					return;
				}

				Account account = getCoreManager().getAccountManager().getAccount(args[0]);
				List<PunishRecord> records = account != null ? new ArrayList<>(account.getPunishRecords().values()) : getCoreManager().getPunishManager().getPlayerPunishRecords(id);

				PunishRecord record = records.stream().filter(rec -> rec.isActive() && rec.getType() == type).findFirst().orElse(null);
				if (record == null) {
					commandSender.sendMessage("§cDeu ruim!");
					return;
				}

				if (getCoreManager().getPunishManager().unPunishPlayer(id, record.getId())) {
					commandSender.sendMessage("§aA operação foi concluida com §fsucesso.");
				} else {
					commandSender.sendMessage("§cOcorreu um problema ao desbanir o player.");
					return;
				}

				UUID punished = (isUUID(args[0]) ? UUID.fromString(args[0]) : getCoreManager().getNameFetcher().getUUID(args[0]));

				Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostPardonTask(record, punished));

				return;
			} else {
				commandSender.sendMessage("§cOcorreu um problema ao desbanir o player.");
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
					getCoreManager().getAccountManager().getAccount(player).getPunishRecords().remove(punishRecord.getId());
			} else {
				getCoreManager().getPacketHandler().sendGlobalPacket(new PacketOutPardon(punishRecord, punishedUniqueId));
			}
		}
	}

}
