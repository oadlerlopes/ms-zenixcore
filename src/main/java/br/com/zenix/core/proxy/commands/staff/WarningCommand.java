package br.com.zenix.core.proxy.commands.staff;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class WarningCommand extends ProxyCommand {

	public WarningCommand() {
		super("aviso", "bc", "broadcast");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return;
		}
		
		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

		if (!proxiedPlayer.hasPermission("commands.cmd.broadcast")) {
			sendPermissionMessage(commandSender);
			return;
		}
		
		if (args.length < 1) {
			sendArgumentMessage(commandSender, "§lBROADCAST", "/aviso");
			return;
		}

		String aviso = getArgs(args, 0);
		
		ProxyServer.getInstance().broadcast(new TextComponent("  "));
		ProxyServer.getInstance().broadcast("§4§lAVISO §f" + aviso.replace("&", "§") + "");
		ProxyServer.getInstance().broadcast(new TextComponent("  "));
	}

}
