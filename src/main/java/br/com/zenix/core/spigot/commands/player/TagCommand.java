package br.com.zenix.core.spigot.commands.player;

import java.util.ArrayList;
import java.util.Comparator;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class TagCommand extends BukkitCommand {

	public TagCommand() {
		super("tag", "");
	}

	public ArrayList<Tag> getPlayerTags(Player player) {
		ArrayList<Tag> tags = new ArrayList<>();
		for (Tag tag : getCoreManager().getTagManager().getTags().values()) {
			if (tag.hasTag(player)) {
				tags.add(tag);
			}
		}

		tags.sort(Comparator.comparing(Tag::getOrder).reversed());
		return tags;
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;

		if (args.length == 0) {
			TextComponent tagsMessage = new TextComponent("§aSuas tags atuais são: §f");
			for (int i = 0; i < getPlayerTags(player).size(); i++) {
				Tag tag = getPlayerTags(player).get(i);
				tagsMessage.addExtra(i == 0 ? "" : "§f, ");
				tagsMessage.addExtra(buildGroupComponent(tag, player.getName()));
			}
			player.spigot().sendMessage(tagsMessage);
		} else {
			String selectedGroup = args[0];
			for (Tag tag : getCoreManager().getTagManager().getTags().values()) {
				if (tag.getName().equalsIgnoreCase(selectedGroup)) {
					if (!tag.hasTag(player)) {
						player.sendMessage("§cVocê não possui a tag: " + tag.getName());
						return true;
					}

					player.sendMessage("§aA sua tag foi alterada para §f" + tag.getName().toUpperCase());
					getCoreManager().getTagManager().updateTagCommand(player, tag);
					return true;
				}
			}
			player.sendMessage("§cTag inexistente.");
		}

		return true;
	}

	private BaseComponent buildGroupComponent(Tag tag, String playerName) {
		BaseComponent baseComponent = new TextComponent("§f" + tag.getName().toUpperCase());
		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { new TextComponent("§fExemplo: " + (tag.getPrefix() + playerName)), new TextComponent("\n"), new TextComponent("§aClique para selecionar!") }));
		baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + tag.getName()));
		return baseComponent;
	}

}
