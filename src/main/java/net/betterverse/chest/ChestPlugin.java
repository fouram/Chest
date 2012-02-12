package net.betterverse.chest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import net.betterverse.chest.commands.ChestCommands;
import net.betterverse.chest.commands.WorkbenchCommand;
import net.betterverse.chest.event.InventoryListener;

public class ChestPlugin extends JavaPlugin {
	private PluginDescriptionFile pdf;
	private static final Logger logger = Logger.getLogger("Minecraft");
	private ChestManager chestManager;
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

		new InventoryListener(this);

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
		
		this.chestManager.save(false);

		log("Version " + this.pdf.getVersion() + " disabled");
	}

	public ChestManager getChestManager() {
		return this.chestManager;
	}

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
