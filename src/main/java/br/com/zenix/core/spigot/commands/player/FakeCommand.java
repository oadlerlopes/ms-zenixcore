package br.com.zenix.core.spigot.commands.player;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.events.PlayerSkinFakeChange;
import br.com.zenix.core.spigot.player.skin.Storage;
import br.com.zenix.core.spigot.server.type.ServerType;
import net.minecraft.server.v1_7_R4.EntityPlayer;

public class FakeCommand extends BukkitCommand {

	private static final String[] randomNicks = new String[] { "zJohnsPvP_", "cura139", "96o1lxo", "54901xo", "zCuruxumba", "z66Jans" };

	public FakeCommand() {
		super("fake", "Change your name, skin, with this command.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		if (!hasPermission(commandSender, "fake")) {
			return false;
		}

		final Player player = (Player) commandSender;
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

		if (args.length != 1) {
			player.sendMessage("§aUse: §f/fake <nick>, <random>" + (player.hasPermission("commons.cmd.fake") ? ", <list>" : ""));
			return false;
		}
		if (args[0].equalsIgnoreCase("#")) {

			if (!getCoreManager().getSkinManager().usingFake(player.getUniqueId())) {
				player.sendMessage("§cVocê não usando um fake, para usar digite /fake.");
				return false;
			}

			getCoreManager().getSkinManager().fakePlayer(player, entityPlayer, getCoreManager().getSkinManager().getInFake().getValue(player.getUniqueId()), true);
			getCoreManager().getSkinManager().removePlayerInFake(player);
			getCoreManager().getTagManager().updateTagCommand(player);
		} else if (args[0].equalsIgnoreCase("random")) {
			String nick = randomNicks[new Random().nextInt(randomNicks.length - 1)];
			player.chat("/fake " + nick);
		} else {
			String nick = args[0];

			if (nick.equalsIgnoreCase("list")) {
				if (!player.hasPermission("commons.cmd.fakelist")) {
					return false;
				}

				Storage<UUID, String, String> storage = getCoreManager().getSkinManager().getInFake();
				commandSender.sendMessage("§bUsuários utilizando fake no momento:");
				for (Object object : storage.keyToArray()) {
					if (!(object instanceof UUID))
						continue;

					UUID uuid = (UUID) object;
					commandSender.sendMessage("§b" + storage.getValue(uuid) + "§f é §b" + storage.getSubValue(uuid));
				}
			}

			if (nick.length() >= 16) {
				player.sendMessage("§cSeu nick tem mais de 16 caracteres.");
				return false;
			}

			if (!getCoreManager().getSkinManager().validString(nick)) {
				player.sendMessage("§cSeu nick é inválido.");
				return false;
			}

			if (getCoreManager().getSkinManager().usingFake(player.getUniqueId())) {
				player.sendMessage("§cUse o comando /fake # para resetar seu nick.");
				return false;
			}

			if (getCoreManager().getSkinManager().nickInUse(nick)) {
				player.sendMessage("§cEsse nick já está sendo utilizado.");
				return false;
			}

			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.getName().toLowerCase().equals(nick.toLowerCase())) {
					player.sendMessage("§cUm player com esse nick está online!");
					return true;
				}
			}

			if (nick.equalsIgnoreCase("list")) {
				return false;
			}

			if (getCoreManager().getSkinManager().isPremium(nick)) {
				player.sendMessage("§cVocê não pode escolher esse nickname, escolha outro!");
				return false;
			}

			getCoreManager().getSkinManager().putPlayerInFake(player, nick);
			getCoreManager().getSkinManager().fakePlayer(player, entityPlayer, nick, false);

			PlayerSkinFakeChange event = new PlayerSkinFakeChange(player);
			Bukkit.getPluginManager().callEvent(event);

			if (getCoreManager().getServerType().equals(ServerType.HG)) {
				for (Player players : Bukkit.getOnlinePlayers()) {
					players.showPlayer(player);
				}
			}

			return true;
		}

		return false;
	}

}
