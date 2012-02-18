package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

@CommandInfo(alias = "ct", permission = "timeout", usage = "[player]", description = "toggles whether a player is in timeout, or lists those in timeout if no arguments are given")
public class CommandTimeout implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandTimeout(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// list the players in timeout if there are no args
		if(sArgs.trim().equals("")) {
			String inTimeout = new String("&7Players in timeout: ");
			for(int i = 0; i < plugin.chatManager.onTimeout.size(); i++) {
				inTimeout += "&f" + plugin.chatManager.onTimeout.get(i);
				if(i < plugin.chatManager.onTimeout.size() - 1) {
					inTimeout += "&7, ";
				}
			}
			// and alert them
			ColourHandler.sendMessage(player, inTimeout);
			
			// we handled it
			return true;
		}
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(sArgs.trim());
		// make sure they're a valid player
		if(targetPlayer == null) {
			ColourHandler.sendMessage(player, "&cError: I could not find the player '&f" + sArgs.trim() + "&c'!");
			return true;
		}
		
		// toggle their status!
		Boolean onTimeout = plugin.chatManager.toggleTimeout(targetPlayer);
		
		// and alert them to their status
		if(onTimeout) {
			ColourHandler.sendMessage(player, "&f" + targetPlayer.getName() + " &7is now on &fTIMEOUT&7! DON'T FORGET TO REMOVE THEIR TIMEOUT STATUS (no timer is implemented yet)!");
			ColourHandler.sendMessage(targetPlayer, "&cYou have been placed in timeout for being naughty. No more chat for you.");
			plugin.log(player.getName() + " placed " + targetPlayer.getName() + " in timeout!");
			
			// announce it to the server?
			if(plugin.config.options.announceTimeouts) {
				Player[] players = plugin.getServer().getOnlinePlayers();
				for(int i = 0; i < players.length; i++) {
					if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWALL) >= 0) {
						ColourHandler.sendMessage(players[i], "Attention&7: &f" + targetPlayer.getName() + " &7has been placed in timeout for being &cnaughty&7!");
					}
				}
			}
		}
		else {
			ColourHandler.sendMessage(player, "&f" + targetPlayer.getName() + " &7is no longer on timeout!");
			ColourHandler.sendMessage(targetPlayer, "&aYou are no longer in timeout! You may talk again.");
			plugin.log(player.getName() + " removed " + targetPlayer.getName() + " from timeout!");
			
			// announce it to the server?
			if(plugin.config.options.announceTimeouts) {
				Player[] players = plugin.getServer().getOnlinePlayers();
				for(int i = 0; i < players.length; i++) {
					if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWALL) >= 0) {
						ColourHandler.sendMessage(players[i], "Attention&7: &f" + targetPlayer.getName() + " &7has been removed from timeout for being &anice&7!");
					}
				}
			}
		}
		
		// and we handled it!
		return true;
	}
}