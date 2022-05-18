package br.com.zenix.core.plugin.data.lang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.account.Account;

public class LangManager extends Management {

	public static final HashMap<String, List<LangMessage>> messages = new HashMap<>();

	public LangManager(CoreManager manager) {
		super(manager);
	}

	public boolean initialize() {

		try {
			Connection connection = getCoreManager().getDataManager().getMySQL().getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(CoreQueries.LANG_DATA_QUERY.toString());
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

				Lang lang = Lang.getLang(resultSet.getInt(2));
				String key = resultSet.getString(3);
				String text = resultSet.getString(4);

				List<LangMessage> messagesList = new ArrayList<>();

				if (messages.containsKey(key)) {
					messagesList = messages.get(key);
				}

				messagesList.add(new LangMessage(lang, text));

				messages.put(key, messagesList);

				getLogger().log("Readed the message '%s' from '%s' lang.", text, lang.name());
			}

			resultSet.close();
			preparedStatement.close();

		} catch (Exception e) {
			getLogger().error("Error to read all langs.", e);
		}

		return true;
	}

	public String getMessage(String key, Lang lang) {
		if (!messages.containsKey(key))
			return "N\\A";
		return messages.get(key).stream().filter(langm -> langm.getLang() == lang).findFirst().orElse(null).getText();
	}

	public String getMessage(String key, Player player) {
		int val = getCoreManager().getAccountManager().getAccount(player).getDataHandler().getValue(DataType.LANG).getValue();
		Lang lang = Lang.getLang(val);
		getLogger().log("LANG= " + lang);
		getLogger().log("VAL = " + val);

		return getMessage(key, lang);
	}

	public String getMessage(String key, Account account) {
		return getMessage(key, Lang.getLang(account.getDataHandler().getValue(DataType.LANG).getValue()));
	}

	public HashMap<String, List<LangMessage>> getMessages() {
		return messages;
	}

	public static final class LangMessage {
		private final Lang lang;
		private final String text;

		public LangMessage(Lang lang, String text) {
			this.lang = lang;
			this.text = text;
		}

		public Lang getLang() {
			return lang;
		}

		public String getText() {
			return text;
		}
	}

}
