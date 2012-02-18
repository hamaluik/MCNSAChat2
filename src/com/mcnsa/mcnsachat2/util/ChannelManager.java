package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

public class ChannelManager {
	private MCNSAChat2 plugin = null;
	// keep track of channels
	public HashMap<String, Channel> channels = new HashMap<String, Channel>();
	// and the channel that each player is in!
	public HashMap<String, String> players = new HashMap<String, String>();
	// keep track of who is locked in their channels
	public ArrayList<String> locked = new ArrayList<String>();
	// keep track of who is poofed!
	public ArrayList<String> poofed = new ArrayList<String>();
	// and the configuration..
	ConfigManager config = null;
	
	// a list of all players that have seeAll on
	public ArrayList<String> seeAll = new ArrayList<String>();
	
	public ChannelManager(MCNSAChat2 instance, ConfigManager _config) {
		plugin = instance;
		config = _config;
		
		// set up the hard channels
		loadHardChannels();
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
	
	public Boolean channelExists(String channelName) {
		return channels.containsKey(channelName);
	}
	
	public Boolean createChannelIfNotExists(String channelName) {
		// return false if it already existed
		if(channels.containsKey(channelName)) {
			//plugin.debug("channel " + channelName + " already existed, so it was not created!");
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
		//plugin.debug("channel " + channel + " WAS NOT empty (or was hard) and so stuck around");
		return false;
	}
	
	// move a player between channels
	public Boolean movePlayer(String channel, Player player, boolean suppressWho) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to move player " + player.getName() + " into non-existant channel: " + channel);
			return false;
		}
		
		// figure out if they were in a channel to begin with
		if(players.containsKey(player.getName())) {
			//plugin.debug("player " + player.getName() + " was already in a channel!");
			
			// remove them from the channel
			removePlayer(player);
		}
		
		// add them to their new channel
		channels.get(channel).players.add(player.getName());
		// and track their channel!
		players.put(player.getName(), channel);
		
		// report it to the player!
		if(!suppressWho && plugin.chatManager.getVerbosity(player).compareTo(Verbosity.SHOWSOME) >= 0) {
			ColourHandler.sendMessage(player, "&7Welcome to the " + channels.get(channel).colour + channels.get(channel).name + " &7channel!");
		}

		// let them know who's here
		if(!suppressWho && plugin.chatManager.getVerbosity(player).compareTo(Verbosity.SHOWALL) >= 0) {
			Player[] channelPlayers = listListenerPlayers(channel);
			String message = new String("&7Players here: ");
			for(int i = 0; i < channelPlayers.length; i++) {
				// add the players prefix (colour)
				if(isPoofed(channelPlayers[i])) {
					// they're poofed!
					// see if we can see them or not
					if(plugin.hasPermission(player, "seepoofed")) {
						message += plugin.permissions.getUser(channelPlayers[i]).getPrefix() + channelPlayers[i].getName() + "&b*";
					}
				}
				else {
					message += plugin.permissions.getUser(channelPlayers[i]).getPrefix() + channelPlayers[i].getName();
				}
				if(i < channelPlayers.length - 1) {
					 message += "&7, ";
				}
			}
			ColourHandler.sendMessage(player, message);
		}
		
		// and to everyone in the channel
		// TODO: sort this out
		/*ArrayList<String> listeners = getListeners(channel, player);
		for(int i = 0; i < listeners.size(); i++) {
			// make sure we don't tell them that they joined it!
			if(!listeners.get(i).equals(player.getName())) {
				plugin.getServer().getPlayer(listeners.get(i)).sendMessage(plugin.processColours(listeners.get(i) + " &7has joined the channel!"));
			}
		}*/
		
		// and log it!
		plugin.log(player.getName() + " moved into channel " + channel);
		
		// success!
		return true;
	}
	
