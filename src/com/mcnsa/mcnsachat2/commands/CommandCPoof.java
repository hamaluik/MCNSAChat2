package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "cpoof", permission = "poof", description = "toggles whether you are poofed or not")
public class CommandCPoof implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandCPoof(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {		
		// toggle their status!
		Boolean poofed = plugin.channelManager.togglePoof(player);
		
		// and alert them to their status
		if(poofed) {
			ColourHandler.sendMessage(player, "&7You are now poofed!");
		}
		else {
			ColourHandler.sendMessage(player, "&7You are no longer poofed!");
		}
		
		// and we handled it!
		return true;
	}
}
