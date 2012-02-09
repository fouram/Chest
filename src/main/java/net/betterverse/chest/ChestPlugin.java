package net.betterverse.chest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.config.Configuration;

import net.betterverse.chest.commands.ChestCommands;
import net.betterverse.chest.commands.WorkbenchCommand;
import net.betterverse.chest.event.InventoryListener;

public class ChestPlugin extends JavaPlugin {
	private PluginDescriptionFile pdf;
	private static final Logger logger = Logger.getLogger("Minecraft");
	private ChestManager chestManager;
	private InventoryListener il;
	public List<String> worlds;

	public void log(String message) {
		logger.info("[" + pdf.getName() + "] "+message);
	}

	public void onEnable() {
		this.pdf = getDescription();
		Configuration config = getConfiguration();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			ArrayList admincmds = new ArrayList();
			admincmds.add("ac.admin");
			admincmds.add("ac.save");
			admincmds.add("ac.reload");

			config.setProperty("admincmds", admincmds);
			config.setProperty("admins", getOps());

			ArrayList w = new ArrayList();
			w.add("world");
			w.add("world_nether");
			config.setProperty("worlds", w);

			config.setProperty("autosave", Integer.valueOf(10));

			config.save();
		}

		il = new InventoryListener(this);

		//setupPermissions();

		this.chestManager = new ChestManager(this);
		this.chestManager.load();

		ChestCommands chestCommands = new ChestCommands(this);
		getCommand("chest").setExecutor(chestCommands);
		getCommand("clearchest").setExecutor(chestCommands);
		getCommand("savechests").setExecutor(chestCommands);
		getCommand("workbench").setExecutor(new WorkbenchCommand(this));

		int autosaveInterval = config.getInt("autosave", 10) * 3000;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				ChestPlugin.this.chestManager.save(true);
			}
		}
		, autosaveInterval, autosaveInterval);

		this.worlds = config.getStringList("worlds", null);

		log("Extra enhancements for Bettercraft by 4am");
		log("Version " + this.pdf.getVersion() + " enabled");
	}

	public void onDisable() {
		//this.chestManager.save(false);

		log("Version " + this.pdf.getVersion() + " disabled");
	}

	public ChestManager getChestManager() {
		return this.chestManager;
	}

	/*private void setupPermissions() {
		if (this.permissionHandler == null) {
		  Plugin permissions = getServer().getPluginManager().getPlugin("Permissions");
		  if (permissions != null) {
			this.permissionHandler = ((Permissions)permissions).getHandler();
			} else {
			PluginDescriptionFile pdfFile = getDescription();
			log.info("[" + pdfFile.getName() + "] Permission system not enabled. Using seperate settings.");
			}
		  }
	}*/

	private List<String> getOps() {
		ArrayList ops = new ArrayList();
		try {
			BufferedReader e = new BufferedReader(new FileReader("ops.txt"));
			String s = "";
			while ((s = e.readLine()) != null) {
				if (!s.equals("")) {
					ops.add(s);
				}
			}
			e.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ops;
	}

	/*public boolean hasPermission(Player player, String permission) {
		if (this.permissionHandler != null) {
			return this.permissionHandler.has(player, permission);
		}
		Configuration config = getConfiguration();
		List admincmds = config.getStringList("admincmds", null);
		if (!admincmds.contains(permission)) {
			return true;
		}
		List admins = config.getStringList("admins", null);
		for (String admin : admins) {
			if (admin.equalsIgnoreCase(player.getName())) {
				return true;
			}
		}
		return false;
	}*/
}
