package br.com.zenix.core.spigot.anticheat.check;

import br.com.zenix.core.spigot.commands.base.BukkitListener;

public class Check extends BukkitListener {

	private String name;
	private boolean enabled = true, banTimer = false, bannable = true, judgementDay = false;
	private int maxViolations = 5, violationsToNotify = 15;
	private long violationResetTime = 600000L;

	public Check(){}
	
	public Check(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isBannable() {
		return bannable;
	}

	public boolean hasBanTimer() {
		return banTimer;
	}

	public boolean isJudgmentDay() {
		return judgementDay;
	}

	public int getMaxViolations() {
		return maxViolations;
	}

	public int getViolationsToNotify() {
		return violationsToNotify;
	}

	public long getViolationResetTime() {
		return violationResetTime;
	}

	public void setEnabled(boolean Enabled) {
		this.enabled = Enabled;
	}

	public void checkValues() {
		if (getCoreManager().getConfig().getBoolean("checks." + this.getName() + ".enabled") == true) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
		if (getCoreManager().getConfig().getBoolean("checks." + this.getName() + ".bannable") == true) {
			setBannable(true);
		} else {
			setEnabled(false);
		}
	}

	public void setBannable(boolean bannable) {
		this.bannable = bannable;
	}

	public void setAutoBanTimer(boolean banTimer) {
		this.banTimer = banTimer;
	}

	public void setMaxViolations(int maxViolations) {
		this.maxViolations = maxViolations;
	}

	public void setViolationsToNotify(int violationsToNotify) {
		this.violationsToNotify = violationsToNotify;
	}

	public void setViolationResetTime(long violationResetTime) {
		this.violationResetTime = violationResetTime;
	}

	public void setJudgementDay(boolean judgementDay) {
		this.judgementDay = judgementDay;
	}

	public String getName() {
		return name;
	}
}