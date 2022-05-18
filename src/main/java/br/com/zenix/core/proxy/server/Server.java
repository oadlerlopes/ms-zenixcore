package br.com.zenix.core.proxy.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.zenix.core.proxy.manager.managements.PingManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Server {

	private final Server instance;

	private final String name, group, template, showIp;

	private final int id, memory, maxPlayers;
	private final boolean autoStart, setupAlways;

	private final InetSocketAddress adress;

	private boolean finalCopy;

	private Date date;
	private String log, logHash;
	private Process process;
	private Thread thread;
	private ServerStatus status;
	private File directory;

	public Server(int id, String name, String group, String template, String showIp, int memory, int maxPlayers, boolean autoStart, boolean setupAlways, InetSocketAddress address) {
		this.id = id;
		this.name = name;
		this.group = group;
		this.template = template;
		this.showIp = showIp;
		this.memory = memory;
		this.maxPlayers = maxPlayers;
		this.autoStart = autoStart;
		this.setupAlways = setupAlways;
		this.adress = address;
		this.status = ServerStatus.OFFLINE;
		this.log = "";
		this.finalCopy = false;
		this.instance = this;
	}

	public boolean create(PingManager serverManager) {
		return false;
	}

	public boolean writeCommand(String command) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			writer.write(command);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}

	public String getTemplate() {
		return template;
	}

	public String getShowIp() {
		return showIp;
	}

	public int getMemory() {
		return memory;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public boolean isSetupAlways() {
		return setupAlways;
	}

	public boolean isFinalCopy() {
		return finalCopy;
	}

	public InetSocketAddress getAddress() {
		return adress;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public ServerStatus getStatus() {
		return status;
	}

	public void setStatus(ServerStatus status) {
		this.status = status;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public File getServerDirectory() {
		return directory;
	}

	public String nameID() {
		return "[" + id + ":" + adress.getPort() + "|" + name.toUpperCase() + "] ";
	}

	public Date getDate() {
		if (date != null)
			return date;
		return new Date();
	}

	public String toString() {
		return "Server[id=" + id + ",name=" + name + ",group=" + group + ",template=" + template + ",adress=" + adress.toString() + ",showip=" + showIp + ",memory=" + memory + ",status=" + status.name() + ",maxplayers="
				+ maxPlayers + ",autostart=" + autoStart + ",setup=" + setupAlways + "]";
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		Gson gson = new Gson();

		jsonObject.add("id", gson.toJsonTree(id));
		jsonObject.add("name", gson.toJsonTree(name));
		jsonObject.add("group", gson.toJsonTree(group));
		jsonObject.add("template", gson.toJsonTree(template));
		jsonObject.add("adress", gson.toJsonTree(adress));
		jsonObject.add("showip", gson.toJsonTree(showIp));
		jsonObject.add("memory", gson.toJsonTree(memory));
		jsonObject.add("status", gson.toJsonTree(status));
		jsonObject.add("maxplayers", gson.toJsonTree(maxPlayers));
		jsonObject.add("autostart", gson.toJsonTree(autoStart));
		jsonObject.add("setup", gson.toJsonTree(setupAlways));
		jsonObject.add("logHash", gson.toJsonTree(logHash));

		return jsonObject;
	}

	public Server getInstance() {
		return instance;
	}

	public String getLogHash() {
		return logHash;
	}

	public void setLogHash(String logHash) {
		this.logHash = logHash;
	}

}
