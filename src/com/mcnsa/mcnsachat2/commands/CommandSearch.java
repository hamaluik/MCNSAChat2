package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "csearch", permission = "search", usage = "<player>", description = "searches for which channel <player> is in")
public class CommandSearch implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandSearch(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// make sure we have args
		if(sArgs.length() < 1) {
			// no args :(
			return false;
		}
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(sArgs.trim());
		// make sure they're a valid player
		if(targetPlayer == null) {
			ColourHandler.sendMessage(player, "&cError: I could not find the player '&f" + sArgs.trim() + "&c'!");
			return true;
		}
		
		// get their channel
		String channel = plugin.channelManager.getPlayerChannel(targetPlayer);
		
		// and return a message
		ColourHandler.sendMessage(player, plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7is in channel: " + plugin.channelManager.getChannelColour(channel) + channel);
		
		// and we handled it!
		return true;
	}
}
