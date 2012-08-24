package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "creload", permission = "reload", description = "reloads the MCNSAChat2 config")
public class CommandReload implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandReload(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {		
		plugin.reloadConfig();
		if(!plugin.config.load(plugin.getConfig())) {
			// shit
			// BAIL
			plugin.error("configuration failed");
			ColourHandler.sendMessage(player, "&cError - mcnsachat2 configuration reload failed!");
		}
		plugin.saveConfig();
		
		// send them a message
		ColourHandler.sendMessage(player, "&aConfiguration reloaded successfully!");
		plugin.log("&aConfiguration reloaded by &f" + player.getName());
		
		// and we handled it!
		return true;
	}
}

