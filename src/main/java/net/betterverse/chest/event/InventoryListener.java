package net.betterverse.chest.event;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;

import net.betterverse.chest.ChestPlugin;

public class InventoryListener implements Listener {
	private ChestPlugin plugin;

	public InventoryListener(Plugin plugin) {
		this.plugin = (ChestPlugin)plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void HandleChestClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().equals("Premium Chest")) {
			plugin.getChestManager().savePlayer(e.getPlayer().getName());
		}
	}
}
