package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "cwho", permission = "who", description = "lists everyone who is in the channel")
public class CommandWho implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandWho(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {		
		Player[] players = plugin.channelManager.listPlayers(plugin.channelManager.getPlayerChannel(player));
		
		String message = new String("&7Players here: ");
		for(int i = 0; i < players.length; i++) {
			// add the players prefix (colour)
			message += plugin.permissions.getUser(players[i]).getPrefix() + players[i].getName() + "&7, ";
		}
		
		ColourHandler.sendMessage(player, message);
		
		// and we handled it!
		return true;
	}
}

