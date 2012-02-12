package net.betterverse.chest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.IIOException;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityChest;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.plugin.Plugin;

public class ChestManager {
	private ChestPlugin plugin;
	private final HashMap<String, InventoryLargeChest> chests;
	private final File dataFolder;

	public ChestManager(Plugin plugin) {
		this.plugin = (ChestPlugin) plugin;
		this.dataFolder = new File(plugin.getDataFolder(),"chests/");
		this.chests = new HashMap<String, InventoryLargeChest>();
	}

	public InventoryLargeChest getChest(String playerName) {
		InventoryLargeChest chest = (InventoryLargeChest)this.chests.get(playerName.toLowerCase());

		if (chest == null) {
			chest = addChest(playerName);
		}
		return chest;
	}

	private InventoryLargeChest addChest(String playerName) {
		InventoryLargeChest chest = new InventoryLargeChest("Premium Chest", new TileEntityChest(), new TileEntityChest());
		this.chests.put(playerName.toLowerCase(), chest);
		return chest;
	}

	public void removeChest(String playerName) {
		this.chests.remove(playerName.toLowerCase());
	}
	
	public void convert() {
		this.chests.clear();

		
		int loadedChests = 0;

		this.dataFolder.mkdirs();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".chest");
			}
		};
		for (File chestFile : this.dataFolder.listFiles(filter)) {
			try {
				InventoryLargeChest chest = new InventoryLargeChest("Premium Chest", new TileEntityChest(), new TileEntityChest());
				String playerName = chestFile.getName().substring(0, chestFile.getName().length() - 6);

				BufferedReader in = new BufferedReader(new FileReader(chestFile));

				int field = 0;
				String line;
				while ((line = in.readLine()) != null)
				{
					
					if (line != "") {
						String[] parts = line.split(":");
						try {
							int type = Integer.parseInt(parts[0]);
							int amount = Integer.parseInt(parts[1]);
							short damage = Short.parseShort(parts[2]);
							if (type != 0)
								chest.setItem(field, new ItemStack(type, amount, damage));
						}
						catch (NumberFormatException localNumberFormatException)
						{
						}
						field++;
					}
				}
				
				in.close();
				this.chests.put(playerName.toLowerCase(), chest);
				savePlayer(playerName);
				File renamedFile = new File(chestFile.getAbsolutePath()+".old");
				if (!chestFile.renameTo(renamedFile)) throw new IIOException("Can't rename old chest file!");
				
				loadedChests++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		plugin.log("Converted " + loadedChests + " chests from the old format!");
	}

	public void load() {
		int loadedChests = 0;
		this.chests.clear();
		this.dataFolder.mkdirs();
		
		convert();
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".yml");
			}
		};
		
		for (File chestFile : this.dataFolder.listFiles(filter)) {
			InventoryLargeChest chest = new InventoryLargeChest("Premium Chest", new TileEntityChest(), new TileEntityChest());

			YamlConfiguration in = YamlConfiguration.loadConfiguration(chestFile);
			
			Map<String,Object> slots = in.getValues(false);
			for (String slot : slots.keySet())
			{
				try {
					org.bukkit.inventory.ItemStack theItem = in.getItemStack(slot);
					if (theItem == null) continue;
					
					ItemStack loadItem = new CraftItemStack(theItem).getHandle();
					
					
					chest.setItem(Integer.valueOf(slot.split("-")[1]),loadItem);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			this.chests.put(chestFile.getName().split("\\.")[0].toLowerCase(), chest);
			loadedChests++;
		}

		plugin.log("Loaded " + loadedChests + " chests");
	}
	
	public void save(Boolean isAuto) {
		int savedChests = 0;

		for (String playerName : this.chests.keySet()) {
			if (savePlayer(playerName)) {
				savedChests++;
			}
		}

		if (!isAuto) {
			plugin.log("Saved " + savedChests + " chests");
		} else {
			plugin.log("Auto-saved " + savedChests + " chests");
		}
	}

	public boolean savePlayer(String player) {
		this.dataFolder.mkdirs();
		InventoryLargeChest chest = (InventoryLargeChest)this.chests.get(player);
		if (chest == null) return false;
		
		try {
			
			YamlConfiguration out = new YamlConfiguration();
			
			int slot = 0;
			for (ItemStack stack : chest.getContents()) {
				if (stack != null) {
					out.set("slot-"+Integer.toString(slot), new CraftItemStack(stack));
				}
				slot++;
			}
			
			out.save(new File(this.dataFolder+File.separator+player+".yml"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
