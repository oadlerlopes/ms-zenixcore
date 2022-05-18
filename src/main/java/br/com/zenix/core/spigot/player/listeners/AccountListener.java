package br.com.zenix.core.spigot.player.listeners;

import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.spigot.commands.base.BukkitListener;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;
import net.minecraft.server.v1_7_R4.EntityPlayer;

public class AccountListener extends BukkitListener {

	public ReentrantLock reentrantLock = new ReentrantLock();

	@EventHandler(priority = EventPriority.LOWEST)
	public void login(AsyncPlayerPreLoginEvent event) {
		reentrantLock.lock();

		Account account = new Account(event.getUniqueId());

		account.load(new Callback<Boolean>() {
			public void finish(Boolean bool) {
				if (bool)
					getCoreManager().getAccountManager().getAccounts().put(event.getUniqueId(), account);
				else {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException exception) {
						exception.printStackTrace();
					}
				}
			}
		});

		reentrantLock.unlock();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void login(PlayerLoginEvent event) {
		long start = System.currentTimeMillis();

		getCoreManager().getAccountManager().getLogger().debug("[" + (System.currentTimeMillis() - start)
				+ "ms] Trying to load the account of the player " + event.getPlayer().getName() + "!");

		Account account = getCoreManager().getAccountManager().getAccount(event.getPlayer());

		if (account == null || !account.isLoaded()) {
			event.setKickMessage("§cA sua conta não pode ser carregada.");
			event.setResult(Result.KICK_OTHER);
			return;
		}

		if (!account.canJoin()) {
			PunishRecord record = account.getLastActiveBan();
			if (record.getType().equals(PunishType.BAN)) {
				event.setKickMessage(" §fVocê foi §4§lBANIDO PERMANENTEMENTE§f\n§fPor "
						+ getCoreManager().getNameFetcher().getName(record.getStaff()) + " na data "
						+ getCoreManager().getUtils().formatDate(record.getStart()) + "\n§c§lMotivo: §f"
						+ record.getMotive() + "\n\nFoi §e§lbanido incorretamente§f? Peça §e§lAPPEAL §fem:"
						+ "\nhttp:forum.zenix.cc\n"
						+ "Compre seu §3§lUNBAN§f em http:loja.zenix.cc para ter o §3§lACESSO§f liberado.");
			} else {
				event.setKickMessage(" §fVocê foi §3§lBANIDO TEMPORARIAMENTE§f\n§fPor "
						+ getCoreManager().getNameFetcher().getName(record.getStaff()) + " na data "
						+ getCoreManager().getUtils().formatDate(record.getStart()) + "\n§c§lMotivo: §f"
						+ record.getMotive() + "\n§fExpira em: §c"
						+ getCoreManager().getUtils().compareSimpleTime(record.getExpire())
						+ "\n\n§fFoi §e§lbanido incorretamente§f? Peça §e§lAPPEAL §fem:" + "\nhttp:forum.zenix.cc\n"
						+ "Compre seu §3§lUNBAN§f em http:loja.zenix.cc para ter o §3§lACESSO§f liberado.");
			}
			event.setResult(Result.KICK_BANNED);
			account.unload();
		}

		account.updatePlayer(event.getPlayer());

		if (event.getResult() == Result.KICK_FULL) {
			if (!event.getPlayer().hasPermission("server.whitelist")) {
				event.disallow(Result.KICK_OTHER, "§cServidor lotado!");
			} else {
				event.allow();
			}
		}

		if (event.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
			if (event.getPlayer().hasPermission("server.whitelist.admin")) {
				event.setResult(PlayerLoginEvent.Result.ALLOWED);
			} else {
				event.disallow(Result.KICK_OTHER, "§cServidor em White-List!");
			}
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		getCoreManager().getAccountManager().updateAccount(event.getPlayer());

		Account account = getCoreManager().getAccountManager().getAccount(player);

		if (player.hasPermission("zenix.beta.2x2")) {
			account.setDoubleRunning(true);
		}

		if (player.hasPermission("zenix.multiple")) {
			account.setDoubleCoinsRunning(true);
			account.setTripleCoinsRunning(true);
		}

		if (player.getAddress().getAddress().toString().contains("0.0.0.0")) {
			player.kickPlayer("Tentativa de invasão.");
		}

		if (getCoreManager().getSkinManager().usingFake(player.getUniqueId())) {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

			getCoreManager().getSkinManager().fakePlayer(player, entityPlayer,
					getCoreManager().getSkinManager().getInFake().getValue(player.getUniqueId()), true);
			getCoreManager().getSkinManager().removePlayerInFake(player);
			getCoreManager().getTagManager().updateTagCommand(player);
		}

		if (account.isParticipatingCup()) {
			PermissionAttachment attachment = player.addAttachment(getCoreManager().getPlugin());

			for (Entry<String, Boolean> s : getCoreManager().getPermissionManager().getRank(632).getPermissions()
					.entrySet()) {
				if (!s.getKey().startsWith("commons.tag"))
					attachment.setPermission(s.getKey(), s.getValue());
			}

			attachment.setPermission("commons.tag.copa", true);
		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void quit(PlayerQuitEvent event) {
		if (getCoreManager().getSkinManager().usingFake(event.getPlayer().getUniqueId())) {
			getCoreManager().getSkinManager().removePlayerInFake(event.getPlayer());
		}
		getCoreManager().getAccountManager().unloadAccount(event.getPlayer().getUniqueId());
	}

}
