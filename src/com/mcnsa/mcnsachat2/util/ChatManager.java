package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.Packet3Chat;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class ChatManager {
	MCNSAChat2 plugin = null;
	ChannelManager channelManager = null;
	// a list of all players that are on timeout
	public ArrayList<String> onTimeout = new ArrayList<String>();
	// a list of all players that have VoxelChat installed
	public ArrayList<String> voxelChat = new ArrayList<String>();
	// keep track of the verbosity level for players
	public HashMap<String, Verbosity> verbosity = new HashMap<String, Verbosity>();

	public ChatManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public void setChannelManager(ChannelManager cm) {
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
		if(!plugin.hasPermission(player, "colour")) {
			message =  plugin.stripColours(message);
		}
		// and add it
		outgoing = outgoing.replace("%message", message);
		// now process the colours
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
		
		// now, take care of those who have VoxelChat
		if(!emote) {
			for(int i = 0; i < voxelChat.size(); i++) {
				// get the player info
				Player pl = plugin.getServer().getPlayer(voxelChat.get(i));
				
				plugin.debug("Sending VoxelChat data to player " + pl.getName());
				
				// do this so this manual queueing so we don't overflow the 119 character limit
				((CraftPlayer)pl).getHandle().netServerHandler.networkManager.queue(new Packet3Chat((new StringBuilder()).append("\247b\247d\247c\247b\247d\247cq?=$name=").append(player.getName()).toString()));
				((CraftPlayer)pl).getHandle().netServerHandler.networkManager.queue(new Packet3Chat((new StringBuilder()).append("\247b\247d\247c\247b\247d\247cq?=$message=").append(message).toString()));
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
	
	// enable VoxelChat for a player
	public void enableVoxelChat(Player player) {
		if(!voxelChat.contains(player.getName())) {
			plugin.debug("player " + player.getName() + " has enabled VoxelChat!");
			voxelChat.add(player.getName());
		}
	}
	
	// and remove VoxelChatters when they log off
	public void disableVoxelChat(Player player) {
		if(voxelChat.contains(player.getName())) {
			plugin.debug("player " + player.getName() + " has disabled VoxelChat!");
			voxelChat.remove(player.getName());
		}
	}
	
	// and toggle VoxelChat manually
	public boolean toggleVoxelChat(Player player) {
		if(voxelChat.contains(player.getName())) {
			plugin.debug("player " + player.getName() + " has disabled VoxelChat!");
			voxelChat.remove(player.getName());
			return false;
		}
		else {
			plugin.debug("player " + player.getName() + " has enabled VoxelChat!");
			voxelChat.add(player.getName());
			return true;
		}
	}
	
	// set a player's verbosity
	public void setVerbosity(Player player, Verbosity level) {
		plugin.debug("Seeing player " + player.getName() + "'s verbosity to: " + level);
		verbosity.put(player.getName(), level);
	}
	
	// set a player's verbosity
	public Verbosity getVerbosity(Player player) {
		plugin.debug("Getting verbosity for " + player.getName());
		if(!verbosity.containsKey(player.getName())) {
			plugin.debug("Player did not have verbosity set!");
			setVerbosity(player, Verbosity.SHOWALL);
		}
		plugin.debug("Player has verbosity: " + verbosity.get(player.getName()));
		return verbosity.get(player.getName());
	}
	
	// keep track of player verbosity
	public enum Verbosity {
		SHOWNONE, SHOWSOME, SHOWALL
	}
}
