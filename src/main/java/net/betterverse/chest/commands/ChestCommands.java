package net.betterverse.chest.commands;

import java.util.List;

import net.minecraft.server.EntityPlayer;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.betterverse.chest.ChestManager;
import net.betterverse.chest.ChestPlugin;
import net.betterverse.chest.Teller;
import net.betterverse.chest.Teller.Type;

public class ChestCommands implements CommandExecutor {
	private final ChestPlugin plugin;
	private final ChestManager chestManager;

	public ChestCommands(ChestPlugin plugin) {
		this.plugin = plugin;
		this.chestManager = ((ChestPlugin)plugin).getChestManager();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((sender instanceof Player)) {
			String w = ((Player)sender).getWorld().getName();
			if (!this.plugin.worlds.contains(w)) {
				return true;
			}
		}
		String name = command.getName();
		if (name.equals("chest")) {
			return performChestCommand(sender, args);
		}
		if (name.equalsIgnoreCase("clearchest")) {
			return performClearChestCommand(sender, args);
		}
		if (name.equalsIgnoreCase("savechests")) {
			return performSaveChestsCommand(sender, args);
		}
		return false;
	}

	private boolean performChestCommand(CommandSender sender, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player)sender;

			if (args.length == 1) {
				if (player.hasPermission("ac.admin")) {
					EntityPlayer eh = ((CraftPlayer)sender).getHandle();
					eh.a(this.chestManager.getChest(args[0]));
				} else {
					Teller.tell(player, Teller.Type.Warning, "You're not allowed to use this command.");
				}
				return true;
			}
			if (args.length == 0) {
				if (player.hasPermission("ac.chest")) {
					EntityPlayer eh = ((CraftPlayer)sender).getHandle();
					eh.a(this.chestManager.getChest(player.getName()));
				} else {
					Teller.tell(player, Teller.Type.Warning, "You're not allowed to use this command.");
				}
				return true;
			}
		}
		return false;
	}

	private boolean performClearChestCommand(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			if (((sender instanceof Player)) && (! ((Player)sender).hasPermission("ac.admin"))) {
				Teller.tell(sender, Teller.Type.Warning, "You're not allowed to clear other user's chests.");
				return true;
			}
			this.chestManager.removeChest(args[0]);
			Teller.tell(sender, Teller.Type.Success, "Successfully cleared " + args[0] + "'s chest.");
			return true;
		}
		if ((sender instanceof Player)) {
			Player player = (Player)sender;
			if (!player.hasPermission("ac.chest")) {
				Teller.tell(player, Teller.Type.Warning, "You're not allowed to use this command.");
			} else {
				this.chestManager.removeChest(player.getName());
				Teller.tell(player, Teller.Type.Success, "Successfully cleared your chest.");
			}
			return true;
		}

		return false;
	}

	private boolean performSaveChestsCommand(CommandSender sender, String[] args) {
		if (((sender instanceof Player)) &&
				(!((Player)sender).hasPermission("ac.save"))) {
			Teller.tell(sender, Teller.Type.Warning, "You're not allowed to use this command.");
			return true;
		}

		this.chestManager.save(false);
		Teller.tell(sender, Teller.Type.Success, "Saved all chests.");
		return true;
	}
}
