package com.mcnsa.mcnsachat2.commands;

import java.util.Arrays;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

@CommandInfo(alias = "clock", permission = "lock", usage = "[player]", description = "locks [player]'s channel or lists those who are currently locked")
public class CommandLock implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandLock(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1) {
			// get a list of who are locked
			String[] locked = plugin.channelManager.listLocked();
			Arrays.sort(locked);
			String message = "&7Players have been locked: ";
			for(int i = 0; i < locked.length; i++) {
				message += "&f" + locked[i] + "&7, ";
			}
			player.sendMessage(plugin.processColours(message));
			return true;
		}
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(sArgs.trim());
		// make sure they're a valid player
		if(targetPlayer == null) {
			player.sendMessage(plugin.processColours("&cError: I could not find the player '&f" + sArgs.trim() + "&c'!"));
			return true;
		}
		
		// toggle their locked status
		boolean locked = plugin.channelManager.toggleLocked(targetPlayer);
		
		// and return a message
		if(locked) {
			player.sendMessage(plugin.processColours(plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7has been &clocked &7in their channel!"));
			if(plugin.chatManager.getVerbosity(targetPlayer).compareTo(Verbosity.SHOWSOME) >= 0) {
				targetPlayer.sendMessage(plugin.processColours("&7You have been &clocked &7in your channel!"));
			}
		}
		else {
			player.sendMessage(plugin.processColours(plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7has been &aunlocked &7from their channel!"));
			if(plugin.chatManager.getVerbosity(targetPlayer).compareTo(Verbosity.SHOWSOME) >= 0) {
				targetPlayer.sendMessage(plugin.processColours("&7You have been &aunlocked &7from your channel!"));
			}
		}
		
		// and we handled it!
		return true;
	}
}
