package br.com.zenix.core.networking.packet.type;

import java.util.HashMap;
import java.util.Map;

import br.com.zenix.core.networking.packet.Packet;
import br.com.zenix.core.networking.packet.direction.PacketDirection;
import br.com.zenix.core.networking.packet.direction.in.PacketInClanChat;
import br.com.zenix.core.networking.packet.direction.in.PacketInPardon;
import br.com.zenix.core.networking.packet.direction.in.PacketInPlayerPermission;
import br.com.zenix.core.networking.packet.direction.in.PacketInPunishment;
import br.com.zenix.core.networking.packet.direction.in.PacketInRank;
import br.com.zenix.core.networking.packet.direction.in.PacketInRankHandler;
import br.com.zenix.core.networking.packet.direction.in.PacketInRankUpdate;
import br.com.zenix.core.networking.packet.direction.in.PacketInSetPermission;
import br.com.zenix.core.networking.packet.direction.out.PacketOutClanChat;
import br.com.zenix.core.networking.packet.direction.out.PacketOutPardon;
import br.com.zenix.core.networking.packet.direction.out.PacketOutPlayerPermission;
import br.com.zenix.core.networking.packet.direction.out.PacketOutPunishment;
import br.com.zenix.core.networking.packet.direction.out.PacketOutRank;
import br.com.zenix.core.networking.packet.direction.out.PacketOutRankHandler;
import br.com.zenix.core.networking.packet.direction.out.PacketOutRankUpdate;
import br.com.zenix.core.networking.packet.direction.out.PacketOutSetPermission;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public interface PacketType {

	public int getId();

	public Class<? extends Packet> getImplClass();

	public static interface PacketTypeIn extends PacketType {

		@Override
		public Class<? extends PacketIn> getImplClass();

		public PacketIn createInstance();
	}

	public static interface PacketTypeOut extends PacketType {

		@Override
		public Class<? extends PacketOut> getImplClass();

		public PacketDirection getDirection();
	}

	public static enum In implements PacketTypeIn {
		/**
		 * Server -> Server
		 */
		
		RANK(0, PacketInRank.class),
		PUNISHMENT(1, PacketInPunishment.class),
		PARDON(2, PacketInPardon.class),
		PERMISSION_GROUP(3, PacketInSetPermission.class),
		RANK_UPDATE(4, PacketInRankUpdate.class),
		RANK_HANDLER(5, PacketInRankHandler.class),
		PERMISSION_PLAYER(6, PacketInPlayerPermission.class),
		CLAN_CHAT(7, PacketInClanChat.class);

		/**
		 * Proxy -> Server
		 */

		private static final Map<Integer, PacketTypeIn> BY_ID = new HashMap<Integer, PacketTypeIn>();

		private final int id;
		private final Class<? extends PacketIn> implClass;

		static {
			for (In type : values()) {
				BY_ID.put(type.id, type);
			}
		}

		private In(int id, Class<? extends PacketIn> implClass) {
			this.id = id;
			this.implClass = implClass;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Class<? extends PacketIn> getImplClass() {
			return implClass;
		}

		@Override
		public PacketIn createInstance() {
			try {
				return implClass.newInstance();
			} catch (Exception e) {
				return null;
			}
		}

		public static PacketTypeIn getPacketType(int id) {
			return BY_ID.get(id);
		}
	}

	public static enum Out implements PacketTypeOut {
		/**
		 * Server -> Server
		 */

		RANK(0, PacketOutRank.class, PacketDirection.SERVER),
		PUNISHMENT(1, PacketOutPunishment.class, PacketDirection.SERVER),
		PARDON(2, PacketOutPardon.class, PacketDirection.SERVER),
		PERMISSION_GROUP(3, PacketOutSetPermission.class, PacketDirection.SERVER),
		RANK_UPDATE(4, PacketOutRankUpdate.class, PacketDirection.SERVER),
		RANK_HANDLER(5, PacketOutRankHandler.class, PacketDirection.SERVER),
		PERMISSION_PLAYER(6, PacketOutPlayerPermission.class,PacketDirection.SERVER),
		CLAN_CHAT(7, PacketOutClanChat.class,PacketDirection.SERVER);
		
		/**
		 * Server -> Proxy
		 */

		private static final Map<Integer, PacketTypeOut> BY_ID = new HashMap<Integer, PacketTypeOut>();

		private final int id;
		private final Class<? extends PacketOut> implClass;
		private final PacketDirection direction;

		static {
			for (Out type : values()) {
				BY_ID.put(type.id, type);
			}
		}

		private Out(int id, Class<? extends PacketOut> implClass, PacketDirection direction) {
			this.id = id;
			this.implClass = implClass;
			this.direction = direction;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Class<? extends PacketOut> getImplClass() {
			return implClass;
		}

		@Override
		public PacketDirection getDirection() {
			return direction;
		}

		public static PacketTypeOut getPacketType(int id) {
			return BY_ID.get(id);
		}
	}
}
