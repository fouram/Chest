package com.bettercraft.betachest;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Teller
{
	public static void tell(CommandSender sender, Type type, String message)
	{
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

		sender.sendMessage(ChatColor.BLACK + "[" + ChatColor.GRAY + "Alpha Chest" + ChatColor.BLACK + "] " + color + message);
	}

	public static enum Type
	{
		Info, Success, Warning, Error, Misc;
	}
}
