package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

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
				inTimeout += "&f" + plugin.chatManager.onTimeout.get(i) + "&7, ";
			}
			// and alert them
			player.sendMessage(plugin.processColours(inTimeout));
			
			// we handled it
			return true;
		}		
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(sArgs.trim());
		// make sure they're a valid player
		if(targetPlayer == null) {
			player.sendMessage(plugin.processColours("&cError: I could not find the player '&f" + sArgs.trim() + "&c'!"));
			return true;
		}
		
		// toggle their status!
		Boolean onTimeout = plugin.chatManager.toggleTimeout(targetPlayer);
		
		// and alert them to their status
		if(onTimeout) {
			player.sendMessage(plugin.processColours("&f" + targetPlayer.getName() + " &7is now on &fTIMEOUT&7! DON'T FORGET TO REMOVE THEIR TIMEOUT STATUS (no timer is implemented yet)!"));
			targetPlayer.sendMessage(plugin.processColours("&cYou have been placed in timeout for being naughty. No more chat for you."));
			plugin.log(player.getName() + " placed " + targetPlayer.getName() + " in timeout!");
			
			// announce it to the server?
			if(plugin.config.options.announceTimeouts) {
				Player[] players = plugin.getServer().getOnlinePlayers();
				for(int i = 0; i < players.length; i++) {
					players[i].sendMessage(plugin.processColours("Attention&7: &f" + targetPlayer.getName() + " &7has been placed in timeout for being &cnaughty&7!"));
				}
			}
		}
		else {
			player.sendMessage(plugin.processColours("&f" + targetPlayer.getName() + " &7is no longer on timeout!"));
			targetPlayer.sendMessage(plugin.processColours("&aYou are no longer in timeout! You may talk again."));
			plugin.log(player.getName() + " removed " + targetPlayer.getName() + " from timeout!");
			
			// announce it to the server?
			if(plugin.config.options.announceTimeouts) {
				Player[] players = plugin.getServer().getOnlinePlayers();
				for(int i = 0; i < players.length; i++) {
					players[i].sendMessage(plugin.processColours("Attention&7: &f" + targetPlayer.getName() + " &7has been removed from timeout for being &anice&7!"));
				}
			}
		}
		
		// and we handled it!
		return true;
	}
}