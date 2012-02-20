package net.betterverse.chest.commands;

import net.minecraft.server.EntityPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.betterverse.chest.ChestManager;
import net.betterverse.chest.ChestPlugin;

public class ChestCommands implements CommandExecutor {
	private final ChestPlugin plugin;
	private final ChestManager chestManager;

	public ChestCommands(ChestPlugin plugin) {
		this.plugin = plugin;
		this.chestManager = ((ChestPlugin)plugin).getChestManager();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String name = command.getName();
		
		if (name.equalsIgnoreCase("savechests")) {
			return performSaveChestsCommand(sender, args);
		}
		if (name.equalsIgnoreCase("chestreload")) {
			return performChestConfigReloadCommand(sender, args);
		}
		
		if ((sender instanceof Player)) {
			String w = ((Player)sender).getWorld().getName();
			if (!this.plugin.worlds.contains(w)) {
				return true;
			}
		}
		
		if (name.equals("chest")) {
			return performChestCommand(sender, args);
		}
		if (name.equalsIgnoreCase("clearchest")) {
			return performClearChestCommand(sender, args);
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
					plugin.tell(player, ChestPlugin.TellType.Warning, "You're not allowed to use this command.");
				}
				return true;
			}
			if (args.length == 0) {
				if (player.hasPermission("ac.chest")) {
					EntityPlayer eh = ((CraftPlayer)sender).getHandle();
					eh.a(this.chestManager.getChest(player.getName()));
				} else {
					plugin.tell(player, ChestPlugin.TellType.Warning, "You're not allowed to use this command.");
				}
				return true;
			}
		}
		return false;
	}

	private boolean performClearChestCommand(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			if (((sender instanceof Player)) && (! ((Player)sender).hasPermission("ac.admin"))) {
				plugin.tell(sender, ChestPlugin.TellType.Warning, "You're not allowed to clear other user's chests.");
				return true;
			}
			this.chestManager.removeChest(args[0]);
			plugin.tell(sender, ChestPlugin.TellType.Success, "Successfully cleared " + args[0] + "'s chest.");
			return true;
		}
		if ((sender instanceof Player)) {
			Player player = (Player)sender;
			if (!player.hasPermission("ac.chest")) {
				plugin.tell(player, ChestPlugin.TellType.Warning, "You're not allowed to use this command.");
			} else {
				this.chestManager.removeChest(player.getName());
				plugin.tell(player, ChestPlugin.TellType.Success, "Successfully cleared your chest.");
			}
			return true;
		}

		return false;
	}

	private boolean performSaveChestsCommand(CommandSender sender, String[] args) {
		if ((sender instanceof Player) && (!((Player)sender).hasPermission("ac.save"))) {
			plugin.tell(sender, ChestPlugin.TellType.Warning, "You do not have permissions to use that command.");
			plugin.log(((Player)sender).getName()+" attempted to use \"savechests\" command (no permision)");
			return true;
		}

		this.chestManager.save(false);
		
		if (sender instanceof Player) {
			plugin.tell(sender, ChestPlugin.TellType.Success, "Saved all chests.");
			plugin.log(((Player)sender).getName()+" force-saved all chests.");
		} else if (sender instanceof ConsoleCommandSender) {
			plugin.log("CONSOLE force-saved all chests.");
		} else {
			// Future-proofing
			Class<? extends CommandSender> c = sender.getClass();
			plugin.log(c.getName() + " force-saved all chests.");
		}
		return true;
	}
	
	private boolean performChestConfigReloadCommand(CommandSender sender, String[] args) {
		if ( (sender instanceof Player) && (!((Player)sender).hasPermission("ac.admin"))) {
			plugin.tell(sender, ChestPlugin.TellType.Warning, "You do not have permissions to use that command.");
			plugin.log(((Player)sender).getName()+" attempted to use \"chestreload\" command (no permision)");
			return true;
		}
		
		plugin.reload();
		
		if (sender instanceof Player) {
			plugin.tell(sender, ChestPlugin.TellType.Success, "Configuration file reloaded.");
			plugin.log(((Player)sender).getName()+" reloaded configuration file.");
		} else if (sender instanceof ConsoleCommandSender) {
			plugin.log("CONSOLE reloaded configuration file.");
		} else {
			// Future-proofing
			Class<? extends CommandSender> c = sender.getClass();
			plugin.log(c.getName() + " reloaded configuration file.");
		}
		return true;
	}
}
