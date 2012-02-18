package com.mcnsa.mcnsachat2.commands;

import java.util.Arrays;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
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
				message += "&f" + locked[i];
				if(i < locked.length - 1) {
					message += "&7, ";
				}
			}
			ColourHandler.sendMessage(player, message);
			return true;
		}
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(sArgs.trim());
		// make sure they're a valid player
		if(targetPlayer == null) {
			ColourHandler.sendMessage(player, "&cError: I could not find the player '&f" + sArgs.trim() + "&c'!");
			return true;
		}
		
		// toggle their locked status
		boolean locked = plugin.channelManager.toggleLocked(targetPlayer);
		
		// and return a message
		if(locked) {
			ColourHandler.sendMessage(player, plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7has been &clocked &7in their channel!");
			if(plugin.chatManager.getVerbosity(targetPlayer).compareTo(Verbosity.SHOWSOME) >= 0) {
				ColourHandler.sendMessage(targetPlayer, "&7You have been &clocked &7in your channel!");
			}
		}
		else {
			ColourHandler.sendMessage(player, plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7has been &aunlocked &7from their channel!");
			if(plugin.chatManager.getVerbosity(targetPlayer).compareTo(Verbosity.SHOWSOME) >= 0) {
				ColourHandler.sendMessage(targetPlayer, "&7You have been &aunlocked &7from your channel!");
			}
		}
		
		// and we handled it!
		return true;
	}
}
