package br.com.zenix.core.proxy.player.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import br.com.zenix.core.networking.PacketProxyHandler;
import br.com.zenix.core.proxy.commands.base.ProxyListener;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PluginMessageListener extends ProxyListener {

	@EventHandler
	public void onMessage(PluginMessageEvent event) {
		if (!(event.getSender() instanceof Server)) {
			return;
		}

		Server source = (Server) event.getSender();
		String channel = event.getTag();
		ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());

		switch (channel) {
		case PacketProxyHandler.REDIRECT_ALL_CHANNEL:
			getProxyManager().getPacketHandler().redirectPacketAll(source, input);
			break;

		case PacketProxyHandler.REDIRECT_UNIQUE_ID_CHANNEL:
			getProxyManager().getPacketHandler().redirectPacketUniqueId(source, input);
			break;

		case PacketProxyHandler.REDIRECT_NAME_CHANNEL:
			getProxyManager().getPacketHandler().redirectPacketName(source, input);
			break;

		case PacketProxyHandler.PROXY_PACKET_CHANNEL:
			getProxyManager().getPacketHandler().handleProxyPacket(source, input);
			break;

		default:
			return;
		}

		event.setCancelled(true);
	}
}
