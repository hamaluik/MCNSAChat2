package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "cverbosity", usage = "[all|some|none]", description = "changes your notification verbosity level or lists your current level")
public class CommandVerbosity implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandVerbosity(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// make sure we have args
		if(sArgs.length() < 1) {
			// no args - tell them their verbosity level
			Verbosity level = plugin.chatManager.getVerbosity(player);
			// and alert them!
			if(level == Verbosity.SHOWALL) {
				ColourHandler.sendMessage(player, "&7Your verbosity level is set to show &fALL &7chat notifications");
			}
			else if(level == Verbosity.SHOWSOME) {
				ColourHandler.sendMessage(player, "&7Your verbosity level is set to show &fSOME &7chat notifications");
			}
			else if(level == Verbosity.SHOWNONE) {
				ColourHandler.sendMessage(player, "&7Your verbosity level is set to show &fNO &7chat notifications");
			}
			return true;
		}
		
		// and get their parameter
		sArgs = sArgs.trim();
		Verbosity level = null;
		if(sArgs.equalsIgnoreCase("all")) {
			level = Verbosity.SHOWALL;
		}
		else if(sArgs.equalsIgnoreCase("some")) {
			level = Verbosity.SHOWSOME;
		}
		else if(sArgs.equalsIgnoreCase("none")) {
			level = Verbosity.SHOWNONE;
		}
		else {
			// bad args :(
			return false;
		}
		
		// set their verbosity level!
		plugin.chatManager.setVerbosity(player, level);
		
		// and alert them!
		if(level == Verbosity.SHOWALL) {
			ColourHandler.sendMessage(player, "&7Your verbosity level has been set to show &fALL &7chat notifications");
		}
		else if(level == Verbosity.SHOWSOME) {
			ColourHandler.sendMessage(player, "&7Your verbosity level has been set to show &fSOME &7chat notifications");
		}
		else if(level == Verbosity.SHOWNONE) {
			ColourHandler.sendMessage(player, "&7Your verbosity level has been set to show &fNO &7chat notifications");
		}
		
		// and we handled it!
		return true;
	}
}
