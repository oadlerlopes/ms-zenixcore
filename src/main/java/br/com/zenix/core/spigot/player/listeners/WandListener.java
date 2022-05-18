package br.com.zenix.core.spigot.player.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.zenix.core.spigot.commands.base.BukkitListener;
import br.com.zenix.core.spigot.player.item.ItemBuilder;

public class WandListener extends BukkitListener {

	private static final ItemBuilder itemBuilder = new ItemBuilder(Material.AIR);

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (itemBuilder.checkItem(event.getItem(), "§aWand")) {
			if (event.getPlayer().hasPermission("commons.cmd.event")) {
				if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
					Block clicked = event.getClickedBlock();
					getCoreManager().getWorldEditManager().setFirstPosition(event.getPlayer().getUniqueId(),
							clicked.getLocation());
					event.getPlayer()
							.sendMessage("§aA primeira localização foi setada §f(" + clicked.getLocation().getBlockX()
									+ "," + clicked.getLocation().getBlockY() + "," + clicked.getLocation().getBlockZ()
									+ ")");

				} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

					Block clicked = event.getClickedBlock();
					getCoreManager().getWorldEditManager().setSecondPosition(event.getPlayer().getUniqueId(),
							clicked.getLocation());
					event.getPlayer()
							.sendMessage("§aA segunda localização foi setada §f(" + clicked.getLocation().getBlockX()
									+ "," + clicked.getLocation().getBlockY() + "," + clicked.getLocation().getBlockZ()
									+ ")");
				}
				event.setCancelled(true);
			}
		}
	}
}
