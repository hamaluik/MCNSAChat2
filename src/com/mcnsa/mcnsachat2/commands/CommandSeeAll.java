package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "cs", permission = "seeall", usage = "", description = "toggles whether or not you can see all chat")
public class CommandSeeAll implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandSeeAll(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {		
		// toggle their status!
		Boolean seeAll = plugin.channelManager.toggleSeeAll(player);
		
		// and alert them to their status
		if(seeAll) {
			ColourHandler.sendMessage(player, "&7You can now see &fALL &7chat channels!");
		}
		else {
			ColourHandler.sendMessage(player, "&7You can &fNO LONGER &7see &fALL &7chat channels!");
		}
		
		// and we handled it!
		return true;
	}
}
