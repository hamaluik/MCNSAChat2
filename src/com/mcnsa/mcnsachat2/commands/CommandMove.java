package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

@CommandInfo(alias = "cmove", permission = "move", usage = "<player> <channel>", description = "moves <player> to <channel>")
public class CommandMove implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandMove(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1) {
			return false;
		}
		
		// split the args
		String[] args = sArgs.split("\\s");
		if(args.length != 2) {
			return false;
		}
		String channel = args[1];
		
		// see if we're moving everyone
		Player[] targetPlayers;
		if(args[0].equals("*")) {
			targetPlayers = plugin.getServer().getOnlinePlayers();
		}
		else {
			// get the targeted player
			targetPlayers = new Player[1];
			targetPlayers[0] = plugin.getServer().getPlayer(args[0]);
			// make sure they're a valid player
			if(targetPlayers[0] == null) {
				ColourHandler.sendMessage(player, "&cError: I could not find the player '&f" + args[0] + "&c'!");
				return true;
			}
		}

		// create the channel if it doesn't exist
		plugin.channelManager.createChannelIfNotExists(channel);
		
		// loop through all the target players
		for(int i = 0; i < targetPlayers.length; i++) {
			// and return a message
			ColourHandler.sendMessage(player, plugin.permissions.getUser(targetPlayers[i]).getPrefix() + targetPlayers[i].getName() + " &7has been moved to channel: " + plugin.channelManager.getChannelColour(channel) + channel);
			if(plugin.chatManager.getVerbosity(targetPlayers[i]).compareTo(Verbosity.SHOWSOME) >= 0) {
				ColourHandler.sendMessage(targetPlayers[i], "&7You have been moved to channel: " + plugin.channelManager.getChannelColour(channel) + channel);
			}
			
			// now move into it!
			plugin.channelManager.movePlayer(channel, targetPlayers[i], false);
		}
		
		// and we handled it!
		return true;
	}
}
