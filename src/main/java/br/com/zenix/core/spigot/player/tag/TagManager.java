package br.com.zenix.core.spigot.player.tag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;
import br.com.zenix.core.spigot.player.tag.type.TagType;
import br.com.zenix.core.spigot.server.type.ServerType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class TagManager extends Management {

	private static final HashMap<String, Tag> tags = new HashMap<>();
	private static final HashMap<String, Tag> tagsMaked = new HashMap<>();

	private static final HashMap<UUID, String> displayName = new HashMap<>();
	private static final HashMap<UUID, String> prefixRank = new HashMap<>();
	private static final HashMap<UUID, Tag> lastTag = new HashMap<>();

	public TagManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		getLogger().debug("Trying to load all the tags in the enum.");
		try {
			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.TAGS_SELECT.toString());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String prefix = resultSet.getString(3);
				String color = resultSet.getString(4);
				int order = resultSet.getInt(5);

				if (!tagsMaked.containsKey(name)) {
					tagsMaked.put(name, new Tag(id, name, prefix, color, order));
				}
			}

			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to load the tags.", e);
			return false;
		}
		for (TagType tagType : TagType.values()) {
			tags.put(tagType.getTagName(), new Tag(tagType.getId(), tagType.getTagName(), tagType.getPrefix(),
					tagType.getColor(), tagType.getOrder()));
			if (!tagsMaked.containsKey(tagType.getTagName())) {
				createTag(tagType.getTagName(), tagType.getColor(), tagType.getPrefix(), tagType.getOrder(),
						tagType.getId());
			}
		}

		return true;
	}

	public boolean createTag(String tagName, String color, String prefix, int order, int lastId) {
		try {
			long start = System.currentTimeMillis();

			PreparedStatement preparedStatement = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.LAST_ID.toString());
			preparedStatement.setString(1, "global_tags");
			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.close();
			preparedStatement.close();

			getLogger().debug("[" + (System.currentTimeMillis() - start)
					+ "ms] The last id of the table global_tags was getted.");

			color = color.replace('&', '§').replace(";", " ");
			prefix = prefix.replace('&', '§').replace(";", " ");

			PreparedStatement insertStatment = getCoreManager().getDataManager().getMySQL().getConnection()
					.prepareStatement(CoreQueries.TAGS_INSERT.toString());
			insertStatment.setString(1, tagName);
			insertStatment.setString(2, prefix);
			insertStatment.setString(3, color);
			insertStatment.setInt(4, 1000 - order);
			insertStatment.execute();
			insertStatment.close();

			if (!tags.containsKey(tagName)) {
				tags.put(tagName, new Tag(lastId, tagName, prefix, color, order));
			}

			getLogger().debug("[" + (System.currentTimeMillis() - start) + "ms] The tag " + tagName + " was create.");

			return true;

		} catch (Exception e) {
			getLogger().error("Error when the plugin tried to create the tag " + tagName + ".", e);
			return false;
		}
	}

	public Tag getTag(int id) {
		for (Tag tag : tags.values())
			if (tag.getId() == id)
				return tag;
		return null;
	}

	public Tag getTag(String tagName) {
		for (Tag tag : tags.values())
			if (tag.getName().equalsIgnoreCase(tagName))
				return tag;
		return null;
	}

	public Tag tagCommand(Player account) {
		Tag toReturn = null;

		List<Tag> ta = new ArrayList<>();
		for (Tag tag : tags.values())
			if (tag.hasTag(account))
				ta.add(tag);

		ta.sort(Comparator.comparing(Tag::getOrder).reversed());

		toReturn = ta.iterator().next();
		return toReturn;
	}

	public String constructPlayerName(Player player) {
		return getPrefixRank(player) + getDisplayName(player);
	}

	public Tag getPlayerTag(Player player) {
		if (!lastTag.containsKey(player.getUniqueId()))
			return tagCommand(player);
		return lastTag.get(player.getUniqueId());
	}

	public void setPlayerTag(Player player, Tag tag) {
		lastTag.put(player.getUniqueId(), tag);
	}

	public String getDisplayName(Player player) {
		if (!displayName.containsKey(player.getUniqueId())) {
			setDisplayName(player, "§7" + player.getPlayer().getName());
		}
		return displayName.get(player.getUniqueId());
	}

	public String getPrefixRank(Player player) {
		setPrefixRank(player, "§7(§f-§7");

		if (getCoreManager().getAccountManager().getAccount(player).getLeaguePrefix() != null) {
			setPrefixRank(player, getCoreManager().getAccountManager().getAccount(player).getLeaguePrefix());
		}
		return prefixRank.get(player.getUniqueId());
	}

	public void setDisplayName(Player player, String name) {
		if (displayName.containsKey(player.getUniqueId())) {
			removeDisplayName(player);
		}

		displayName.put(player.getUniqueId(), name);
	}

	public void setPrefixRank(Player player, String name) {
		prefixRank.put(player.getUniqueId(), name);
	}

	public void removeDisplayName(Player player) {
		if (displayName.containsKey(player.getUniqueId())) {
			displayName.remove(player.getUniqueId());
		}
	}

	public void removePrefixRank(Player player) {
		if (prefixRank.containsKey(player.getUniqueId())) {
			prefixRank.remove(player.getUniqueId());
		}
	}

	public void updateTag(Player player, String team, String prefix, String suffix) {
		getCoreManager().getTagUtils().setNameTag(player.getPlayer().getName(), team, prefix, " " + suffix);
	}

	public void updateTag(Player player) {
		Tag tag = lastTag.containsKey(player.getUniqueId()) ? lastTag.get(player.getUniqueId()) : tagCommand(player);
		if (getPrefixRank(player) == null || tag.getPrefix() == null || player == null) {
			player.getPlayer().kickPlayer("§cAlgo ocorreu e sua conta não foi carregada.");
		}
		updateTag(player,
				"A" + ((1000 - tag.getOrder()))
						+ (15 + getCoreManager().getAccountManager().getAccount(player).getLeague().getOrder()),
				tag.getPrefix(), getPrefixRank(player));
	}

	public void updateTagCommand(Player player) {
		if (getCoreManager().getServerType() != ServerType.PRACTICE)
			updateTagCommand(player, tagCommand(player));
	}

	public void updateTagCommand(Player player, Tag tag) {
		try {
			if (getPrefixRank(player) == null || tag.getPrefix() == null || player == null) {
				player.getPlayer().kickPlayer("§cAlgo ocorreu e sua conta não foi carregada.");
			}

			setPrefixRank(player, getCoreManager().getAccountManager().getAccount(player).getLeaguePrefix());

			if (getPrefixRank(player) != null) {
				updateTag(player,
						"A" + ((1000 - tag.getOrder()))
								+ (15 + getCoreManager().getAccountManager().getAccount(player).getLeague().getOrder()),
						tag.getPrefix(), getPrefixRank(player));
			} else {
				updateTag(player,
						"A" + ((1000 - tag.getOrder()))
								+ (15 + getCoreManager().getAccountManager().getAccount(player).getLeague().getOrder()),
						tag.getPrefix(), getPrefixRank(player));
			}

			setDisplayName(player, tag.getPrefix() + player.getPlayer().getName());
			setPlayerTag(player, tag);
		} catch (Exception e) {
			player.getPlayer().kickPlayer("§cOcorreu um problema ao carregar seu grupo.");
		}
	}

	public void updateTagFake(Player player, Tag tag) {
		try {
			if (getPrefixRank(player) == null || tag.getPrefix() == null || player == null) {
				player.getPlayer().kickPlayer("§cAlgo ocorreu e sua conta não foi carregada.");
			}
			setPrefixRank(player, getCoreManager().getAccountManager().getAccount(player).getLeaguePrefix());
			if (getPrefixRank(player) != null) {
				updateTag(player,
						"A" + ((1000 - tag.getOrder()))
								+ (15 + getCoreManager().getAccountManager().getAccount(player).getLeague().getOrder()),
						tag.getPrefix(), "§7(§f-§7)");
			} else {
				updateTag(player,
						"A" + ((1000 - tag.getOrder()))
								+ (15 + getCoreManager().getAccountManager().getAccount(player).getLeague().getOrder()),
						tag.getPrefix(), "§7(§f-§7)");
			}

			setDisplayName(player, tag.getPrefix() + player.getPlayer().getName());
			setPlayerTag(player, tag);
		} catch (Exception e) {
			player.getPlayer().kickPlayer("§cOcorreu um problema ao carregar seu grupo.");
		}
	}

	public HashMap<String, Tag> getTags() {
		return tags;
	}

}
