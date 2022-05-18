package br.com.zenix.core.spigot.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.lang.Lang;
import br.com.zenix.core.plugin.data.lang.LangManager;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class LangCommand extends BukkitCommand {

	public LangCommand() {
		super("lang");
		enabled = false;
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;

		LangManager langManager = getCoreManager().getLangManager();

		if (args.length != 1) {

			StringBuilder langs = new StringBuilder();

			langs.append(langManager.getMessage("command-lang-execute", player));

			for (int i = 0; i < Lang.values().length; i++) {
				Lang lang = Lang.values()[i];
				langs.append(i == 0 ? "§f" : "§f, ");
				langs.append(lang.getName() + "§8(§7" + lang.name() + "§8)");
			}

			player.sendMessage(langs.toString());
		} else {

			String name = args[0];

			Lang lang = Lang.getLang(name);
			if (lang == null) {
				player.sendMessage(String.format(langManager.getMessage("command-lang-not-found", player), args[0]));
				return false;
			}

			Account account = getCoreManager().getAccountManager().getAccount(player);
			account.getDataHandler().setValue(DataType.LANG, lang.getId());
			account.getDataHandler().update(DataType.LANG);

			player.sendMessage(langManager.getMessage("command-lang-changed", player));
		}

		return true;
	}
}
