package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class ChannelManager {
	private MCNSAChat2 plugin = null;
	// keep track of channels
	private HashMap<String, Channel> channels = new HashMap<String, Channel>();
	// and the channel that each player is in!
	private HashMap<String, String> players = new HashMap<String, String>();
	// and the configuration..
	ConfigManager config = null;
	
	public ChannelManager(MCNSAChat2 instance, ConfigManager _config) {
		plugin = instance;
		config = _config;
		
		// set up the hard channels
		loadHardChannels();
		
		// and put players in the proper channel
		plugin.log("placing players in default channel...");
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			movePlayer(config.options.defaultChannel, players[i]);
		}
	}
	
	private void loadHardChannels() {
		// loop through the config channels
		for(String key: config.options.hardChannels.keySet()) {
			// copy over the configuration options
			Channel channel = new Channel(key);
			channel.hard = true; // make sure it's a hard channel
			channel.colour = config.options.hardChannels.get(key).colour;
			channel.alias = config.options.hardChannels.get(key).alias;
			channel.permission = config.options.hardChannels.get(key).permission;
			channel.listeners = config.options.hardChannels.get(key).listeners;
			channel.local = config.options.hardChannels.get(key).local;
			channel.broadcast = config.options.hardChannels.get(key).broadcast;
			
			// register the alias
			plugin.commandManager.registerAlias(channel.alias, key);
			
			// and keep track of it
			channels.put(key, channel);
		}
	}
	
	public Boolean createChannelIfNotExists(String channelName) {
		// return false if it already existed
		if(channels.containsKey(channelName)) {
			plugin.debug("channel " + channelName + " already existed, so it was not created!");
			return false;
		}
		
		// create the new channel
		Channel channel = new Channel(channelName);
		channel.colour = config.options.defaultColour;
		
		// and keep track of it
		channels.put(channelName, channel);
		
		// yup, we created a new channel!
		return true;
	}
	
	public Boolean removeChannelIfEmpty(String channel) {
		if(channels.containsKey(channel) && channels.get(channel).players.isEmpty() && !channels.get(channel).hard) {
			// ok, the channel exists and it IS empty and it IS NOT hard!
			channels.remove(channel);
			plugin.log("channel " + channel + " emptied out and was removed");
			return true;
		}
		// we didn't remove anything
		plugin.debug("channel " + channel + " WAS NOT empty (or was hard) and so stuck around");
		return false;
	}
	
	// move a player between channels
	public Boolean movePlayer(String channel, Player player) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to move player " + player.getName() + " into non-existant channel: " + channel);
			return false;
		}
		
		// figure out if they were in a channel to begin with
		if(players.containsKey(player.getName())) {
			plugin.debug("player " + player.getName() + " was already in a channel!");
			
			// remove them from the channel
			removePlayer(player);
		}
		
		// add them to their new channel
		channels.get(channel).players.add(player.getName());
		// and track their channel!
		players.put(player.getName(), channel);
		
		// and log it!
		plugin.log(player.getName() + " moved into channel " + channel);
		
		// success!
		return true;
	}
	
	// remove a player from all channels (i.e. leaving the game?)
	public void removePlayer(Player player) {
		// make sure they're tracked
		if(!players.containsKey(player.getName())) {
			plugin.debug("player " + player.getName() + " was not tracked, ignoring removal...");
			return;
		}
		
		// get their channel
		String channel = getPlayerChannel(player);
		
		// remove them from their first channel
		channels.get(players.get(player.getName())).players.remove(player.getName());
		// and remove them from the tracked user list
		players.remove(player.getName());
		
		plugin.debug("player " + player.getName() + " removed from channel " + channel);
		
		// and clear out empty channels
		removeChannelIfEmpty(channel);
	}
	
	// get a list of everyone who can hear the chat
	// (excluding those who are listening in)
	public ArrayList<String> getListeners(String channel, Player player) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to get listeners for non-existant channel: " + channel);
			return new ArrayList<String>();
		}
		
		// a list of all listeners
		ArrayList<String> listeners = new ArrayList<String>();
		
		// add the sender
		listeners.add(player.getName());
		
		// add everyone in the channel
		// TODO: sort based on local
		for(int i = 0; i < channels.get(channel).players.size(); i++) {
			// only add them if they're not already there
			if(!listeners.contains(channels.get(channel).players.get(i))) {
				// add them!
				listeners.add(channels.get(channel).players.get(i));
			}
		}
		
		// and return!
		return listeners;
	}
	
	// this time, include EVERYONE who will hear it!
	public ArrayList<String> getAllListeners(String channel, Player player) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to get listeners for non-existant channel: " + channel);
			return new ArrayList<String>();
		}
		
		// a list of all normal listeners
		ArrayList<String> listeners = getListeners(channel, player);
		
		// now include those listening in automatically
		// get a list of all online players
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			// make sure they're not already on the list
			if(!listeners.contains(players[i].getName())) {
				// check their permissions!
				if(plugin.hasPermission(players[i], "listen." + channels.get(channel).listeners)) {
					// add them!
					listeners.add(players[i].getName());
				}
			}
		}
		
		return listeners;
	}
	
	// get which channel the player is currently in
	public String getPlayerChannel(Player player) {
		if(!players.containsKey(player.getName())) return "";
		return players.get(player.getName());
	}
	
	// get a channel's colour
	public String getChannelColour(String channel) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to get colour for non-existant channel: " + channel);
			return config.options.defaultColour;
		}
		
		// return the colour!
		return channels.get(channel).colour;
	}
	
	// a channel
	@SuppressWarnings("unused")
	private class Channel {
		public String name = new String("");
		public Boolean hard = false;
		public String colour = new String("&f");
		public String alias = new String("");
		public String permission = new String("");
		public String listeners = new String("");
		public Boolean local = false;
		public Boolean broadcast = false;
		
		public ArrayList<String> players = new ArrayList<String>();
		
		public Channel(String _name) {
			name = _name;
		}
	}
}
