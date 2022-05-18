package br.com.zenix.core.spigot.commands.player.clan;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.clan.Clan;
import br.com.zenix.core.spigot.player.clan.groups.ClanHierarchy;
import br.com.zenix.core.spigot.player.clan.player.ClanAccount;

public class ClanCommand extends BukkitCommand {

	public ClanCommand() {
		super("clan");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		Player player = (Player) commandSender;
		Account account = getCoreManager().getAccountManager().getAccount(player);
		UUID uuid = player.getUniqueId();

		if (args.length <= 0) {
			player.sendMessage("§6§l ");
			if (!account.isHaveClan()) {
				player.sendMessage("§3§l/clan criar §f<Nome do Clan> <Sigla> - Você pode criar um Clan.");
				player.sendMessage(
						"§3§l/clan join §f<§fNome do Clan> - Você pode entrar em um Clan que alguém tenha criado e convidou você.");
			} else {
				if (getCoreManager().getClanAccountManager().getClanAccount(player)
						.getClanHierarchy() == ClanHierarchy.OWNER) {
					player.sendMessage(
							"§3§l/clan setImage §f<Imgur Link> - Você pode alterar o link da imagem do (https://zenix.cc/clanprofile/"
									+ getCoreManager().getClanAccountManager().getClanAccount(player).getClan()
											.getName());
					player.sendMessage("§3§l/clan delete §f<§fNome do Clan> - Você saiu do seu Clan e o deleta.");
				}

				if (getCoreManager().getClanAccountManager().getClanAccount(player).isStaff()) {
					player.sendMessage("§3§l/clan promote §f<Player> - Você pode promover um membro de cargo.");
					player.sendMessage("§3§l/clan demote §f<Player> - Você pode rebaixar um membro de cargo.");
					player.sendMessage("§3§l/clan invite §f<Player> - Você pode convidar jogadores para o clã.");
					player.sendMessage("§3§l/clan kick §f<Player> - Você pode expulsar jogadores do clã.");
				}

				player.sendMessage("§3§l/clan chat §f- Você entra no chat da clã.");
				player.sendMessage(
						"§3§l/clan leave §f<§fNome do Clan> - Você sai do seu Clan e estará disponível para entrar em outro novamente.");
			}
		}

		if (args.length >= 1) {
			switch (args[0].toLowerCase()) {
			case "chat":
				if (account.isHaveClan()) {
					account.setClanChat(!account.isClanChat());
					player.sendMessage("§cAgora você" + (account.isClanChat() ? "está" : "não está")
							+ "falando no chat da sua clã!");
				} else {
					player.sendMessage("§cVocê não tem nenhum clan para entrar no chat.");
				}
				return true;
			case "setimage":
				if (account.isHaveClan()) {

					if (args.length != 2) {
						player.sendMessage("§aUse: §f/clan setimage <linkimgur>");
						return false;
					}

					if (getCoreManager().getClanAccountManager().getClanAccount(player)
							.getClanHierarchy() == ClanHierarchy.OWNER) {

						if (!args[1].startsWith("https://i.imgur.com/")) {
							player.sendMessage("§cÉ necessário que a imagem seja upada no imgur! Exemplo> /clan setimage https://i.imgur.com/exemplo");
							return true;
						} else {
							player.sendMessage("§aA imagem foi setada com sucesso.");
							player.sendMessage(
									"§c(Aviso) Imagens com pornografia, citação de outros servidores ou que tentam atacar algum pessoa resultarão em exclusão do clã e banimento permanente do dono da mesma.");

							getCoreManager().getClanManager().setClanImage(
									getCoreManager().getClanAccountManager().getClanAccount(player).getClan().getName(),
									args[1]);
						}

					}
				} else {
					player.sendMessage("§cVocê não tem nenhum clan para entrar setar imagem.");
				}
				return true;
			case "kick":
				if (account.isHaveClan()) {
					Player target = Bukkit.getPlayerExact(args[1]);

					if (args.length != 2) {
						player.sendMessage("§aUse: §f/clan kick <player>");
						return false;
					}

					getCoreManager().getClanAccountManager().getClanAccount(player).getClan().kick(player, target);
				} else {
					player.sendMessage("§cVocê não tem nenhum clan para kickar alguém.");
				}
				return true;
			case "promote":
				if (account.isHaveClan()) {
					Player target = Bukkit.getPlayerExact(args[1]);

					if (args.length != 2) {
						player.sendMessage("§aUse: §f/clan promote <player>");
						return false;
					}

					if (target == null) {
						sendOfflinePlayerMessage(commandSender, args[1]);
						return false;
					}

					getCoreManager().getClanAccountManager().getClanAccount(player).getClan().promote(player, target);
				} else {
					player.sendMessage("§cVocê não tem nenhum clan para promover alguém.");
				}
				return true;
			case "invite":
				if (account.isHaveClan()) {
					Player target = Bukkit.getPlayerExact(args[1]);

					if (args.length != 2) {
						player.sendMessage("§aUse: §f/clan invite <player>");
						return false;
					}

					if (target == null) {
						sendOfflinePlayerMessage(commandSender, args[1]);
						return false;
					}

					getCoreManager().getClanAccountManager().getClanAccount(player).getClan().invite(player, target);
				} else {

					player.sendMessage("§cVocê não tem nenhum clan para promover alguém.");
				}
				return true;
			case "join":
				if (account.isHaveClan()) {
					player.sendMessage("§cVocê já está em um clã!");
					return false;
				}

				if (!getCoreManager().getClanManager().getInvites().containsKey(player.getUniqueId())) {
					player.sendMessage("§cVocê não tem nenhum convite para entrar!");
					return false;
				}

				if (getInvite(player.getUniqueId(), args[1])) {
					getCoreManager().getClanManager().getInvites().remove(player.getUniqueId());
					getCoreManager().getClanManager().joinClan(
							new Clan(args[1], getCoreManager().getClanManager().getClanTag(args[1]), 1),
							player.getUniqueId(), ClanHierarchy.MEMBER);
					account.setHaveClan(true);

					account.updatePlayer(player);

					player.sendMessage("§7Você agora entrou para o clan §c" + args[1] + "§7"
							+ "! Seja bem vindo á um novo reino de batalhas!");
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0F, 10.0F);
					return false;
				}

				player.sendMessage("§cNão há nenhum convite registrado.");
				return true;

			case "delete":
				if (args.length != 2) {
					player.sendMessage("§aUse: §f/clan delete <name>");
					return false;
				}

				String name = args[1];

				if (account.isHaveClan()) {

					String clanName = getCoreManager().getClanAccountManager().getClanAccount(player).getClan()
							.getName();

					if (name.equalsIgnoreCase(clanName)) {
						getCoreManager().getClanAccountManager().getClanAccount(player).getClan().delete(player,
								clanName);
					} else {
						player.sendMessage("§cEsse clan não é seu!");
					}
				} else {
					player.sendMessage("§cVocê não tem nenhum clan para deletar.");
				}
				return true;

			case "leave":
				if (account.isHaveClan()) {
					getCoreManager().getClanAccountManager().getClanAccount(player).getClan().leave(player);
				} else {
					player.sendMessage("§cVocê não tem nenhum clan para sair.");
				}
				return true;
			case "create":
				if (getCoreManager().getClanManager().checkPlayer(uuid)) {
					player.sendMessage("§cVocê já tem um clan!");
					return false;
				}

				if ((args.length < 3) || (args.length == 1)) {
					player.sendMessage("§aUse: §f/clan criar <nome> <tag>");
					return false;
				}

				final String namec = args[1];
				final String tag = args[2];

				if (getCoreManager().getClanManager().checkClan(namec)) {
					player.sendMessage("§cClan já existente!");
					return false;
				}

				if (getCoreManager().getClanManager().checkTag(tag)) {
					player.sendMessage("§cTag já existente!");
					return false;
				}

				if (!validateName(namec)) {
					player.sendMessage("§cO nome não deve ter simbolos e ter até 16 caracteres!");
					return false;
				}

				if (!validateTag(tag)) {
					player.sendMessage("§cAlgum problema com sua tag.");
					return false;
				}

				new BukkitRunnable() {
					public void run() {
						getCoreManager().getClanManager().createClan(new Clan(namec, tag, 1));
						getCoreManager().getClanManager().joinClan(new Clan(namec, tag, 1), player.getUniqueId(),
								ClanHierarchy.OWNER);
						getCoreManager().getClanAccountManager().addClanAccount(new ClanAccount(player));
						
					    account.getDataHandler().getValue(DataType.CLAN_DEATH).setValue(0);
					    account.getDataHandler().getValue(DataType.CLAN_ELO).setValue(0);
					    account.getDataHandler().getValue(DataType.CLAN_KILL).setValue(0);
					    account.getDataHandler().getValue(DataType.CLAN_WINS).setValue(0);
					    account.getDataHandler().getValue(DataType.CLAN_XP).setValue(0);
					    account.getDataHandler().update(DataType.CLAN_KILL);
					    account.getDataHandler().update(DataType.CLAN_ELO);
					    account.getDataHandler().update(DataType.CLAN_WINS);
					    account.getDataHandler().update(DataType.CLAN_XP);
					    account.getDataHandler().update(DataType.CLAN_DEATH);
						account.updatePlayer(player);
						player.sendMessage("§aVocê criou o seu clã §f'" + namec + "' §a com a TAG §f'" + tag + "'§a.");
					}
				}.runTaskAsynchronously(getCoreManager().getPlugin());
				return true;
			case "criar":
				if (getCoreManager().getClanManager().checkPlayer(uuid)) {
					player.sendMessage("§cVocê já tem um clan!");
					return false;
				}

				if ((args.length < 3) || (args.length == 1)) {
					player.sendMessage("§aUse: §f/clan criar <nome> <tag>");
					return false;
				}

				final String namec2 = args[1];
				final String tag2 = args[2];

				if (getCoreManager().getClanManager().checkClan(namec2)) {
					player.sendMessage("§cClan já existente!");
					return false;
				}

				if (getCoreManager().getClanManager().checkTag(tag2)) {
					player.sendMessage("§cTag já existente!");
					return false;
				}

				if (!validateName(namec2)) {
					player.sendMessage("§cO nome não deve ter simbolos e ter até 16 caracteres!");
					return false;
				}

				if (!validateTag(tag2)) {
					player.sendMessage("§cAlgum problema com sua tag.");
					return false;
				}

				new BukkitRunnable() {
					public void run() {
						getCoreManager().getClanManager().createClan(new Clan(namec2, tag2, 1));
						getCoreManager().getClanManager().joinClan(new Clan(namec2, tag2, 1), player.getUniqueId(),
								ClanHierarchy.OWNER);
						getCoreManager().getClanAccountManager().addClanAccount(new ClanAccount(player));

						account.updatePlayer(player);
						player.sendMessage(
								"§aVocê criou o seu clã §f'" + namec2 + "' §a com a TAG §f'" + tag2 + "'§a.");
					}
				}.runTaskAsynchronously(getCoreManager().getPlugin());
				return true;
			}
		}
		return true;
	}

	public boolean getInvite(UUID uuid, String string) {
		CoreManager core = Core.getCoreManager();

		if (!core.getClanManager().getInvites().containsKey(uuid)) {
			return false;
		}

		return ((String) core.getClanManager().getInvites().get(uuid)).toString().equalsIgnoreCase(string.toString());
	}

	public static boolean validateName(String name) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{1,16}");
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}

	public static boolean validateTag(String tag) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{1,5}");
		Matcher matcher = pattern.matcher(tag);
		return matcher.matches();
	}
}
