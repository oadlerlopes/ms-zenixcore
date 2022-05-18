package br.com.zenix.core.spigot.player.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.jedis.JedisExecutor;
import br.com.zenix.core.spigot.commands.base.BukkitListener;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.events.ServerTimeEvent;

public class PluginListener extends BukkitListener {

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		getCoreManager().getTagUtils().removeTag(event.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		getCoreManager().getTagUtils().updateTeamsToPlayer(event.getPlayer());

		if (getCoreManager().getAccountManager().getAccount(event.getPlayer()) != null) {
			getCoreManager().getTagManager().updateTagCommand(event.getPlayer());
		} else {
			event.getPlayer().kickPlayer("Conexão perdida");
		}

		getCoreManager().getAccountManager().getAccount(event.getPlayer()).updatePermissions();
	}

	@EventHandler
	public void onTime(ServerTimeEvent event) {
		try {
			getCoreManager().setServerStatus(getCoreManager().getServerStatus().getServerStatus());
			getCoreManager().getDataManager().getJedisHandler().getJedis()
					.publish(JedisExecutor.SERVER_INFO_DATA_CHANNEL, getCoreManager().getServerStatus().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Player players : Bukkit.getOnlinePlayers()) {
			Account account = getCoreManager().getAccountManager().getAccount(players);

			if (account.getRank().getName() == null || account.getLeague().getName() == null
					|| account.getRank() == null || account.getLeague() == null || account == null) {
				players.kickPlayer("§cAlgo ocorreu e sua conta não foi carregada!");
			}

			if (account.isDoubleRunning()) {

				if (!players.hasPermission("zenix.beta.2x2")) {

					long current = System.currentTimeMillis();
					if (((current < account.getDoubleEnd() ? 1 : 0) | (account.getDoubleEnd() == -1L ? 1 : 0)) != 0) {
					} else {
						account.setDoubleStart(0);
						account.setDoubleEnd(0);
						account.setDoubleRunning(false);
						account.update();
						account.getPlayer().sendMessage("§3§lDOUBLEXP §fO seu §e§lDOUBLEXP§f §c§lACABOU!");

					}
				}
			}
			if (account.isDoubleCoinsRunning()) {

				if (!players.hasPermission("zenix.coins")) {
					long current = System.currentTimeMillis();
					if (((current < account.getDoubleCoinsEnd() ? 1 : 0)
							| (account.getDoubleCoinsEnd() == -1L ? 1 : 0)) != 0) {
					} else {
						account.setDoubleCoinsStart(0);
						account.setDoubleCoinsEnd(0);
						account.setDoubleCoinsRunning(false);

						account.getDataHandler().getValue(DataType.DOUBLECOINS_VALUE)
								.setValue(account.getDataHandler().getValue(DataType.DOUBLECOINS_VALUE).getValue() - 1);
						account.getDataHandler().getValue(DataType.DOUBLECOINS_ACTIVE).setValue(0);
						account.getDataHandler().getValue(DataType.DOUBLECOINS_END).setValue(0);
						account.getDataHandler().getValue(DataType.DOUBLECOINS_START).setValue(0);

						account.getDataHandler().update(DataType.DOUBLECOINS_ACTIVE);
						account.getDataHandler().update(DataType.DOUBLECOINS_END);
						account.getDataHandler().update(DataType.DOUBLECOINS_START);
						account.getDataHandler().update(DataType.DOUBLECOINS_VALUE);

						account.getPlayer().sendMessage("§3§lDOUBLECOINS §fO seu §e§lDOUBLECOINS§f §c§lACABOU!");

					}
				}
			}

			if (account.isTripleCoinsRunning()) {
				if (!players.hasPermission("zenix.coins")) {
					long current = System.currentTimeMillis();
					if (((current < account.getTripleCoinsEnd() ? 1 : 0)
							| (account.getTripleCoinsEnd() == -1L ? 1 : 0)) != 0) {
					} else {
						account.setTripleCoinsStart(0);
						account.setTripleCoinsEnd(0);
						account.setTripleCoinsRunning(false);

						account.getDataHandler().getValue(DataType.TRIPLECOINS_VALUE)
								.setValue(account.getDataHandler().getValue(DataType.TRIPLECOINS_VALUE).getValue() - 1);
						account.getDataHandler().getValue(DataType.TRIPLECOINS_ACTIVE).setValue(0);
						account.getDataHandler().getValue(DataType.TRIPLECOINS_END).setValue(0);
						account.getDataHandler().getValue(DataType.TRIPLECOINS_START).setValue(0);

						account.getDataHandler().update(DataType.TRIPLECOINS_ACTIVE);
						account.getDataHandler().update(DataType.TRIPLECOINS_END);
						account.getDataHandler().update(DataType.TRIPLECOINS_START);
						account.getDataHandler().update(DataType.TRIPLECOINS_VALUE);
						account.getPlayer().sendMessage("§3§lTRIPLECOINS §fO seu §e§lTRIPLECOINS§f §c§lACABOU!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if (event.getLeaveMessage().toLowerCase().contains("you logged in from another location")) {
			event.setCancelled(true);
		} else if (event.getLeaveMessage().toLowerCase().contains("flying is not enabled on this server")) {
			event.setCancelled(true);
		}
	}

}
