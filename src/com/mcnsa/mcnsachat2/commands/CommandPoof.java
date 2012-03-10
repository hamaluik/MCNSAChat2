package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "poof", permission = "vanish", usage = "", description = "toggle your vanished status")
public class CommandPoof implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandPoof(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// construct the result
		boolean vanished = plugin.vanishManager.togglePlayerVisibility(player);
		if(vanished) {
			ColourHandler.sendMessage(player, "&5You are now invisible!");
		}
		else {
			ColourHandler.sendMessage(player, "&5You are now visible!");
		}
		
		// and we handled it!
		return true;
	}
}