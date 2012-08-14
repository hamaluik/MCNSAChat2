package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.TimerTask;

import net.minecraft.server.Packet3Chat;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class ChatManager {
	MCNSAChat2 plugin = null;
	ChannelManager channelManager = null;
	// hashmap of how much time is left for players on timeout
	public HashMap<String, Long> timeoutTimers = new HashMap<String, Long>();
	// a list of all players that have VoxelChat installed
	public ArrayList<String> voxelChat = new ArrayList<String>();
	// keep track of the verbosity level for players
	public HashMap<String, Verbosity> verbosity = new HashMap<String, Verbosity>();
	// keep track of who has who muted
	public HashMap<String, ArrayList<String>> muted = new HashMap<String, ArrayList<String>>();
	// keep track of all possible ranks for confusion mode
	private ArrayList<String> confusionRanks = new ArrayList<String>();

	public ChatManager(MCNSAChat2 instance) {
		plugin = instance;
		
		// load the confusion ranks
		PermissionGroup[] groups = plugin.permissions.getGroups();
		for(int i = 0; i < groups.length; i++) {
			if(!groups[i].has("mcnsachat2.ignoreconfusion")) {
				// this group doesn't have confusion-ignoring turned on
				// add them to the list!
				confusionRanks.add(groups[i].getPrefix() + groups[i].getName());
			}
		}
	}
	
	public void setChannelManager(ChannelManager cm) {
		channelManager = cm;
	}

	public boolean handleChat(Player player, String message, Boolean emote, String toChannel, Boolean checkColours) {
		plugin.debug("handling chat");
		// figure out which channel to speak to
		String channel = new String(toChannel);
		if(toChannel.equals("")) {
			// figure out which channel the player is in
			channel = channelManager.getPlayerChannel(player);
		}
		plugin.debug("\tplayer is talking to channel: " + channel);
		
		// see if they're in timeout
		if(timeoutTimers.containsKey(player.getName())) {
			// they're in timeout!
			ColourHandler.sendMessage(player, "&cYou can't talk, because you're in timeout with &f" + plugin.formatTime(timeoutTimers.get(player.getName())) + " &cleft!");
			plugin.log("{timeout} " + player.getName() + ": " + message);
			return false;
		}

		// and get a list of everyone who is listening in
		ArrayList<String> listeners = channelManager.getAllListeners(channel, player);
		plugin.debug("\tlisteners acquired");

		// check for spam
		if(plugin.spamManager.checkChatSpam(player)) {
			// they're spamming!
			// send them to timeout!
			if(!inTimeout(player)) {
				// (only if they're not already there..)
				setTimeout(player, plugin.config.options.spamConfig.timeoutTime.longValue());
				
				String colouredName = plugin.permissions.getUser(player).getPrefix() + player.getName();
				
				// announce it to the server?
				if(plugin.config.options.announceTimeouts) {
					Player[] players = plugin.getServer().getOnlinePlayers();
					for(int i = 0; i < players.length; i++) {
						if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWALL) >= 0) {
							ColourHandler.sendMessage(players[i], "Attention&7: &f" + colouredName + " &7has been placed in timeout for &4spamming&7!");
						}
					}
				}
			}
			
			// get out of here
			plugin.log(player.getName() + " was detected of spam!");
			return false;
		}
		plugin.debug("\tno spam detected");

		// now send the message out!
		String outgoing = new String(plugin.config.options.chatFormat);
		// change the format if it's an emote
		if(emote)
			outgoing = plugin.config.options.emoteFormat;
		
		// universe and channel replacements
		outgoing = outgoing.replace("%universe", plugin.config.options.universeName);
		outgoing = outgoing.replace("%channel", channelManager.getChannelColour(channel) + channel);
		
		// handle confusion mode
		if(plugin.channelManager.channelExists(channel) && plugin.channelManager.hasConfusionMode(channel) && !plugin.hasPermission(player, "ignoreconfusion")) {
			// this person's identity must be confused!
			// get a random rank
			Random generator = new Random();
			Integer roll = generator.nextInt(confusionRanks.size());
			outgoing = outgoing.replace("%prefix%suffix", confusionRanks.get(roll));
		}
		else {
			outgoing = outgoing.replace("%prefix", plugin.permissions.getUser(player).getPrefix());
			outgoing = outgoing.replace("%suffix", plugin.permissions.getUser(player).getSuffix());
		}
		
		outgoing = outgoing.replace("%player", player.getName());
		// now process colours..
		if(checkColours && !plugin.hasPermission(player, "colour")) {
			message = ColourHandler.stripColours(message);
		}
		
		// handle rave mode
		if(plugin.channelManager.channelExists(channel) && plugin.channelManager.hasRaveMode(channel)) {
			String raveMessage = new String("");
			for(int i = 0; i < message.length(); i++) {
				raveMessage += ColourHandler.randomColour() + message.charAt(i);
			}
			message = raveMessage;
		}
		// handle rainbow mode
		else if(plugin.channelManager.channelExists(channel) && plugin.channelManager.hasRainbowMode(channel)) {
			String rainbowMessage = new String("");
			for(int i = 0; i < message.length(); i++) {
				rainbowMessage += ColourHandler.rainbowColour() + message.charAt(i);
			}
			message = rainbowMessage;
		}
		
		// and add it
		outgoing = outgoing.replace("%message", message);
		// now process the colours
		outgoing = ColourHandler.processColours(outgoing);
		
		plugin.debug("\toutgoing message formatted: " + outgoing);
		
		// now send it
		for(int i = 0; i < listeners.size(); i++) {
			// get the player associated with this name
			Player recipient = plugin.getServer().getPlayer(listeners.get(i));
			if(recipient != null) {				
				if(!timeoutTimers.containsKey(listeners.get(i))) {
					// pass along the message to anyone who's not in timeout
					recipient.sendMessage(outgoing);	
				}
				/*else {
					// uh-oh, they're in timeout!
					String timeout = new String(plugin.config.options.chatFormat);
					// change the format if it's an emote
					if(emote)
						timeout = plugin.config.options.emoteFormat;
					timeout = timeout.replace("%universe", plugin.config.options.universeName);
					timeout = timeout.replace("%channel", channelManager.getChannelColour(channel) + channel);
					timeout = timeout.replace("%prefix", plugin.permissions.getUser(player).getPrefix());
					timeout = timeout.replace("%suffix", plugin.permissions.getUser(player).getSuffix());
					timeout = timeout.replace("%player", player.getName());
					timeout = timeout.replace("%message", "&c(You can't hear this because you're in timeout!)");
					recipient.sendMessage(ColourHandler.processColours(timeout));
				}*/
			}
		}
		
		// now, take care of those who have VoxelChat
		if(!emote) {
			message = ColourHandler.processColours(message);
			for(int i = 0; i < voxelChat.size(); i++) {
				// make sure that voxelChat player is listening!
				if(listeners.contains(voxelChat.get(i))) {
					// get the player info
					Player pl = plugin.getServer().getPlayer(voxelChat.get(i));
					
					//plugin.debug("Sending VoxelChat data to player " + pl.getName());
					
					// do this so this manual queueing so we don't overflow the 119 character limit
					((CraftPlayer)pl).getHandle().netServerHandler.networkManager.queue(new Packet3Chat((new StringBuilder()).append("\247b\247d\247c\247b\247d\247cq?=$name=").append(player.getName()).toString()));
					((CraftPlayer)pl).getHandle().netServerHandler.networkManager.queue(new Packet3Chat((new StringBuilder()).append("\247b\247d\247c\247b\247d\247cq?=$message=").append(message).toString()));
				}
			}
		}
		
		// and log it
		plugin.log(outgoing);
		
		// they spoke, so send it off to network chat
		return true;
	}
	
	public void sendNetworkChat(Player player, String message, Boolean emote, String toChannel, Boolean checkColours) {
		// first off, figure out which channel to send to
		// to make sure that it's network-enabled
		String channel = new String(toChannel);
		if(toChannel.equals("")) {
			// figure out which channel the player is in
			channel = channelManager.getPlayerChannel(player);
		}
		
		// if it's not a networked channel, we don't care
		if(!channelManager.isNetworked(channel)) {
			plugin.debug("channel " + channel + " isn't networked, so no broadcast!");
			return;
		}
		
		// it is networked! ok to send!
		String outgoing = new String("");
		outgoing += plugin.config.options.universeName + ":";
		outgoing += channel + ":";
		outgoing += player.getName() + ":";
		outgoing += plugin.permissions.getUser(player).getPrefix() + ":";
		outgoing += plugin.permissions.getUser(player).getSuffix() + ":";
		outgoing += (emote ? "1" : "0") + ":";
		// now sort out colours
		if(checkColours && !plugin.hasPermission(player, "colour")) {
			message = ColourHandler.stripColours(message);
		}
		outgoing += message;
		
		// now send it off to the network manager!
		plugin.netManager.sendMessage(outgoing);
		
		// and we're done!
	}
	
	public void handleNetworkChat(String message) {
		// we are receiving networked chat!
		// first, split it into parts
		// parts = [universe, channel, player, prefix, suffix, emote, message]
		String[] parts = message.split(":", 7);
				
		// make sure we receieved a valid message
		if(parts.length != 7) {
			plugin.debug("receieved network message: " + message);
			return;
		}
		plugin.debug("received network message: " + message);
		
		// now sort out the parameters
		String universe = parts[0];
		String channel = parts[1];
		String player = parts[2];
		String prefix = parts[3];
		String suffix = parts[4];
		Boolean emote = parts[5].equals("1");
		message = parts[6];
		
		// get all the listeners for this channel
		ArrayList<String> listeners = channelManager.getAllListeners(channel, player);
		
		// don't have to check for spam (will be done on the remote server instance)

		// now send the message out!
		String outgoing = new String(plugin.config.options.chatFormat);
		// change the format if it's an emote
		if(emote)
			outgoing = plugin.config.options.emoteFormat;
		// now do all the replacements
		outgoing = outgoing.replace("%universe", universe);
		outgoing = outgoing.replace("%channel", channelManager.getChannelColour(channel) + channel);
		outgoing = outgoing.replace("%prefix",  prefix);
		outgoing = outgoing.replace("%suffix", suffix);
		outgoing = outgoing.replace("%player", player);
		
		// don't have to check colours, will be done remotely
		
		// and add it
		outgoing = outgoing.replace("%message", message);
		// now process the colours
		outgoing = ColourHandler.processColours(outgoing);
		
		// now send it out!
		for(int i = 0; i < listeners.size(); i++) {
			// get the player associated with this name
			Player recipient = plugin.getServer().getPlayer(listeners.get(i));
			if(recipient != null) {				
				if(!timeoutTimers.containsKey(listeners.get(i))) {
					// pass along the message to anyone who's not in timeout
					recipient.sendMessage(outgoing);	
				}
				else {
					// uh-oh, they're in timeout!
					String timeout = new String(plugin.config.options.chatFormat);
					// change the format if it's an emote
					if(emote)
						timeout = plugin.config.options.emoteFormat;
					timeout = timeout.replace("%universe", universe);
					timeout = timeout.replace("%channel", channelManager.getChannelColour(channel) + channel);
					timeout = timeout.replace("%prefix", prefix);
					timeout = timeout.replace("%suffix", suffix);
					timeout = timeout.replace("%player", player);
					timeout = timeout.replace("%message", "&c(You can't hear this because you're in timeout!)");
					recipient.sendMessage(ColourHandler.processColours(timeout));
				}
			}
		}
		
		// don't handle chat bubbles (they're not even on this server!)
		
		// and log it
		plugin.log("{net} " + outgoing);
	}
	
	// set player on a timeout
	public void setTimeout(Player player, Long time) {
		if(!timeoutTimers.containsKey(player.getName())) {
			timeoutTimers.put(player.getName(), time);
			plugin.log(player.getName() + " is now in timeout");
		}
	}
	
	// take a player off timeout
	public void clearTimeout(Player player) {
		if(timeoutTimers.containsKey(player.getName())) {
			timeoutTimers.remove(player.getName());
			plugin.log(player.getName() + " is no longer in timeout");
		}
	}
	
	// see if a player is in timeout
	public Boolean inTimeout(Player player) {
		return timeoutTimers.containsKey(player.getName());
	}
	public Boolean inTimeout(String player) {
		return timeoutTimers.containsKey(player);
	}
	
	// update the timeout timers, taking care of anyone getting cleared from timeout
	public void updateTimeoutTimers() {
		// loop over all players currently on timeout
		for(String name: timeoutTimers.keySet()) {
			// see if the person is online
			Player player = plugin.getServer().getPlayer(name);
			if(player != null && Arrays.asList(plugin.getServer().getOnlinePlayers()).contains(player)) {
				// yup, they're online
				// get their timer!
				timeoutTimers.put(name, timeoutTimers.get(name) - 1);
				if(timeoutTimers.get(name) <= 0L) {
					// time for them to come off timeout!
					this.clearTimeout(player);
					
					// and inform things
					ColourHandler.sendMessage(player, "&aYou are no longer in timeout! You may talk again.");
					
					String message = plugin.config.options.timeoutExpiredMessage;
					message = message.replaceAll("%prefix", plugin.permissions.getUser(player).getPrefix());
					message = message.replaceAll("%suffix", plugin.permissions.getUser(player).getSuffix());
					message = message.replaceAll("%player", player.getName());
					
					// announce it to the server?
					if(plugin.config.options.announceTimeouts) {
						Player[] players = plugin.getServer().getOnlinePlayers();
						for(int i = 0; i < players.length; i++) {
							if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWALL) >= 0) {
								ColourHandler.sendMessage(players[i], message);
							}
						}
					}
				}
			}
		}
	}
	
	// set the entire onTimeout list (for persistance)
	/*@SuppressWarnings("unchecked")
	public void setTimeout(ArrayList<String> players) {
		onTimeout.clear();
		onTimeout = (ArrayList<String>)players.clone();
	}*/
	
	// enable VoxelChat for a player
	public void enableVoxelChat(Player player) {
		if(!voxelChat.contains(player.getName())) {
			//plugin.debug("player " + player.getName() + " has enabled VoxelChat!");
			voxelChat.add(player.getName());
		}
	}
	
	// and remove VoxelChatters when they log off
	public void disableVoxelChat(Player player) {
		if(voxelChat.contains(player.getName())) {
			//plugin.debug("player " + player.getName() + " has disabled VoxelChat!");
			voxelChat.remove(player.getName());
		}
	}
	
	// and toggle VoxelChat manually
	public boolean toggleVoxelChat(Player player) {
		if(voxelChat.contains(player.getName())) {
			//plugin.debug("player " + player.getName() + " has disabled VoxelChat!");
			voxelChat.remove(player.getName());
			return false;
		}
		else {
			//plugin.debug("player " + player.getName() + " has enabled VoxelChat!");
			voxelChat.add(player.getName());
			return true;
		}
	}
	
	// set a player's verbosity
	public void setVerbosity(Player player, Verbosity level) {
		//plugin.debug("Seeing player " + player.getName() + "'s verbosity to: " + level);
		verbosity.put(player.getName(), level);
	}
	
	// set a player's verbosity
	public Verbosity getVerbosity(Player player) {
		//plugin.debug("Getting verbosity for " + player.getName());
		if(!verbosity.containsKey(player.getName())) {
			//plugin.debug("Player did not have verbosity set!");
			setVerbosity(player, Verbosity.SHOWALL);
		}
		//plugin.debug("Player has verbosity: " + verbosity.get(player.getName()));
		return verbosity.get(player.getName());
	}
	
	// toggle if a player has someone muted or not
	public boolean toggleMuted(Player player, Player targetPlayer) {
		if(muted.containsKey(player.getName())) {
			// we're already tracking their muted list
			ArrayList<String> muteList = muted.get(player.getName());
			// now toggle their status
			if(muteList.contains(targetPlayer.getName())) {
				// they're now unmuted!
				muteList.remove(targetPlayer.getName());
				muted.put(player.getName(), muteList);
				return false;
			}
			else {
				// they're now muted!
				muteList.add(targetPlayer.getName());
				muted.put(player.getName(), muteList);
				return true;
			}
		}
		else {
			// we're not tracking them yet, add them!
			ArrayList<String> muteList = new ArrayList<String>();
			muteList.add(targetPlayer.getName());
			muted.put(player.getName(), muteList);
			return true;
		}
	}
	
	public String[] mutedList(Player player) {
		// make sure we have a muted list for them
		if(!muted.containsKey(player.getName())) {
			return new String[0];
		}
		
		ArrayList<String> muteList = muted.get(player.getName());
		// and transform to an array
		String[] muteArray = new String[muteList.size()];
		for(int i = 0; i < muteList.size(); i++) {
			muteArray[i] = muteList.get(i);
		}
		
		return muteArray;
	}
	
	public boolean isPlayerMuted(String muter, String mutee) {
		if(!muted.containsKey(muter)) {
			// they obviously can't have them muted because they don't exist in our records!
			return false;
		}
		
		// return whether they're on the list or not!
		return muted.get(muter).contains(mutee);
	}
	
	public void reloadVerbosities() {
		Player[] online = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < online.length; i++) {
			// get their stored verbosity level
			Verbosity level = plugin.ph.getOfflineVerbosity(online[i].getName());
			setVerbosity(online[i], level);
		}
	}
	
	// keep track of player verbosity
	public enum Verbosity {
		SHOWNONE, SHOWSOME, SHOWALL
	}
	
	// timeout timer class
	public class TimeoutTimerTask extends TimerTask {
		private ChatManager chatManager = null;
		
		public TimeoutTimerTask(ChatManager _chatManager) {
			chatManager = _chatManager;
		}

		@Override
		public void run() {
			try {
				chatManager.updateTimeoutTimers();
			}
			catch(Exception e) {
				chatManager.plugin.error("Timeout timer crashed: " + e.getMessage());
			}
		}
	}
}
