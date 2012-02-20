package com.mcnsa.mcnsachat2.util;

import java.util.ArrayDeque;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class SpamManager {
	private MCNSAChat2 plugin = null;
	// keep track of the times they last chatted
	private HashMap<String, ArrayDeque<Long>> chatTimes = new HashMap<String, ArrayDeque<Long>>();
	// and the times they last joined
	private HashMap<String, Long> joinTimes = new HashMap<String, Long>();
	// and who is barred from joining (and for how long)
	private HashMap<String, Long> allowableRejoinTimes = new HashMap<String, Long>();

	public SpamManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public Boolean checkChatSpam(Player player) {
		// get the current time (when they're chatting)
		long now = System.currentTimeMillis();
		
		// check to see if we even care
		if(plugin.hasPermission(player, "ignorespam")) {
			return false;
		}
		
		String playerName = player.getName();
		
		// and get the chat times for them
		ArrayDeque<Long> times = new ArrayDeque<Long>();
		if(chatTimes.containsKey(playerName)) {
			// add this chat to their list!
			times.addAll(chatTimes.get(playerName));
		}
		
		// add this talking instance to the spam queue
		times.add(now);
		
		// pop the head off if necessary
		if(times.size() > plugin.config.options.spamConfig.messageLimit) {
			times.remove();
		}
		
		// and put it back in
		chatTimes.put(playerName, times);
		
		// check to see if they're spamming
		if(times.size() == plugin.config.options.spamConfig.messageLimit && (times.getLast() - times.getFirst()) < plugin.config.options.spamConfig.messagePeriod) {
			// they ARE spamming!
			plugin.log(playerName + " is spamming!");
			return true;
		}
		
		// guess not
		return false;
	}
	
	// see if they're barred from joining!
	public Boolean canPlayerJoin(Player player) {
		long now = System.currentTimeMillis();
		
		// check to see if we even care
		if(plugin.hasPermission(player, "ignorespam")) {
			return true;
		}
		
		// check to see if they're allowed to rejoin
		// check first so they don't keep resetting their time by attempting to reconnect
		if(allowableRejoinTimes.containsKey(player.getName())) { 
			// they're currently barred
			// see if they should be unbarred
			if(now < allowableRejoinTimes.get(player.getName())) {
				// nope, not getting unbarred yet!
				plugin.log(player.getName() + " was barred from joining for join/leave spamming!");
				return false;
			}
		}
		
		// ok, they've waited long enough or weren't having issues to begin with
		// log when they joined
		joinTimes.put(player.getName(), now);
		
		// if we get here, they're allowed to join
		return true;
	}
	
	public void playerQuit(Player player) {
		// get the current time
		long now = System.currentTimeMillis();
		
		// check to see if we even care
		if(plugin.hasPermission(player, "ignorespam")) {
			return;
		}
		
		// if we weren't tracking them, we don't care
		if(!joinTimes.containsKey(player.getName())) {
			return;
		}
		
		// now see if they rejoined quickly
		if((now - joinTimes.get(player.getName())) < plugin.config.options.spamConfig.minOnlineTime) {
			// they left too fast!
			// book em, Dan!
			allowableRejoinTimes.put(player.getName(), now + plugin.config.options.spamConfig.miniBanTime.longValue());
			plugin.log(player.getName() + " was mini-banned for join/leave spamming!");
		}
	}
}
