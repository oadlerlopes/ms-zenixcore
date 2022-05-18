package br.com.zenix.core.proxy.commands.player;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PingCommand extends ProxyCommand {

	public PingCommand() {
		super("ping");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

		if (args.length == 0) {
			proxiedPlayer.sendMessage("§eO seu ping é de §b"
					+ ProxyServer.getInstance().getPlayer(proxiedPlayer.getName()).getPing() + "ms");
		} else if (args.length == 1) {
			if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
				proxiedPlayer.sendMessage("§eO ping do player §b" + args[0] + "§e é de §b"
						+ ProxyServer.getInstance().getPlayer(args[0]).getPing() + "ms");
			} else {
				proxiedPlayer.sendMessage("§cO player citado está offline.");
			}
		}
		return;
	}

}
