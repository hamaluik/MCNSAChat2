package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
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
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(args[0]);
		// make sure they're a valid player
		if(targetPlayer == null) {
			player.sendMessage(plugin.processColours("&cError: I could not find the player '&f" + sArgs.trim() + "&c'!"));
			return true;
		}

		// create the channel if it doesn't exist
		plugin.channelManager.createChannelIfNotExists(channel);
		
		// and return a message
		player.sendMessage(plugin.processColours(plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7has been moved to channel: " + plugin.channelManager.getChannelColour(channel) + channel));
		if(plugin.chatManager.getVerbosity(targetPlayer).compareTo(Verbosity.SHOWSOME) >= 0) {
			targetPlayer.sendMessage(plugin.processColours("&7You have been moved to channel: " + plugin.channelManager.getChannelColour(channel) + channel));
		}
		
		// now move into it!
		plugin.channelManager.movePlayer(channel, targetPlayer);
		
		// and we handled it!
		return true;
	}
}
