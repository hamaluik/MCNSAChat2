package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "c", usage = "<channel>", description = "changes your channel to <channel>")
public class CommandChannel implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandChannel(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// get the channel name
		String channel = sArgs.trim();
		
		// make sure they're not locked
		if(plugin.channelManager.isLocked(player)) {
			// they can't change channels!
			ColourHandler.sendMessage(player, "&cYou have been locked in your channel and cannot change channels!");
			return true;
		}
		
		// create the channel if it doesn't exist
		channel = plugin.channelManager.createChannelIfNotExists(channel);
		
		// first up: check their perms
		String perm = plugin.channelManager.getPermission(channel);
		if(!perm.equals("") && !plugin.hasPermission(player, "channel." + perm)) {
			// return a message if they don't have permission
			plugin.log(player.getName() + " attempted to change to channel: " + channel + " without permission!");
			ColourHandler.sendMessage(player, "&cYou don't have permission to do that!");
			return true;
		}
		
		// now move into it!
		plugin.channelManager.movePlayer(channel, player, false);
		
		return true;
	}
}
