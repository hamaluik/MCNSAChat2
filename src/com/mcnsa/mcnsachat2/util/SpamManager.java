package com.mcnsa.mcnsachat2.util;

import java.util.ArrayDeque;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class SpamManager {
	private MCNSAChat2 plugin = null;
	// keep track of the times they last chatted
	private HashMap<String, ArrayDeque<Long>> chatTimes = new HashMap<String, ArrayDeque<Long>>();

	public SpamManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public Boolean checkSpam(Player player) {
		String playerName = player.getName();
		
		// get the current time (when they're chatting)
		long now = System.currentTimeMillis();
		
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
			return true;
		}
		
		// guess not
		return false;
	}
}
