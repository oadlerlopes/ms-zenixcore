package br.com.zenix.core.spigot.player.punish.constructor;

import java.util.Date;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PunishRecord {

	private final int id;
	private int punished, staff;
	private long start, expire;
	private String motive;
	private boolean active;
	private PunishType type;

	public PunishRecord(int id) {
		this.id = id;
	}

	public PunishRecord(int id, int punished, int staff, long start, long expire, String motive, boolean active, PunishType type) {
		this.id = id;
		this.punished = punished;
		this.staff = staff;
		this.start = start;
		this.expire = expire;
		this.motive = motive;
		this.active = active;
		this.type = type;
	}

	public int getPunished() {
		return punished;
	}

	public int getStaff() {
		return staff;
	}

	public long getStart() {
		return start;
	}

	public long getExpire() {
		return expire;
	}

	public String getMotive() {
		return motive;
	}

	public boolean isActive() {
		if (active && expire <= 10000) {
			return true;
		} else if (active) {
			if (new Date().before(new Date(expire)))
				return true;
			if (new Date().after(new Date(expire))) {
				Core.getCoreManager().getPunishManager().unPunishPlayer(punished, id);
				active = false;
			}
			return true;
		} else if (!active) {
			return false;
		} else {
			return false;
		}
	}

	public PunishType getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public void setPunished(int punished) {
		this.punished = punished;
	}

	public void setStaff(int staff) {
		this.staff = staff;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public void setMotive(String motive) {
		this.motive = motive;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setType(PunishType type) {
		this.type = type;
	}

	public String toString() {
		return String.format("PunishRecord[id=%s,punish=%s,staff=%s,start=%s,expire=%s,motive=%s,active=%s,type=%s]", id, punished, staff, start, expire, motive, active, type.name());
	}
}
