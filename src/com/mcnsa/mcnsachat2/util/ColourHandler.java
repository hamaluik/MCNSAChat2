package com.mcnsa.mcnsachat2.util;

import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

public class ColourHandler {
	static Integer nextColour = new Integer(0);
	
	// make it a static class) private constructor, static methods
	private ColourHandler() {}

	public static String translateColour(String name) {
		String colour = new String("???");
		
		// map it!
		if(name.equalsIgnoreCase("&0")) colour = "black";
		else if(name.equalsIgnoreCase("&1")) colour = "dark blue";
		else if(name.equalsIgnoreCase("&2")) colour = "dark green";
		else if(name.equalsIgnoreCase("&3")) colour = "dark teal";
		else if(name.equalsIgnoreCase("&4")) colour = "dark red";
		else if(name.equalsIgnoreCase("&5")) colour = "purple";
		else if(name.equalsIgnoreCase("&6")) colour = "gold";
		else if(name.equalsIgnoreCase("&7")) colour = "grey";
		else if(name.equalsIgnoreCase("&8")) colour = "dark grey";
		else if(name.equalsIgnoreCase("&9")) colour = "blue";
		else if(name.equalsIgnoreCase("&a")) colour = "green";
		else if(name.equalsIgnoreCase("&b")) colour = "teal";
		else if(name.equalsIgnoreCase("&c")) colour = "red";
		else if(name.equalsIgnoreCase("&d")) colour = "pink";
		else if(name.equalsIgnoreCase("&e")) colour = "yellow";
		else if(name.equalsIgnoreCase("&f")) colour = "white";
		else if(name.equalsIgnoreCase("&k")) colour = "magic";
		else if(name.equalsIgnoreCase("&l")) colour = "bold";
		else if(name.equalsIgnoreCase("&m")) colour = "strikethrough";
		else if(name.equalsIgnoreCase("&n")) colour = "underlined";
		else if(name.equalsIgnoreCase("&o")) colour = "italics";
		else if(name.equalsIgnoreCase("&r")) colour = "reset";
		
		return colour;
	}

	public static String translateName(String name) {
		// default colour will be white.
		String colour = new String("");
		
		// map it!
		if(name.equalsIgnoreCase("black")) colour = "&0";
		else if(name.equalsIgnoreCase("dblue")) colour = "&1";
		else if(name.equalsIgnoreCase("dark blue")) colour = "&1";
		else if(name.equalsIgnoreCase("dgreen")) colour = "&2";
		else if(name.equalsIgnoreCase("dark green")) colour = "&2";
		else if(name.equalsIgnoreCase("dteal")) colour = "&3";
		else if(name.equalsIgnoreCase("dark teal")) colour = "&3";
		else if(name.equalsIgnoreCase("daqua")) colour = "&3";
		else if(name.equalsIgnoreCase("dark aqua")) colour = "&3";
		else if(name.equalsIgnoreCase("dred")) colour = "&4";
		else if(name.equalsIgnoreCase("dark red")) colour = "&4";
		else if(name.equalsIgnoreCase("purple")) colour = "&5";
		else if(name.equalsIgnoreCase("dpink")) colour = "&5";
		else if(name.equalsIgnoreCase("dark pink")) colour = "&5";
		else if(name.equalsIgnoreCase("gold")) colour = "&6";
		else if(name.equalsIgnoreCase("orange")) colour = "&6";
		else if(name.equalsIgnoreCase("grey")) colour = "&7";
		else if(name.equalsIgnoreCase("gray")) colour = "&7";
		else if(name.equalsIgnoreCase("dgrey")) colour = "&8";
		else if(name.equalsIgnoreCase("dark grey")) colour = "&8";
		else if(name.equalsIgnoreCase("dgray")) colour = "&8";
		else if(name.equalsIgnoreCase("dark gray")) colour = "&8";
		else if(name.equalsIgnoreCase("blue")) colour = "&9";
		else if(name.equalsIgnoreCase("green")) colour = "&a";
		else if(name.equalsIgnoreCase("bright green")) colour = "&a";
		else if(name.equalsIgnoreCase("teal")) colour = "&b";
		else if(name.equalsIgnoreCase("aqua")) colour = "&b";
		else if(name.equalsIgnoreCase("red")) colour = "&c";
		else if(name.equalsIgnoreCase("pink")) colour = "&d";
		else if(name.equalsIgnoreCase("yellow")) colour = "&e";
		else if(name.equalsIgnoreCase("white")) colour = "&f";
		else if(name.equalsIgnoreCase("random")) colour = "&k";
		else if(name.equalsIgnoreCase("magic")) colour = "&k";
		else if(name.equalsIgnoreCase("bold")) colour = "&l";
		else if(name.equalsIgnoreCase("strike")) colour = "&m";
		else if(name.equalsIgnoreCase("strikethrough")) colour = "&m";
		else if(name.equalsIgnoreCase("underline")) colour = "&n";
		else if(name.equalsIgnoreCase("italics")) colour = "&o";
		else if(name.equalsIgnoreCase("italic")) colour = "&o";
		else if(name.equalsIgnoreCase("reset")) colour = "&r";
		
		return colour;
	}

