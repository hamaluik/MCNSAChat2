package com.mcnsa.mcnsachat2.commands;


import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "crainbow", permission = "rainbow", usage = "", description = "toggles &1r&2a&3i&4n&5b&6o&7w mode in this channel")
public class CommandRainbowMode implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandRainbowMode(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		Boolean raveMode = plugin.channelManager.toggleRainbowMode(plugin.channelManager.getPlayerChannel(player));
		if(raveMode) {
			ColourHandler.sendMessage(player, "&1r&2a&3i&4n&5b&6o&7w mode has been activated!");
		}
		else {
			ColourHandler.sendMessage(player, "&1r&2a&3i&4n&5b&6o&7w mode has been de-activated!");
		}
		
		// and we handled it!
		return true;
	}
}