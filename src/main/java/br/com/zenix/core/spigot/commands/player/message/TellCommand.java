package br.com.zenix.core.spigot.commands.player.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.events.PlayerTellCoreEvent;

public class TellCommand extends BukkitCommand {

	public static final HashMap<UUID, UUID> reply = new HashMap<>();

	public TellCommand() {
		super("tell", "", Arrays.asList("w", "whisper", "r"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
				Account account = getCoreManager().getAccountManager().getAccount(player);

				if (args[0].equalsIgnoreCase("on")) {
					account.setTell(true);
					player.sendMessage("§aVocê agora está apto para receber tells.");
				} else if (args[0].equalsIgnoreCase("off")) {
					account.setTell(false);
					player.sendMessage("§cVocê agora não irá mais receber tells.");
				} else {
					player.sendMessage("§aUse: §f/tell <on, off> ou <player> <message>");
				}
			} else {
				player.sendMessage("§aUse: §f/tell <player> <message> ou /tell (on/off).");
			}
			return false;
		}

		if (args.length < 2) {
			player.sendMessage("§aUse: §f/tell <player> <message>");
			return false;
		}

		if (getCoreManager().getSkinManager().usingFake(args[0])) {
			sendOfflinePlayerMessage(commandSender, args[0]);
			return false;
		}

		Player toTell = Bukkit.getPlayer(args[0]);
		if (toTell == null) {
			sendOfflinePlayerMessage(commandSender, args[0]);
			return false;
		}

		if (!getCoreManager().getAccountManager().getAccount(toTell).isTell()
				&& !hasPermission(commandSender, "tellbypass")) {
			player.sendMessage("§cO player desligou os tells.");
			return false;
		}

		String message = args.length > 1 ? StringUtils.join(args, ' ', 1, args.length) : null;

		PlayerTellCoreEvent event = new PlayerTellCoreEvent(player, toTell, message);

		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			reply.put(player.getUniqueId(), toTell.getUniqueId());
			reply.put(toTell.getUniqueId(), player.getUniqueId());
		}
		
		getCoreManager().getAccountManager().getAccount(player).setTellLast(toTell);

		getCoreManager().getAccountManager().getAccount(toTell).setTellLast(player);
		return true;
	}

}
