package br.com.zenix.core.spigot.player.skin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PlayerList;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap.Serializer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class SkinManager extends Management {

	private static final Storage<UUID, String, String> inFake = new Storage<>();
	private static final HashMap<UUID, PropertyMap> gameProfiles = new HashMap<>();

	public SkinManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return true;
	}

	public Storage<UUID, String, String> getInFake() {
		return inFake;
	}

	public HashMap<UUID, PropertyMap> getGameProfiles() {
		return gameProfiles;
	}

	public boolean validString(String str) {
		return (str.matches("[a-zA-Z0-9_]+")) && (!str.contains(".com") && str.length() > 3);
	}

	public boolean isPremium(String fake) {
		return getUrlContent("https://api.mojang.com/users/profiles/minecraft/" + fake).length() > 0;
	}

	public boolean usingFake(UUID uuid) {
		return inFake.containsKey(uuid);
	}

	public boolean usingFake(String name) {
		return inFake.containsValue(name);
	}

	public boolean nickInUse(String nick) {
		return inFake.containsSubValue(nick);
	}

	public void putPlayerInFake(Player player, String fake) {
		inFake.put(player.getUniqueId(), player.getName(), fake);
	}

	public void removePlayerInFake(Player player) {
		inFake.remove(player.getUniqueId(), inFake.getValue(player.getUniqueId()),
				inFake.getSubValue(player.getUniqueId()));
	}

	public UUID makeUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-"
				+ id.substring(16, 20) + "-" + id.substring(20, 32));
	}

	public boolean fakePlayer(Player player) {
		return fakePlayer(player, ((CraftPlayer) player).getHandle(), inFake.getSubValue(player.getUniqueId()), true);
	}

	// private String value =
	// "eyJ0aW1lc3RhbXAiOjE0NjQyOTYyOTU0MzksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2ZWVjMWMyMTY5YzhjNjBhN2FlNDM2YWJjZDJkYzU0MTdkNTZmOGFkZWY4NGYxMTM0M2RjMTE4OGZlMTM4In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNzY3ZDQ4MzI1ZWE1MzI0NTYxNDA2YjhjODJhYmJkNGUyNzU1ZjExMTUzY2Q4NWFiMDU0NWNjMiJ9fX0=";
	// private String signature =
	// "TOyYc+LBQ5wyGVqXaDvTFejzVOC+ZnsqnXSi9PP4MmCSeU7h0DG6ZwrQbJD3S76wfd+hdIOJurhXW4d/vrDbP4AMUaZzpPRupEZicFxFAl1ZtdtFwzeLYX7COYSLF5nrUy1MSwAN40TnxaEQroLsYFjH0jsqtYLxP1s0WiyNjrEjJ9gWwEPY0fdlNNKjCECYg8vqMafnwsegVifUN8mPJWHykgfGf0sa80nKVTEaNApbbTEHM+EGoU3MDkce65O7tKtSTy979zoKXK+XaJtCQvK5C3s1K49jnxJRHfXcQDW7t6K0VKAoTa5sw/JK4+WmPRNv5eRwOJmGhEcAs1+PN25JB5n+4X/kK2P2eyesIc4DhCUrle+sMifFtaxV6QA15z622wR2XzkUrfiyQyG1b4IuZjuEQcMO/u+rT0PT/Mn5PnofUDagSt/zni+lDT/c8ItXCp1h3oAcMmZ0l4rArIXTeeu6RgRepdrOvKJNr7LjdoHJR9iVCL42GAuEUnwujySKkGP7WfyES9+au7ujPBQhMauMiLFJwoN5RQ9yhv4n2TGwFQ2YArhD4eihDcZ5r/UbpkP9eOS3+C8XZNAK7emrhzob4zFfFTjUBAHxZ92ku9o7Y+PEQN+xItUu70A2aUHJGnE+DEWpaUN7MJmarVhbMZUxuAXjEZiaOjs45z0=";

	private String value = "eyJ0aW1lc3RhbXAiOjE1MTQwNzgxNDM0MzYsInByb2ZpbGVJZCI6ImVlNTMxYmExNTM0YTQ3MjA5ZjY5MjYwNWRiZjU4YTEwIiwicHJvZmlsZU5hbWUiOiJhbWlyMzQzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYTNiYWI3MWVkODQ2NmQzNzNkZGJjOTIzMWY1MWUxZWIxMzMyZGY0OTY1MDI2NDU3MTQzOTY3MzMxY2FjYjUyIn0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lZWMzY2FiZmFlZWQ1ZGFmZTYxYzY1NDYyOTdlODUzYTU0N2MzOWVjMjM4ZDdjNDRiZjRlYjRhNDlkYzFmMmMwIn19fQ==";
	private String signature = "iNB3ZiQDsJZnP8Twxz5LwlyJIeIGpm1M64Cg+h3Izsf5qr4JUB1z2bK0lWjMgm6S++L5WWyScbnlcTnuBNYKDzFZDdM5AZqePxF6S8yErOWlX+xLGQkcExXgtJ/V6XabtHolLbpwss1JcVPxJsZCjCV/MJ0Kk+vSkirUSo5rkPv8qGrGRF610Pf7Q1+HD9K8C/b1BjV0qD4TlL9XJj8R6JbtCTeYwmCiaW5nS2oX+TpftIiEQdMg5I0hOETkIX0yK601fmra6lkhzD7plYRq95QK6ellQALg2EBb+d4drT9MIS/cAT0/Et5WwrWgph0n+LbQCuM4dWsslQzdUeesQWdZdCQWodcJjoT8gJj9FoXkPm0wOCLo3R2aq25pogbeSrjB6txhgeGrrmsfeb4NoMJehJ0aRXjiuGNcQJ7n5VDt7PvsrTWw/Gq56brtn951PJRAn7hkX2ruIQOsSPrhO9Rr5NhpDqHoGe4KSw/HnZia/BFMOrY/l1FYLwayNf6/6CBUjVkTyzDWhFEvqK5lyzHSLYAjSs0SqJsgW82a4v1I4HSbOJiL0ywrSTpuYqX8s7RfSfAR9CtfvokGtzke4XTXm5+ImXwKyEP8UAVnu0iQQZyA4GCWo16hAyCDV/K2kckGq7iuIQERnl2RxxQUQ5fTuqAVy1SfTbHbsgWg3V4=";

	public boolean fakePlayerName(Player player) {
		return fakePlayerName(player, ((CraftPlayer) player).getHandle(), inFake.getSubValue(player.getUniqueId()),
				true);
	}

	public boolean fakePlayerName(final Player player, EntityPlayer entityPlayer, String fake, boolean premium) {

		Location locationData = player.getLocation().clone().add(0, 5, 0);

		try {
			boolean exists = gameProfiles.containsKey(player.getUniqueId());

			try {
				entityPlayer.getClass().getDeclaredField("listName").set(entityPlayer, fake);
				entityPlayer.getClass().getDeclaredField("displayName").set(entityPlayer, fake);
			} catch (Exception e) {
				getLogger().error("Error to set the name " + fake + " to the player " + player.getUniqueId() + ".", e);
			}

			
			Utils.setValue("name", entityPlayer.getProfile(), fake);

			if (gameProfiles.containsKey(player.getUniqueId()) && fake.equals(inFake.getValue(player.getUniqueId()))) {
				PropertyMap profile = gameProfiles.get(player.getUniqueId());

				JsonObject element = new Serializer().serialize(profile, null, null).getAsJsonArray().get(0)
						.getAsJsonObject();
				
				Property property = new Property("textures", element.get("value").getAsString(),
						element.get("signature").getAsString());

				profile.put("textures", property);

				
				Utils.setValue("properties", entityPlayer.getProfile(), profile);

				getLogger().log("The player " + player.getUniqueId() + " want to remove the fake.");

				player.sendMessage("§aVocê voltou para seu nick original!");

				player.teleport(locationData);
			} else {
				if (!exists) {
					gameProfiles.put(player.getUniqueId(), entityPlayer.getProfile().getProperties());
				}

				PropertyMap propertyMap = new PropertyMap();

				if (premium) {
					String uuid = getOriginalUUID("https://zenix.cc/api/player/" + fake).get("id").toString()
							.replaceAll("\"", "").replaceAll(",", "");

					OriginalPlayerSkin playerSkin = new OriginalPlayerSkin(uuid);
					if (playerSkin.loadSkin()) {
						propertyMap.put("textures",
								new Property(playerSkin.getName(), playerSkin.getValue(), playerSkin.getSignature()));
					} else {
						propertyMap.put("textures", new Property("textures", value, signature));
					}
				} else {

					Property property = null;
					
					if (Bukkit.getOnlinePlayers().size() > 1) {
						PropertyMap profile = ((CraftPlayer) Bukkit.getOnlinePlayers().iterator().next()).getHandle()
								.getProfile().getProperties();

						Serializer serializer = new Serializer();
						JsonObject element = serializer.serialize(profile, null, null).getAsJsonArray().get(0)
								.getAsJsonObject();
						property = new Property("textures", element.get("value").getAsString(),
								element.get("signature").getAsString());
					} else {
						property = new Property("textures", value, signature);
					}

					propertyMap.put("textures", property);
				}
				
				
				Utils.setValue("properties", entityPlayer.getProfile(), propertyMap);

				getLogger().log("Profile of gamer " + player.getUniqueId() + " name = "
						+ entityPlayer.getProfile().getName() + " profile.");

				player.sendMessage("§aO seu nick agora é §f" + fake);

				for (Tag tag : getCoreManager().getTagManager().getTags().values()) {
					if (tag.getName().toLowerCase().equals("membro")) {
						getCoreManager().getTagManager().updateTagFake(player, tag);
					}
				}

				player.teleport(locationData.add(0.0D, 0.1D, 0.0D));

				getLogger().log("The player " + player.getUniqueId() + " is changing to fake " + fake + ".");
			}

		} catch (Exception e) {
			getLogger().error("Error to disguise the player " + player.getName() + " to " + fake + ".", e);
		}
		return true;
	}

	public boolean fakePlayer(final Player player, EntityPlayer entityPlayer, String fake, boolean premium) {
		
		Location locationData = player.getLocation().clone().add(0, 5, 0);
		
		try {
			HashSet<Player> canSee = new HashSet<>();
			for (Player online : Bukkit.getOnlinePlayers())
				if (online.canSee(player))
					canSee.add(online);

			PlayerList playerList = MinecraftServer.getServer().getPlayerList();

			PacketPlayOutPlayerInfo remove = PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
			PacketPlayOutEntityDestroy despawn = new PacketPlayOutEntityDestroy(entityPlayer.getId());

			for (Player online : canSee) {
				((CraftPlayer) online).getHandle().playerConnection.sendPacket(remove);
				((CraftPlayer) online).getHandle().playerConnection.sendPacket(despawn);
			}

			fakePlayerName(player, entityPlayer, fake, premium);

			PacketPlayOutPlayerInfo add = PacketPlayOutPlayerInfo.addPlayer(entityPlayer);
			PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
			for (Player online : canSee) {
				((CraftPlayer) online).getHandle().playerConnection.sendPacket(add);
				if (online != player) {
					((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);
				}
			}

			playerList.moveToWorld(entityPlayer, 0, false, locationData, false);

			new BukkitRunnable() {
				
				public void run() {
					player.teleport(locationData);
					
				}
			}.runTaskLater(getCoreManager().getPlugin(), 2L);
			
		} catch (Exception e) {
			getLogger().error("Error to disguise the player " + player.getName() + " to " + fake + ".", e);
		}
		return true;
	}

	private String getUrlContent(String url) {
		try {
			InputStream connection = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection));
			String line = null;
			String content = "";
			while ((line = reader.readLine()) != null) {
				content = content + line;
			}
			return content;
		} catch (Exception exception) {
			getLogger().error("Error trying get the url content from the url " + url + ".", exception);
		}
		return null;
	}

	public JsonObject getOriginalUUID(String name) {
		JsonObject object = null;
		try {
			URL url = new URL(name);

			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 FireFox/25.0");

			Scanner scanner = new Scanner(urlConnection.getInputStream());
			StringBuilder stringBuilder = new StringBuilder();

			while (scanner.hasNext()) {
				stringBuilder.append(scanner.nextLine() + " ");
			}

			scanner.close();
			JsonParser parser = new JsonParser();
			try {
				object = (JsonObject) parser.parse(stringBuilder.toString());
			} catch (Exception e) {
				getLogger().error("Error trying to transform JsonObject for the uuid.", e);
			}
		} catch (Exception e) {
			getLogger().error("Error trying to get the original uuid from the nick " + name + ".", e);
		}
		return object;
	}

	public class OriginalPlayerSkin {

		private String uuid, name, value, signature;

		public OriginalPlayerSkin(String uuid) {
			this.uuid = uuid;
		}

		public boolean loadSkin() {

			try {
				// PreparedStatement preparedStatement =
				// getCoreManager().getMySQLManager().getMySQL().getConnection().prepareStatement("SELECT
				// * FROM `skins_cache` WHERE `uuid`=" + uuid.toString() +
				// ";");
				// ResultSet resultSet = preparedStatement.executeQuery();
				//
				// exists = resultSet.next();
				//
				// if (resultSet.next()) {
				// if (exists && System.currentTimeMillis() <
				// resultSet.getInt("time")) {
				// this.name = "textures";
				// this.value = resultSet.getString("value");
				// this.signature = resultSet.getString("signature");
				//
				// resultSet.close();
				// preparedStatement.close();
				// return true;
				// }
				// }

				URL url = new URL(
						"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
				URLConnection urlConnection = url.openConnection();
				urlConnection.setUseCaches(false);
				urlConnection.setDefaultUseCaches(false);
				urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
				urlConnection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
				urlConnection.addRequestProperty("Pragma", "no-cache");

				@SuppressWarnings("resource")
				String json = new Scanner(urlConnection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
				JSONParser jsonParser = new JSONParser();
				Object object = jsonParser.parse(json);
				JSONArray properties = (JSONArray) ((JSONObject) object).get("properties");

				for (int i = 0; i < properties.size(); i++) {
					try {
						JSONObject property = (JSONObject) properties.get(i);
						String name = (String) property.get("name");
						String value = (String) property.get("value");
						String signature = property.containsKey("signature") ? (String) property.get("signature")
								: null;

						this.name = name;
						this.value = value;
						this.signature = signature;

					} catch (Exception e) {
						getLogger().error("Failed to apply auth property.", e);
						return false;
					}
				}
			} catch (Exception e) {
				getLogger().error("Error when tryed to get the skin of uuid " + uuid.toString() + ".", e);
				return false;
			}
			return true;
		}

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public String getSignature() {
			return signature;
		}
	}

}
