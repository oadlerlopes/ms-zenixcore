package br.com.zenix.core.plugin.data.handler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.spigot.player.account.Account;

public class DataHandler {

	private static final Executor saveAsyncExecutor = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Save Async Thread").build());

	public final HashMap<DataType, TotalData> values = new HashMap<>(16);
	public final Account account;

	public DataHandler(Account account) {
		this.account = account;
	}

	public boolean load() {
		try {

			// long ms = System.currentTimeMillis();
			Connection mainConnection = getAccount().getCoreManager().getDataManager().getMySQL().getConnection();

			for (DataType type : DataType.values()) {
				values.put(type, new TotalData(0, 0, 0, System.currentTimeMillis()));
			}

			PreparedStatement preparedStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DATA_QUERY.toString());
			preparedStatement.setInt(1, getAccount().getId());
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				DataType type = DataType.getDataType(resultSet.getInt(3));
				int value = resultSet.getInt(4);

				PreparedStatement typeStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DAILY_DATA_QUERY_BY_TYPE.toString());
				typeStatement.setInt(1, getAccount().getId());
				typeStatement.setInt(2, type.getId());
				typeStatement.setDate(3, new Date(System.currentTimeMillis()));
				ResultSet resultTypeSet = typeStatement.executeQuery();

				int today = 0;
				if (resultTypeSet.next()) {
					today = resultTypeSet.getInt(4);
				}

				values.put(type, new TotalData(value, value, today, System.currentTimeMillis()));

				typeStatement.close();
				resultTypeSet.close();

				// account.getLogger().log(ms, "Loaded the status " + type + "/"
				// + value + "/" + today + "");
			}

			preparedStatement.close();
			resultSet.close();

			// account.getLogger().log(ms, "Loaded the all status of player " +
			// account.getId() + "");

		} catch (Exception exception) {
			getAccount().getLogger().error(String.format("Error when the plugin tried to load the data of the player %s.", getAccount().getUniqueId().toString()), exception);
			return false;
		}
		return true;
	}

	public TotalData getValue(DataType dataType) {
		Preconditions.checkNotNull(dataType);

		TotalData data = getValues().get(dataType);

		if (data == null) {
			getValues().put(dataType, data = new TotalData(0, 0, 0, System.currentTimeMillis()));
		}

		return data;
	}

	public boolean setValue(DataType type, int value) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(value);

		TotalData data = getValue(type);
		data.setValue(value);
		data.setTime(System.currentTimeMillis());
		update(type);

		try {
			getValues().put(type, data);
		} catch (Exception exception) {
			getAccount().getLogger().error(String.format("Error when the plugin tried to set the value %d to the type %s.", value, type.name()), exception);
			return false;
		}
		return true;
	}

	public boolean update(DataType dataType) {

		saveAsyncExecutor.execute(() -> {
			try {
				Connection mainConnection = getAccount().getCoreManager().getDataManager().getMySQL().getConnection();

				TotalData data = getValue(dataType);

				Date today = new Date(System.currentTimeMillis());

				PreparedStatement existsStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DATA_QUERY_BY_TYPE.toString());
				existsStatement.setInt(1, getAccount().getId());
				existsStatement.setInt(2, dataType.getId());
				ResultSet existsSet = existsStatement.executeQuery();

				if (!existsSet.next()) {
					PreparedStatement typeStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DATA_INSERT.toString());
					typeStatement.setInt(1, account.getId());
					typeStatement.setInt(2, dataType.getId());
					typeStatement.setInt(3, getValue(dataType).getValue());
					typeStatement.execute();
				} else {
					PreparedStatement preparedStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DATA_UPDATE.toString());
					preparedStatement.setInt(1, data.getValue());
					preparedStatement.setInt(2, getAccount().getId());
					preparedStatement.setInt(3, dataType.getId());
					preparedStatement.execute();
				}

				PreparedStatement dailyQueryStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DAILY_DATA_QUERY_BY_TYPE.toString());
				dailyQueryStatement.setInt(1, getAccount().getId());
				dailyQueryStatement.setInt(2, dataType.getId());
				dailyQueryStatement.setDate(3, today);
				ResultSet resultSet = dailyQueryStatement.executeQuery();

				if (resultSet.next()) {
					PreparedStatement dailyUpdateStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DAILY_DATA_UPDATE.toString());
					dailyUpdateStatement.setInt(1, data.getDaily());
					dailyUpdateStatement.setInt(2, getAccount().getId());
					dailyUpdateStatement.setInt(3, dataType.getId());
					dailyUpdateStatement.setDate(4, today);
					dailyUpdateStatement.execute();
				} else {
					PreparedStatement dailyInsertStatement = mainConnection.prepareStatement(CoreQueries.ACCOUNT_GLOBAL_DAILY_DATA_INSERT.toString());
					dailyInsertStatement.setInt(1, data.getValue());
					dailyInsertStatement.setInt(2, getAccount().getId());
					dailyInsertStatement.setInt(3, dataType.getId());
					dailyInsertStatement.setDate(4, today);
					dailyInsertStatement.execute();
				}

			} catch (Exception exception) {
				getAccount().getLogger().error(String.format("Error when the plugin tried to update the data of the player %s.", getAccount().getUniqueId().toString()), exception);
				return;
			}
		});

		return true;
	}

	private HashMap<DataType, TotalData> getValues() {
		return values;
	}

	private Account getAccount() {
		return account;
	}

	public static final class TotalData {

		private int login, value, daily;
		private long time;

		public TotalData(int login, int value, int daily, long time) {
			this.login = login;
			this.value = value;
			this.daily = daily;
			this.time = time;
		}

		public int getLogin() {
			return login;
		}

		public int getDaily() {
			return daily;
		}

		public long getTime() {
			return time;
		}

		public int getValue() {
			return value;
		}

		public void setLogin(int login) {
			this.login = login;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public void setValue(int value) {
			this.value = value;
			setDaily(value - login);
		}

		public void setDaily(int daily) {
			this.daily = daily;
		}

	}

}
