package com.mcnsa.mcnsachat2.util;

public class ColourHandler {
	// make it a static class: private constructor, static methods
	private ColourHandler() {}

	public static String translateName(String name) {
		// default colour will be white.
		String colour = new String("&f");
		
		// map it!
		switch(name.toLowerCase()) {
			case "black": colour = "&0"; break;
			case "dblue": colour = "&1"; break;
			case "dark blue": colour = "&1"; break;
			case "dgreen": colour = "&2"; break;
			case "dark green": colour = "&2"; break;
			case "dteal": colour = "&3"; break;
			case "dark teal": colour = "&3"; break;
			case "daqua": colour = "&3"; break;
			case "dark aqua": colour = "&3"; break;
			case "dred": colour = "&4"; break;
			case "dark red": colour = "&4"; break;
			case "purple": colour = "&5"; break;
			case "dpink": colour = "&5"; break;
			case "dark pink": colour = "&5"; break;
			case "gold": colour = "&6"; break;
			case "orange": colour = "&6"; break;
			case "grey": colour = "&7"; break;
			case "gray": colour = "&7"; break;
			case "dgrey": colour = "&8"; break;
			case "dark grey": colour = "&8"; break;
			case "dgray": colour = "&8"; break;
			case "dark gray": colour = "&8"; break;
			case "blue": colour = "&9"; break;
			case "green": colour = "&a"; break;
			case "bright green": colour = "&a"; break;
			case "teal": colour = "&b"; break;
			case "aqua": colour = "&b"; break;
			case "red": colour = "&c"; break;
			case "pink": colour = "&d"; break;
			case "yellow": colour = "&e"; break;
			case "white": colour = "&f"; break;
		}
		
		return colour;
	}
}
