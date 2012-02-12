package net.betterverse.chest.commands;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.betterverse.chest.ChestPlugin;
import net.betterverse.chest.Workbench;

public class WorkbenchCommand implements CommandExecutor {
	private final ChestPlugin plugin;

	public WorkbenchCommand(ChestPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player)sender;
			if (player.hasPermission("ac.workbench")) {
				EntityPlayer eh = ((CraftPlayer)sender).getHandle();

				eh.netServerHandler.sendPacket(new Packet100OpenWindow(1, 1, "Virtual Crafting", 9));
				eh.activeContainer = new Workbench(eh, 1);
			} else {
				plugin.tell(player, ChestPlugin.TellType.Warning, "You're not allowed to use this command.");
			}
			return true;
		}
		return false;
	}
}
