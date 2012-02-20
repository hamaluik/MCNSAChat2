package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "crave", permission = "rave", usage = "", description = "toggles &cR&aa&eV&be &7mode in this channel")
public class CommandRaveMode implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandRaveMode(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		Boolean raveMode = plugin.chatManager.toggleRaveMode();
		if(raveMode) {
			ColourHandler.sendMessage(player, "&cR&aa&eV&be &7mode has been activated!");
		}
		else {
			ColourHandler.sendMessage(player, "&cR&aa&eV&be &7mode has been de-activated!");
		}
		
		// and we handled it!
		return true;
	}
}
