package br.com.zenix.core.networking;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.networking.packet.type.PacketType.PacketTypeIn;
import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketProxyHandler extends SimpleHandler {

	public static final String PACKET_CHANNEL = "Packet";

	public static final String REDIRECT_ALL_CHANNEL = "RedirectAll";
	public static final String REDIRECT_UNIQUE_ID_CHANNEL = "RedirectUUID";
	public static final String REDIRECT_NAME_CHANNEL = "RedirectName";
	public static final String PROXY_PACKET_CHANNEL = "ProxyPacket";

	private static final byte ALL_SERVER_TYPES = -1;

	public PacketProxyHandler(ProxyManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {

		registerChannels();
		return true;
	}

	public void sendTargetedPacket(ServerInfo target, PacketOut packet) {
		try {
			ByteArrayDataOutput output = ByteStreams.newDataOutput();
			output.writeByte(ALL_SERVER_TYPES);
			output.writeByte(packet.getPacketType().getId());
			packet.writePacketData(output);

			byte[] data = output.toByteArray();
			target.sendData(PACKET_CHANNEL, data, false);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occurred while trying to send a %s packet to server %s! Details below:", packet.getPacketType(), target.getName());
		}
	}

	public void sendTargetedPacket(Collection<ServerInfo> targets, PacketOut packet) {
		try {
			ByteArrayDataOutput output = ByteStreams.newDataOutput();
			output.writeByte(ALL_SERVER_TYPES);
			output.writeByte(packet.getPacketType().getId());
			packet.writePacketData(output);

			byte[] data = output.toByteArray();
			for (ServerInfo target : targets) {
				target.sendData(PACKET_CHANNEL, data);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occurred while trying to send a %s packet to servers %s! Details below:", packet.getPacketType(), targets);
		}
	}

	public void sendGlobalPacket(PacketOut packet) {
		try {
			ByteArrayDataOutput output = ByteStreams.newDataOutput();
			output.writeByte(ALL_SERVER_TYPES);
			output.writeByte(packet.getPacketType().getId());
			packet.writePacketData(output);

			byte[] data = output.toByteArray();
			for (ServerInfo serverInfo : getProxyManager().getPlugin().getProxy().getServers().values()) {
				serverInfo.sendData(PACKET_CHANNEL, data, false);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occurred while trying to send a %s packet to all servers! Details below:", packet.getPacketType());
		}
	}

	public void redirectPacketAll(Server source, ByteArrayDataInput input) {
		try {
			byte[] data = this.readPacketData(input);

			for (ServerInfo info : getProxyManager().getPlugin().getProxy().getServers().values()) {
				if (info != source.getInfo()) {
					info.sendData(PACKET_CHANNEL, data, false);
				}
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occured while trying to redirect packet from %s to all servers! Details below:", source.getInfo().getName());
		}
	}

	public void redirectPacketUniqueId(Server source, ByteArrayDataInput input) {
		UUID uniqueId = null;
		try {
			long most = input.readLong();
			long least = input.readLong();
			uniqueId = new UUID(most, least);
			ProxiedPlayer player = getProxyManager().getPlugin().getProxy().getPlayer(uniqueId);
			if (player == null) {
				return;
			}

			byte[] data = this.readPacketData(input);
			player.getServer().getInfo().sendData(PACKET_CHANNEL, data, false);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occured while trying to redirect packet from %s to unique id %s! Details below:", source.getInfo().getName(), uniqueId);
		}
	}

	public void redirectPacketName(Server source, ByteArrayDataInput input) {
		String name = null;
		try {
			name = input.readUTF();
			ProxiedPlayer player = getProxyManager().getPlugin().getProxy().getPlayer(name);
			if (player == null) {
				return;
			}

			byte[] data = this.readPacketData(input);
			player.getServer().getInfo().sendData(PACKET_CHANNEL, data, false);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occured while trying to redirect packet from %s to name %s! Details below:", source.getInfo().getName(), name);
		}
	}

	public void handleProxyPacket(Server source, ByteArrayDataInput input) {
		try {
			int packetId = input.readByte();

			PacketTypeIn packetType = PacketType.In.getPacketType(packetId);
			if (packetType == null) {
				getLogger().log(Level.WARNING, "Received unknown packet id: %d", packetId);
				return;
			}

			PacketIn packet = packetType.createInstance();
			if (packet == null) {
				getLogger().log(Level.WARNING, "Failed to instantiate packet: %s", packetType);
				return;
			}

			packet.readPacketData(input);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, e, "An error occurred while trying to handle a proxy packet from %s! Details below:", source == null ? "???" : source.getInfo().getName());
		}
	}

	private byte[] readPacketData(ByteArrayDataInput input) {
		short size = input.readShort();
		byte[] data = new byte[size];
		input.readFully(data);
		return data;
	}

	public void registerChannels() {
		getProxyManager().getPlugin().getProxy().registerChannel(REDIRECT_ALL_CHANNEL);
		getProxyManager().getPlugin().getProxy().registerChannel(REDIRECT_UNIQUE_ID_CHANNEL);
		getProxyManager().getPlugin().getProxy().registerChannel(REDIRECT_NAME_CHANNEL);
		getProxyManager().getPlugin().getProxy().registerChannel(PROXY_PACKET_CHANNEL);
	}
}
