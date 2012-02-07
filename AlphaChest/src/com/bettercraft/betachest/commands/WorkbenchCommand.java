package com.bettercraft.betachest.commands;

import com.bettercraft.betachest.BetaChestPlugin;
import com.bettercraft.betachest.BetaWorkbench;
import com.bettercraft.betachest.Teller;
import com.bettercraft.betachest.Teller.Type;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet100OpenWindow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class WorkbenchCommand
implements CommandExecutor
{
	private final BetaChestPlugin plugin;

	public WorkbenchCommand(BetaChestPlugin plugin)
	{
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player)sender;
			if (player.hasPermission("ac.workbench")) {
				EntityPlayer eh = ((CraftPlayer)sender).getHandle();

				int windowId = 1;
				eh.netServerHandler.sendPacket(new Packet100OpenWindow(1, 1, "Virtual Crafting", 9));
				eh.activeContainer = new BetaWorkbench(eh, 1);
			} else {
				Teller.tell(player, Teller.Type.Warning, "You're not allowed to use this command.");
			}
			return true;
		}
		return false;
	}
}

