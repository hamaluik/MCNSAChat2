package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class ChatManager {
	MCNSAChat2 plugin = null;
	ChannelManager channelManager = null;
	// a list of all players that are on timeout
	public ArrayList<String> onTimeout = new ArrayList<String>();

	public ChatManager(MCNSAChat2 instance, ChannelManager cm) {
		plugin = instance;
		channelManager = cm;
	}

	public void handleChat(Player player, String message, Boolean emote, String toChannel) {
		// figure out which channel to speak to
		String channel = new String(toChannel);
		if(toChannel.equals("")) {
			// figure out which channel the player is in
			channel = channelManager.getPlayerChannel(player);
		}
		
		// see if they're in timeout
		if(onTimeout.contains(player.getName())) {
			// they're in timeout!
			player.sendMessage(plugin.processColours("&cYou can't talk, because you're in timeout!"));
			plugin.log("{timeout} " + player.getName() + ": " + message);
			return;
		}

		// and get a list of everyone who is listening in
		ArrayList<String> listeners = channelManager.getAllListeners(channel, player);

		// TODO: check for spam

		// now send the message out!
		String outgoing = new String(plugin.config.options.chatFormat);
		// change the format if it's an emote
		if(emote)
			outgoing = plugin.config.options.emoteFormat;
		outgoing = outgoing.replace("%channel", channelManager.getChannelColour(channel) + channel);
		outgoing = outgoing.replace("%prefix", plugin.permissions.getUser(player).getPrefix());
		outgoing = outgoing.replace("%suffix", plugin.permissions.getUser(player).getSuffix());
		outgoing = outgoing.replace("%player", player.getName());
		// now process colours..
		outgoing = plugin.processColours(outgoing);
		outgoing = outgoing.replace("%message", message);
		// now see if they have permission to write with colour
		if(plugin.hasPermission(player, "colour"))
			outgoing = plugin.processColours(outgoing);
		// now send it
		for(int i = 0; i < listeners.size(); i++) {
			// get the player associated with this name
			Player recipient = plugin.getServer().getPlayer(listeners.get(i));
			if(recipient != null) {				
				if(!onTimeout.contains(listeners.get(i))) {
					// pass along the message to anyone who's not in timeout
					recipient.sendMessage(outgoing);	
				}
				else {
					// uh-oh, they're in timeout!
					String timeout = new String(plugin.config.options.chatFormat);
					// change the format if it's an emote
					if(emote)
						timeout = plugin.config.options.emoteFormat;
					timeout = timeout.replace("%channel", channelManager.getChannelColour(channel) + channel);
					timeout = timeout.replace("%prefix", plugin.permissions.getUser(player).getPrefix());
					timeout = timeout.replace("%suffix", plugin.permissions.getUser(player).getSuffix());
					timeout = timeout.replace("%player", player.getName());
					timeout = timeout.replace("%message", "&c(You can't hear this because you're in timeout!)");
					recipient.sendMessage(plugin.processColours(timeout));
				}
			}
		}

		// and log it
		plugin.log(plugin.stripColours(outgoing));
	}
	
	// toggle whether a player is on timeout or not
	public Boolean toggleTimeout(Player player) {
		if(onTimeout.contains(player.getName())) {
			onTimeout.remove(player.getName());
			return false;
		}
		else {
			onTimeout.add(player.getName());
			return true;
		}
	}
}
