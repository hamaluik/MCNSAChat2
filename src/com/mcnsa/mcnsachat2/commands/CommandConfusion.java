package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "confusion", permission = "confusion", usage = "", description = "toggles confusion mode in this channel")
public class CommandConfusion implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandConfusion(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		Boolean confusionMode = plugin.chatManager.toggleConfusionMode();
		if(confusionMode) {
			ColourHandler.sendMessage(player, "&7confusion mode has been activated!");
		}
		else {
			ColourHandler.sendMessage(player, "&7confusion mode has been de-activated!");
		}
		
		// and we handled it!
		return true;
	}
}