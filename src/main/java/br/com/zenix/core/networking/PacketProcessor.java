package br.com.zenix.core.networking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.zenix.core.networking.packet.direction.in.PacketInClanChat;
import br.com.zenix.core.networking.packet.direction.in.PacketInPardon;
import br.com.zenix.core.networking.packet.direction.in.PacketInPlayerPermission;
import br.com.zenix.core.networking.packet.direction.in.PacketInPunishment;
import br.com.zenix.core.networking.packet.direction.in.PacketInRank;
import br.com.zenix.core.networking.packet.direction.in.PacketInRankHandler;
import br.com.zenix.core.networking.packet.direction.in.PacketInRankUpdate;
import br.com.zenix.core.networking.packet.direction.in.PacketInSetPermission;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketProcessor extends Management {

	public PacketProcessor(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return true;
	}

	public void processGroup(PacketInRank packet) {
		Player player = Bukkit.getPlayer(packet.getTargetUniqueId());
		if (player == null) {
			getLogger().log("Recieved rank for %s, but the player isnt online!", packet.getTargetUniqueId());
			return;
		}

		Account account = getCoreManager().getAccountManager().getAccount(player);
		Rank newRank = packet.getNewRank();

		account.setRank(newRank, packet.getExpiry());
	}

	@SuppressWarnings("static-access")
	public void processRankUpdate(PacketInRankUpdate packet) {
		Rank rank = packet.getRank();
		if (rank == null) {
			getLogger().log("Recieved update for rank %s, but the rank is null!", packet.getRank());
			return;
		}

		if (packet.getValue()) {
			for (Rank ranks : getCoreManager().getPermissionManager().getRanks().values()) {
				ranks.setDefaultRank(false);
			}
		}

		rank.setDefaultRank(packet.getValue());
		rank.setTag(packet.getTag());
	}

	public void processRankPermission(PacketInSetPermission packet) {
		Rank rank = packet.getRank();
		if (rank == null) {
			getLogger().log("Recieved permission for rank %s, but the rank is null!", packet.getRank());
			return;
		}

		rank.addPermission(packet.getPermission(), packet.getValue());
		for (Account account : getCoreManager().getAccountManager().getAccounts().values()) {
			if (account.getRank().getId() == rank.getId()) {
				account.setRank(rank, account.getRankTime());
			}
		}
	}

	@SuppressWarnings("static-access")
	public void processRankHandler(PacketInRankHandler packet) {
		Rank rank = getCoreManager().getPermissionManager().getRank(packet.getRank());
		if (rank != null && packet.getValue()) {
			getLogger().log("Recieved %s for rank %s, but the rank already created!", packet.getValue(),
					packet.getRank());
			return;
		} else if (rank != null && !packet.getValue()) {
			getCoreManager().getPermissionManager().getRanks().remove(packet.getName());
		} else if (rank == null && packet.getValue()) {
			getCoreManager().getPermissionManager().getRanks().put(packet.getName(), new Rank(packet.getName(),
					packet.getRank(), getCoreManager().getTagManager().getTags().values().iterator().next(), 0, false));
		}
	}

	public void processPlayerPermission(PacketInPlayerPermission packet) {
		Account player = getCoreManager().getAccountManager().getAccount(packet.getUniqueId());

		if (player == null) {
			getLogger().log("Recieved permission for %s, but the player isnt online!", packet.getUniqueId());
			return;
		}

		player.getPermissions().put(packet.getPermission(), packet.getValue());
		player.setRank(player.getRank(), player.getRankTime());
	}

	public void processClanChat(PacketInClanChat packet) {
		getLogger().log("Received clan-message for %s, sending information to player now!", packet.getClanName());

		for (Player player : Bukkit.getOnlinePlayers()) {
			Account account = getCoreManager().getAccountManager().getAccount(player);

			if (account.isHaveClan()) {
				if (account.isClanChat()) {
					if (getCoreManager().getClanAccountManager().getClanAccount(player).getClan().getName()
							.equalsIgnoreCase(packet.getClanName())) {
						player.sendMessage("§7[CLAN-" + packet.getClanName() + "] §c" + packet.getMessage());
					}
				}
			}
		}
	}

	public void processPunishment(PacketInPunishment packet) {
		Player player = Bukkit.getPlayer(packet.getPunishedUniqueId());
		PunishType punishmentType = packet.getPunishType();
		if (player == null) {
			getLogger().log("Recieved punish for %s, but the player isnt online!", packet.getPunishedUniqueId());

			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.hasPermission("commons.cmd.ban")) {
					if (punishmentType == PunishType.BAN) {
						players.sendMessage("§4§lBAN §f"
								+ getCoreManager().getNameFetcher().getName(packet.getPunishedUniqueId().toString())
								+ "(" + packet.getPunishedUniqueId() + ") foi §c§lBANIDO§f por "
								+ getCoreManager().getNameFetcher().getName(packet.getPunisherId()) + "! Motivo: "
								+ packet.getReason());
					} else if (punishmentType == PunishType.TEMPBAN) {
						players.sendMessage("§5§lTEMPBAN §f"
								+ getCoreManager().getNameFetcher().getName(packet.getPunishedUniqueId().toString())
								+ "(" + packet.getPunishedUniqueId() + ") foi §5§lBANIDO TEMPORARIAMENTE§f por "
								+ getCoreManager().getNameFetcher().getName(packet.getPunisherId()) + " durante "
								+ getCoreManager().getUtils().compareSimpleTime(packet.getExpiry()) + "! Motivo: "
								+ packet.getReason());
					} else if (punishmentType == PunishType.MUTE) {
						players.sendMessage("§3§lMUTE §f"
								+ getCoreManager().getNameFetcher().getName(packet.getPunishedUniqueId().toString())
								+ "(" + packet.getPunishedUniqueId() + ") foi §3§lMUTADO§f por "
								+ getCoreManager().getNameFetcher().getName(packet.getPunisherId()) + "! Motivo: "
								+ packet.getReason());
					} else if (punishmentType == PunishType.TEMPMUTE) {
						players.sendMessage("§3§lTEMPMUTE §f"
								+ getCoreManager().getNameFetcher().getName(packet.getPunishedUniqueId().toString())
								+ "(" + packet.getPunishedUniqueId() + ") foi §3§lMUTADO TEMPORARIAMENTE§f por "
								+ getCoreManager().getNameFetcher().getName(packet.getPunisherId()) + "! Motivo: "
								+ packet.getReason());
					}
				}

				if (punishmentType == PunishType.BAN) {
					players.sendMessage("§fO player "
							+ getCoreManager().getNameFetcher().getName(packet.getPunishedUniqueId().toString())
							+ " foi " + "§c§lbanido permanentemente".toUpperCase() + "§f do servidor.");
				} else if (punishmentType == PunishType.TEMPBAN) {
					players.sendMessage("§fO player "
							+ getCoreManager().getNameFetcher().getName(packet.getPunishedUniqueId().toString())
							+ " foi " + "§c§lbanido temporariamente".toUpperCase() + "§f do servidor.");
				}
			}
		}

		if (player != null) {
			Account account = new Account(player.getUniqueId());

			if (!account.isLoaded()) {
				account.load(new Callback<Boolean>() {
					public void finish(Boolean bool) {
						if (bool)
							getCoreManager().getAccountManager().getAccounts().put(player.getUniqueId(), account);
						else {
							try {
								Thread.sleep(500L);
							} catch (InterruptedException exception) {
								exception.printStackTrace();
							}
						}
					}
				});
				account.updatePlayer(player.getName());
			}

			if (account != null)
				account.getPunishRecords().put(packet.getPunishId(),
						new PunishRecord(packet.getPunishId(), account.getId(), packet.getPunisherId(),
								packet.getStart(), packet.getExpiry(), packet.getReason(), true, punishmentType));

			if (punishmentType == PunishType.BAN) {
				player.kickPlayer(" §fVocê foi §4§lBANIDO PERMANENTEMENTE§f\nPor "
						+ getCoreManager().getNameFetcher().getName(packet.getPunisherId()) + " na data "
						+ getCoreManager().getUtils().formatDate(packet.getStart()) + "\n§c§lMotivo: §f"
						+ packet.getReason() + "\n\nFoi §e§lbanido incorretamente§f? Peça §e§lAPPEAL§f em:"
						+ "\nhttp://forum.zenix.cc\n"
						+ "Compre seu §3§lUNBAN§f em http://loja.zenix.cc para ter o §3§lACESSO§f liberado.");
			} else if (punishmentType == PunishType.TEMPBAN) {
				player.kickPlayer(" §fVocê foi §3§lBANIDO TEMPORARIAMENTE§f\n§fPor "
						+ getCoreManager().getNameFetcher().getName(packet.getPunisherId()) + " na data "
						+ getCoreManager().getUtils().formatDate(packet.getStart()) + "\n§c§lMotivo: §f"
						+ packet.getReason() + "\n§fExpira em: §c"
						+ getCoreManager().getUtils().compareSimpleTime(packet.getExpiry())
						+ "\n\n§fFoi §e§lbanido incorretamente§f? Peça §e§lAPPEAL §fem:" + "\nhttp://forum.zenix.cc\n"
						+ "Compre seu §3§lUNBAN§f em http://loja.zenix.cc para ter o §3§lACESSO§f liberado.");
			}
		}
	}

	public void processPardon(PacketInPardon packet) {
		Player player = Bukkit.getPlayer(packet.getPunishedUniqueId());

		if (player == null) {
			getLogger().log("Recieved punish for %s, but the player isnt online!", packet.getPunishedUniqueId());
			return;
		}

		Account account = getCoreManager().getAccountManager().getAccount(player);
		if (account != null)
			account.getPunishRecords().remove(packet.getPunishId());
	}

}
