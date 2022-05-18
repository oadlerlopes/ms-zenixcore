package br.com.zenix.core.spigot.commands.player.message;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.events.PlayerTellCoreEvent;

public class RCommand extends BukkitCommand {

	public static final HashMap<UUID, UUID> reply = new HashMap<>();

	public RCommand() {
		super("r", "");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;

		if (args.length == 0) {
			player.sendMessage("§aUse: §f/r <mensagem>");
			return false;
		}
		
		Account sujeito = getCoreManager().getAccountManager().getAccount(player);
		
		if (sujeito.getTellLast() == null) {
			player.sendMessage("§cVocê não tem nenhuma mensagem recente.");
			return false;
		}
		
		if (Bukkit.getPlayer(sujeito.getTellLast().getName()) == null) {
			player.sendMessage("§cPlayer offline.");
			sujeito.setTellLast(null);
			return false;
		}
 
		if (!getCoreManager().getAccountManager().getAccount(sujeito.getTellLast()).isTell()
				&& !hasPermission(commandSender, "tellbypass")) {
			player.sendMessage("§cO player desligou os tells.");
			return false;
		}

		String message = StringUtils.join(args, ' ', 0, args.length);

		PlayerTellCoreEvent event = new PlayerTellCoreEvent(player, sujeito.getTellLast(), message);

		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			reply.put(player.getUniqueId(), sujeito.getTellLast().getUniqueId());
			reply.put(sujeito.getTellLast().getUniqueId(), player.getUniqueId());
		}

		getCoreManager().getAccountManager().getAccount(player).setTellLast(sujeito.getTellLast());
		return true;
	}

}
