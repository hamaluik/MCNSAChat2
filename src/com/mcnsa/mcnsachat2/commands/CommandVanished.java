package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "vanished", permission = "seevanished", usage = "", description = "lists everyone who is vanished")
public class CommandVanished implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandVanished(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// construct the result
		String result = "&3Players who are currently vanished: ";
		for(int i = 0; i < plugin.vanishManager.vanishedPlayers.size(); i++) {
			result += "&f" + plugin.vanishManager.vanishedPlayers.get(i).getName();
			if(i < plugin.vanishManager.vanishedPlayers.size() - 1) {
				result += "&3, ";
			}
		}
		ColourHandler.sendMessage(player, result);
		
		// and we handled it!
		return true;
	}
}