	// remove a player from all channels (i.e. leaving the game?)
	public void removePlayer(Player player) {
		// make sure they're tracked
		if(!players.containsKey(player.getName())) {
			//plugin.debug("player " + player.getName() + " was not tracked, ignoring removal...");
			return;
		}
		
		// get their channel
		String channel = getPlayerChannel(player);
		
		// remove them from their first channel
		channels.get(players.get(player.getName())).players.remove(player.getName());
		// and remove them from the tracked user list
		players.remove(player.getName());
		
		//plugin.debug("player " + player.getName() + " removed from channel " + channel);
		
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
		//plugin.debug("Added " + player.getName() + " for being the sender");
		
		// handle broadcast channels
		if(channels.get(channel).broadcast) {
			Player[] players = plugin.getServer().getOnlinePlayers();
			for(int i = 0; i < players.length; i++) {
				if(!listeners.contains(players[i].getName())) {
					listeners.add(players[i].getName());
					//plugin.debug("Added " + players[i].getName() + " for being in broadcast");
				}
			}
		}
		// handle local channels
		else if(channels.get(channel).local) {
			// add everyone in the channel who is local
			for(int i = 0; i < channels.get(channel).players.size(); i++) {
				// only add them if they're not already there
				if(!listeners.contains(channels.get(channel).players.get(i))) {
					// make sure they're within range
					if(plugin.playerWithinRadius(player, plugin.getServer().getPlayer(channels.get(channel).players.get(i)), plugin.config.options.localChatRadius.intValue())) {
						// make sure they're not muted
						if(!plugin.chatManager.isPlayerMuted(channels.get(channel).players.get(i), player.getName())) {
							listeners.add(channels.get(channel).players.get(i));
							//plugin.debug("Added " + channels.get(channel).players.get(i) + " for being in local radius");
						}
					}
				}
			}
		}
		// handle normal channels
		else {
			// add everyone in the channel
			for(int i = 0; i < channels.get(channel).players.size(); i++) {
				// only add them if they're not already there
				if(!listeners.contains(channels.get(channel).players.get(i))) {
					// make sure they're not muted
					if(!plugin.chatManager.isPlayerMuted(channels.get(channel).players.get(i), player.getName())) {
						// add them!
						listeners.add(channels.get(channel).players.get(i));
						//plugin.debug("Added " + channels.get(channel).players.get(i) + " for being in the same channel");
					}
				}
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
				// check their permissions and seeall status!
				if(!channels.get(channel).listeners.equals("") && plugin.hasPermission(players[i], "listen." + channels.get(channel).listeners)) {
					// add them!
					listeners.add(players[i].getName());
					//plugin.debug("Added " + players[i].getName() + " for being a listener");
				}
				else if(seeAll.contains(players[i].getName())) {
					// add them!
					listeners.add(players[i].getName());
					//plugin.debug("Added " + players[i].getName() + " for having seeAll on");
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
	
	// return a sorted list of channels
	public Channel[] listChannels() {
		// create the list
		Channel[] chList = new Channel[channels.size()];
		
		// get them all!
		int i = 0;
		for(String ch: channels.keySet()) {
			chList[i] = channels.get(ch);
			i += 1;
		}
		
		// sort the array
		Arrays.sort(chList, new ChannelComp());
		
		// and return!
		return chList;
	}
	
	// get a channel's permission
	public String getPermission(String channel) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to get permission for non-existant channel: " + channel);
			return "sdjkhf289h2fudsasfh28";
		}
		
		// and return the perms
		return channels.get(channel).permission;
	}
	
	// set a channel's colour
	public void setColour(String channel, String colour) {
		// trim the channel name to be sure
		channel = channel.trim();
		
		// make sure the channel exists and that it's valid
		if(!channels.containsKey(channel) || channel.equals("")) {
			plugin.error("attempted to set colour for non-existant channel: " + channel);
			return;
		}
		
		// and set the colour!
		channels.get(channel).colour = colour;
		
		// and announce it
		ArrayList<String> players = channels.get(channel).players;
		for(int i = 0; i < players.size(); i++) {
			if(plugin.chatManager.getVerbosity(plugin.getServer().getPlayer(players.get(i))).compareTo(Verbosity.SHOWALL) >= 0) {
				ColourHandler.sendMessage(plugin.getServer().getPlayer(players.get(i)), "&7Channel &f" + channel + "&7's colour has been changed to: " + colour + ColourHandler.translateColour(colour));
			}
		}
	}
	
	// toggle whether a player has seeall on or not
	public Boolean toggleSeeAll(Player player) {
		if(seeAll.contains(player.getName())) {
			seeAll.remove(player.getName());
			plugin.log(player + " no longer has seeall");
			return false;
		}
		else {
			seeAll.add(player.getName());
			plugin.log(player + " now has seeall");
			return true;
		}
	}
	
	// set the entire seeAll list (for persistance)
	@SuppressWarnings("unchecked")
	public void setSeeAll(ArrayList<String> players) {
		seeAll.clear();
		seeAll = (ArrayList<String>)players.clone();
	}
	
	public Player[] listPlayers(String channel) {
		ArrayList<Player> pl = new ArrayList<Player>();
		for(String player: players.keySet()) {
			if(players.get(player).equalsIgnoreCase(channel)) {
				pl.add(plugin.getServer().getPlayer(player));
			}
		}
		
		// make it into an array
		Player[] plArr = new Player[pl.size()];
		for(int i = 0; i < pl.size(); i++) {
			plArr[i] = pl.get(i);
		}
		
		// and sort
		Arrays.sort(plArr, new PlayerRankComparator());
		
		return plArr;
	}
	
	public Player[] listListenerPlayers(String channel) {
		ArrayList<Player> pl = new ArrayList<Player>();
		for(String player: players.keySet()) {
			if(players.get(player).equalsIgnoreCase(channel)) {
				pl.add(plugin.getServer().getPlayer(player));
			}
		}
		// now get everyone who "listens in" on this channel
		// now include those listening in automatically
		// first make sure they channel has some default listeners
		if(!channels.get(channel).listeners.equals("")) {
			// get a list of all online players
			Player[] online = plugin.getServer().getOnlinePlayers();
			for(int i = 0; i < online.length; i++) {
				// make sure they're not already on the list
				if(!pl.contains(online[i])) {
					// check their permissions status!
					if(plugin.hasPermission(online[i], "listen." + channels.get(channel).listeners)) {
						// add them!
						pl.add(online[i]);
						//plugin.debug("Added " + players[i].getName() + " for being a listener");
					}
				}
			}
		}
		
		// make it into an array
		Player[] plArr = new Player[pl.size()];
		for(int i = 0; i < pl.size(); i++) {
			plArr[i] = pl.get(i);
		}
		
		// and sort
		Arrays.sort(plArr, new PlayerRankComparator());
		
		return plArr;
	}
	
	public boolean toggleLocked(Player player) {
		if(locked.contains(player.getName())) {
			//plugin.debug("player " + player.getName() + " has been unlocked from their channel!");
			locked.remove(player.getName());
			return false;
		}
		else {
			//plugin.debug("player " + player.getName() + " has been locked in their channel!");
			locked.add(player.getName());
			return true;
		}
	}
	
	public boolean isLocked(Player player) {
		return locked.contains(player.getName());
	}
	
	public String[] listLocked() {
		String[] lockedArray = new String[locked.size()];
		for(int i = 0; i < locked.size(); i++) {
			lockedArray[i] = locked.get(i);
		}
		return lockedArray;
	}
	
	public void reloadChannels() {
		// and send people to their appropriate channels
		Player[] online = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < online.length; i++) {
			// move them into their channel!
			String channel = plugin.ph.getOfflineChannel(online[i].getName());
			// create the channel if it doesn't exist
			createChannelIfNotExists(channel);
			// and move into it!
			movePlayer(channel, online[i], true);
		}
	}
	
	public boolean togglePoof(Player player) {
		if(poofed.contains(player.getName())) {
			//plugin.debug("player " + player.getName() + " has been unlocked from their channel!");
			poofed.remove(player.getName());
			return false;
		}
		else {
			//plugin.debug("player " + player.getName() + " has been locked in their channel!");
			poofed.add(player.getName());
			return true;
		}
	}
	
	public boolean isPoofed(Player player) {
		return poofed.contains(player.getName());
	}
	
	// a channel
	public class Channel {
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
	
	class ChannelComp implements Comparator<Channel> {
		public int compare(Channel a, Channel b) {
			return a.name.compareTo(b.name);
		}
	}

	// create a comparator class for the group rankings
	class PlayerRankComparator implements Comparator<Player> {
		public int compare(Player a, Player b) {
			int ra = plugin.permissions.getUser(a).getOptionInteger("rank", "", 9999);
			int rb = plugin.permissions.getUser(b).getOptionInteger("rank", "", 9999);
			
			if(ra < rb)
				return 1;
			else if(ra == rb)
				return 0;
			else {
				return -1;
			}
		}
	}
}
