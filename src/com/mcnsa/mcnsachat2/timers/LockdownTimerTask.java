package com.mcnsa.mcnsachat2.timers;

import java.util.TimerTask;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

public class LockdownTimerTask extends TimerTask {
	private MCNSAChat2 plugin;
	
	public LockdownTimerTask(MCNSAChat2 instance) {
		plugin = instance;
	}

	@Override
	public void run() {
		boolean lockdown = plugin.spamManager.toggleLockdown(new Long(-1));
		if(lockdown) {
			plugin.spamManager.toggleLockdown(new Long(-1));
		}
		// now broadcast it!
		Player[] players = plugin.getServer().getOnlinePlayers();
		String message = ColourHandler.processColours("&4ATTENTION!!! &fLockdown mode has been &fde&7activated!");
		for(int i = 0; i < players.length; i++) {
			if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWSOME) >= 0) {
				players[i].sendMessage(message);
			}
		}
	}
}