	// allow for colour tags to be used in strings..
	public static String processColours(String str) {
		return processConsoleColours(str);
	}
	
	public static String processConsoleColours(String str) {
		str = str.replaceAll("&0", ChatColor.BLACK.toString());
		str = str.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		str = str.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		str = str.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		str = str.replaceAll("&4", ChatColor.DARK_RED.toString());
		str = str.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		str = str.replaceAll("&6", ChatColor.GOLD.toString());
		str = str.replaceAll("&7", ChatColor.GRAY.toString());
		str = str.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		str = str.replaceAll("&9", ChatColor.BLUE.toString());
		str = str.replaceAll("&a", ChatColor.GREEN.toString());
		str = str.replaceAll("&b", ChatColor.AQUA.toString());
		str = str.replaceAll("&c", ChatColor.RED.toString());
		str = str.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		str = str.replaceAll("&e", ChatColor.YELLOW.toString());
		str = str.replaceAll("&f", ChatColor.WHITE.toString());
		str = str.replaceAll("&k", ChatColor.MAGIC.toString());
		str = str.replaceAll("&l", ChatColor.BOLD.toString());
		str = str.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		str = str.replaceAll("&n", ChatColor.UNDERLINE.toString());
		str = str.replaceAll("&o", ChatColor.ITALIC.toString());
		str = str.replaceAll("&r", ChatColor.RESET.toString());
		return str;
	}

	// strip colour tags from strings..
	public static String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9klmnor]))", "").replaceAll("(\u00A7([a-f0-9klmnor]))", "");
	}
	
	public static void sendMessage(Player player, String message) {
		if(message.length() < 1) {
			return;
		}
		player.sendMessage(processColours(message));
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		if(message.length() < 1) {
			return;
		}
		if(sender instanceof Player) {
			sender.sendMessage(processColours(message));
		}
		else {
			consoleMessage(message);
		}
	}
	
	public static void sendMessage(JavaPlugin plugin, String name, String message) {
		if(message.length() < 1) {
			return;
		}
		Player player = plugin.getServer().getPlayer(name);
		if(player != null) {
			player.sendMessage(processColours(message));
		}
	}
	
	public static void consoleMessage(String message) {
		if(message.length() < 1) {
			return;
		}
		ColouredConsoleSender.getInstance().sendMessage(ColourHandler.processConsoleColours(message));
	}
	
	public static String randomColour() {
		// get a random number
		Random generator = new Random();
		Integer roll = generator.nextInt(15) + 1; // don't use black
		// convert to hex string
		// and return!
		return "&" + Integer.toHexString(roll);
	}
	
	public static String rainbowColour() {
		// get the next colour
		nextColour += 1;
		if(nextColour >= 16) {
			nextColour = 1;
		}
		return "&" + Integer.toHexString(nextColour);
	}
}
