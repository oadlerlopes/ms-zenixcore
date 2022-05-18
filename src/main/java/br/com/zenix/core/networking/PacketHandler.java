package br.com.zenix.core.networking;

import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.zenix.core.networking.packet.direction.PacketDirection;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.networking.packet.type.PacketType.PacketTypeIn;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.server.type.ServerType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketHandler extends Management implements PluginMessageListener {

	private static final String PACKET_CHANNEL = "Packet";
	private static final String REDIRECT_ALL_CHANNEL = "RedirectAll";
	private static final String REDIRECT_UNIQUE_ID_CHANNEL = "RedirectUUID";
	private static final String REDIRECT_NAME_CHANNEL = "RedirectName";
	private static final String PROXY_PACKET_CHANNEL = "ProxyPacket";

	private PacketProcessor processor;

	public PacketHandler(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		this.processor = new PacketProcessor(getCoreManager());
		if (!processor.correctlyStart()) {
			return false;
		}

		return registerChannels(Bukkit.getMessenger());
	}

	public void sendProxyPacket(PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.PROXY) {
			getLogger().log("%s its a %s packet, but is impossible to send to proxy!", packet.getPacketType(), packet.getPacketType().getDirection());
			return;
		}

		Iterator<? extends Player> iter = Bukkit.getOnlinePlayers().iterator();
		Player proxyLink = iter.hasNext() ? iter.next() : null;

		if (proxyLink == null) {
			getLogger().log(Level.WARNING, "NO players online for send the packet: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();
		data.writeByte(packet.getPacketType().getId());
		packet.writePacketData(data);

		proxyLink.sendPluginMessage(getCoreManager().getPlugin(), PROXY_PACKET_CHANNEL, data.toByteArray());
	}

	public void sendServerPacket(ServerType serverType, PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.SERVER) {
			getLogger().log("%s its a %s packet, but is impossible to send to server!", packet.getPacketType(), packet.getPacketType().getDirection());
			return;
		}

		Iterator<? extends Player> iter = Bukkit.getOnlinePlayers().iterator();
		Player proxyLink = iter.hasNext() ? iter.next() : null;

		if (proxyLink == null) {
			getLogger().log(Level.WARNING, "NO players online for send the packet: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();

		flushPacket(REDIRECT_ALL_CHANNEL, data, proxyLink, packet, serverType);
	}

	public void sendGlobalPacket(PacketOut packet) {
		sendServerPacket(null, packet);
	}

	public void sendPacketToUniqueId(UUID uniqueId, PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.SERVER) {
			getLogger().log("%s its a %s packet, but is impossible to send to server!", packet.getPacketType(), packet.getPacketType().getDirection());
			return;
		}

		Iterator<? extends Player> iter = Bukkit.getOnlinePlayers().iterator();
		Player proxyLink = iter.hasNext() ? iter.next() : null;

		if (proxyLink == null) {
			getLogger().log(Level.WARNING, "NO players online for send the packet: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();
		data.writeLong(uniqueId.getMostSignificantBits());
		data.writeLong(uniqueId.getLeastSignificantBits());

		flushPacket(REDIRECT_UNIQUE_ID_CHANNEL, data, proxyLink, packet, null);
		getLogger().log("The packet " + packet.getPacketType() + " has sended to all servers.");
	}

	public void sendPacketToName(String name, PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.SERVER) {
			getLogger().log("%s its a %s packet, but is impossible to send to server!", packet.getPacketType(), packet.getPacketType().getDirection());
			return;
		}

		Iterator<? extends Player> iter = Bukkit.getOnlinePlayers().iterator();
		Player proxyLink = iter.hasNext() ? iter.next() : null;

		if (proxyLink == null) {
			getLogger().log(Level.WARNING, "NO players online for send the packet: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();
		data.writeUTF(name);

		flushPacket(REDIRECT_NAME_CHANNEL, data, proxyLink, packet, null);
	}

	private void flushPacket(String channel, ByteArrayDataOutput data, Player proxyLink, PacketOut packet, ServerType serverType) {
		ByteArrayDataOutput packetData = ByteStreams.newDataOutput();
		packetData.writeByte(serverType == null ? -1 : serverType.getId());
		packetData.writeByte(packet.getPacketType().getId());
		packet.writePacketData(packetData);
		byte[] packetRawData = packetData.toByteArray();

		data.writeShort(packetRawData.length);
		data.write(packetRawData);

		proxyLink.sendPluginMessage(getCoreManager().getPlugin(), channel, data.toByteArray());
	}

	@Override
	public void onPluginMessageReceived(String channel, Player dontMatter, byte[] rawData) {
		try {
			if (!channel.equals(PACKET_CHANNEL)) {
				return;
			}

			ByteArrayDataInput data = ByteStreams.newDataInput(rawData);
			int serverTypeId = data.readByte();
			if (serverTypeId != -1) {
				ServerType serverType = ServerType.getServerType(serverTypeId);
				if (serverType == null) {
					getLogger().log(Level.WARNING, "Type of server unknown: %d", serverTypeId);
					return;
				}

				if (serverType != getCoreManager().getServerType()) {
					return;
				}
			}

			int packetId = data.readByte();

			PacketTypeIn packetType = PacketType.In.getPacketType(packetId);
			if (packetType == null) {
				getLogger().log(Level.WARNING, "Type of packet unknown: %d", packetId);
				return;
			}

			PacketIn packet = packetType.createInstance();
			if (packet == null) {
				getLogger().log(Level.WARNING, "Error when the plugin tried to create a instance of the packet: %s", packetType);
				return;
			}

			packet.readPacketData(data);
			packet.processPacketData(processor);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, e, "Error when the plugin tried read the packet:");
		}
	}

	public boolean registerChannels(Messenger messenger) {
		try {
			messenger.registerIncomingPluginChannel(getCoreManager().getPlugin(), PACKET_CHANNEL, this);
			messenger.registerOutgoingPluginChannel(getCoreManager().getPlugin(), REDIRECT_ALL_CHANNEL);
			messenger.registerOutgoingPluginChannel(getCoreManager().getPlugin(), REDIRECT_UNIQUE_ID_CHANNEL);
			messenger.registerOutgoingPluginChannel(getCoreManager().getPlugin(), REDIRECT_NAME_CHANNEL);
			messenger.registerOutgoingPluginChannel(getCoreManager().getPlugin(), PROXY_PACKET_CHANNEL);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
