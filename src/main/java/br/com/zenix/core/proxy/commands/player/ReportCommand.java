package br.com.zenix.core.proxy.commands.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableSet;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportCommand extends ProxyCommand {

	public static final ArrayList<UUID> coowdownReport = new ArrayList<>();

	public ReportCommand() {
		super("report");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return;
		}

		if (args.length < 2) {
			commandSender.sendMessage("§aUse: /report <player> <motivo>");
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

		if (inCoowdown(proxiedPlayer)) {
			commandSender.sendMessage("§cAguarde para poder enviar outro report.");
			return;
		}

		ProxiedPlayer reportedPlayer = ProxyServer.getInstance().getPlayer(args[0]);

		if (!isPlayer(reportedPlayer)) {
			commandSender.sendMessage("§cO player citado está offline.");
			return;
		}

		String motive = getArgs(args, 1);
		String motiveSave = getArgs(args, 1).toLowerCase();

		if (motiveSave.contains("staff") || motiveSave.contains("lixo") || motiveSave.contains("cade")
				|| motiveSave.contains("buceta") || motiveSave.contains("desgraçado") || motiveSave.contains("caralho")
				|| motiveSave.contains("PVP") || motiveSave.contains("mds") || motiveSave.contains("olha")
				|| motiveSave.contains("muito") || motiveSave.contains("'-") || motiveSave.contains("mush")
				|| motiveSave.contains("ajuda") || motiveSave.contains("blatant") || motiveSave.contains("fudido")
				|| motiveSave.contains("tela") || motiveSave.contains("evento") || motiveSave.contains("'-")
				|| motiveSave.contains("bosta") || motiveSave.contains("fudidao") || motiveSave.contains("vsf")
				|| motiveSave.contains("hack") || motiveSave.contains("telar")) {
			commandSender.sendMessage("§cA sua mensagem de report está em um filtro de restrição.");
			return;
		}

		for (ProxiedPlayer proxiedPlayers : ProxyServer.getInstance().getPlayers()) {
			if (hasAccount(getProxyManager().getAccountManager().getAccount(proxiedPlayers.getUniqueId()))) {
				if (!getProxyManager().getAccountManager().getAccount(proxiedPlayers.getUniqueId()).isSilent()) {
					TextComponent component = new TextComponent("§cClique para CONECTAR ao SERVIDOR");
					TextComponent componentReport = new TextComponent("§c§lREPORT §fO player "
							+ reportedPlayer.getName() + " foi REPORTADO!");
					if (proxiedPlayers.getServer().getInfo().getName() == reportedPlayer.getServer().getInfo()
							.getName()) {
						component.setClickEvent(
								new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + reportedPlayer.getName()));
						componentReport.setClickEvent(
								new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + reportedPlayer.getName()));
					} else {
						component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/server " + reportedPlayer.getServer().getInfo().getName()));
						componentReport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/server " + reportedPlayer.getServer().getInfo().getName()));
					}
					component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder("§cClique para ir até o servidor").create()));
					componentReport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder("§c§lREPORT\n\n§fPlayer: §c" + reportedPlayer.getName() + "\n"
									+ "§fMotivo: §c" + motive.toLowerCase() + "\n" + "§fReporter: §c"
									+ proxiedPlayer.getName() + "\n" + "§fServer: §c"
									+ reportedPlayer.getServer().getInfo().getName() + "\n").create()));
					proxiedPlayers.sendMessage(componentReport);
				}
			}
		}
		proxiedPlayer.sendMessage("§aO seu report sobre o player '§f" + reportedPlayer.getName() + "§a' foi enviado!");
		addCooldown(proxiedPlayer);
	}

	public void addCooldown(final ProxiedPlayer proxiedPlayer) {
		coowdownReport.add(proxiedPlayer.getUniqueId());
		ProxyServer.getInstance().getScheduler().schedule(getProxyManager().getPlugin(), new Runnable() {
			public void run() {
				coowdownReport.remove(proxiedPlayer.getUniqueId());
			}
		}, 80, TimeUnit.SECONDS);
	}

	public boolean inCoowdown(ProxiedPlayer proxiedPlayer) {
		return coowdownReport.contains(proxiedPlayer.getUniqueId());
	}

	public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
		if ((args.length > 2) || (args.length == 0)) {
			return ImmutableSet.of();
		}
		Set<String> match = new HashSet<>();
		if (args.length == 1) {
			String search = args[0].toLowerCase();
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.getName().toLowerCase().startsWith(search)) {
					match.add(player.getName());
				}
			}
		}
		return match;
	}

}