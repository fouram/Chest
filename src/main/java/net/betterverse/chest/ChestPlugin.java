package net.betterverse.chest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import net.betterverse.chest.commands.ChestCommands;
import net.betterverse.chest.commands.WorkbenchCommand;
import net.betterverse.chest.event.InventoryListener;

public class ChestPlugin extends JavaPlugin {
	private PluginDescriptionFile pdf;
	private static final Logger logger = Logger.getLogger("Minecraft");
	private ChestManager chestManager;
	private int autosaveTask = 0, autosaveInterval = 0;
	public List<String> worlds;
	

	public void log(String message) {
		logger.info("[" + pdf.getName() + "] "+message);
	}

	public void onEnable() {
		
		this.pdf = getDescription();

		new InventoryListener(this);

		this.chestManager = new ChestManager(this);
		this.chestManager.load();

		ChestCommands chestCommands = new ChestCommands(this);
		getCommand("chest").setExecutor(chestCommands);
		getCommand("clearchest").setExecutor(chestCommands);
		getCommand("savechests").setExecutor(chestCommands);
		getCommand("chestreload").setExecutor(chestCommands);
		getCommand("workbench").setExecutor(new WorkbenchCommand(this));

		// If config.yml does not specify autosave, default to 10. Specify 0 to disable.
		startAutosaveTask(getConfig().getInt("autosave", 10));

		this.worlds = getConfig().getStringList("worlds");

		log("Extra enhancements for Betterverse by 4am");
		log("Version " + this.pdf.getVersion() + " enabled");
	}
	
	private void startAutosaveTask(int interval) {
		if (interval == 0) {
			this.autosaveInterval = 0;
			this.autosaveTask = 0;
			return;
		}
		
		this.autosaveInterval = interval * 3000;
		this.autosaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				ChestPlugin.this.chestManager.save(true);
			}
		}
		, this.autosaveInterval, this.autosaveInterval);
	}
	
	public void reload() {
		
		reloadConfig();
		
		if (autosaveTask != 0) {
			getServer().getScheduler().cancelTask(autosaveTask);
			startAutosaveTask(getConfig().getInt("autosave", 10));
		}
		this.worlds = getConfig().getStringList("worlds");
		
	}

	public void onDisable() {
		
		getServer().getScheduler().cancelTasks(this);
		this.chestManager.save(false);

		log("Version " + this.pdf.getVersion() + " disabled");
	}

	public ChestManager getChestManager() {
		return this.chestManager;
	}

	@SuppressWarnings("unused")
	private List<String> getOps() {
		ArrayList<String> ops = new ArrayList<String>();
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
	
	public void tell(CommandSender sender, TellType type, String message) {
		ChatColor color = ChatColor.WHITE;
		switch (type) {
		case Error:
			color = ChatColor.WHITE;
			break;
		case Info:
			color = ChatColor.DARK_GREEN;
			break;
		case Misc:
			color = ChatColor.GOLD;
			break;
		case Success:
			color = ChatColor.DARK_RED;
			break;
		case Warning:
			color = ChatColor.DARK_BLUE;
		}

		sender.sendMessage(ChatColor.BLACK + "[" + ChatColor.GRAY + this.pdf.getName() + ChatColor.BLACK + "] " + color + message);
	}

	public static enum TellType {
		Info, Success, Warning, Error, Misc;
	}
}
