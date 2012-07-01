package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

@CommandInfo(alias = "ct", permission = "timeout", usage = "[player] [time] [reason]", description = "toggles player timeout status (time like Hawkeye), or lists those in timeout if no arguments are given")
public class CommandTimeout implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandTimeout(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public boolean isLong(String str) {
		try {
			Long.parseLong(str);
		}
		catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public Boolean handle(Player player, String sArgs) {
		// list the players in timeout if there are no args
		if(sArgs.trim().equals("")) {
			String inTimeout = new String("&7Players in timeout: ");
			/*for(int i = 0; i < plugin.chatManager.onTimeout.size(); i++) {
				inTimeout += "&f" + plugin.chatManager.onTimeout.get(i);
				if(i < plugin.chatManager.onTimeout.size() - 1) {
					inTimeout += "&7, ";
				}
			}*/
			for(String name: plugin.chatManager.timeoutTimers.keySet()) {
				inTimeout += plugin.permissions.getUser(name).getPrefix() + name + " &7(&9" + plugin.formatTime(plugin.chatManager.timeoutTimers.get(name)) + "&7), ";
			}
			// and alert them
			ColourHandler.sendMessage(player, inTimeout);
			
			// we handled it
			return true;
		}

		// get the args
		// make sure we have valid args
		String[] args = sArgs.trim().split("\\s");
		String reason = new String("");
		String targetName = new String("");
		Long timeoutTime = -1L;
		if(args.length >= 2) {
			// parse the args
			targetName = args[0];
			try {
				long weeks = 0;
				long days = 0;
				long hours = 0;
				long mins = 0;
				long secs = 0;
				
				String nums = "";
				for(int j = 0; j < args[1].length(); j++) {
					String c = args[1].substring(j, j+1);
					if(isLong(c)) {
						nums += c;
						continue;
					}
					
					long num = Long.parseLong(nums);
					if(c.equals("w")) weeks = num;
					else if(c.equals("d")) days = num;
					else if(c.equals("h")) hours = num;
					else if(c.equals("m")) mins = num;
					else if(c.equals("s")) secs = num;
					else throw new IllegalArgumentException("invalid time measurement: &7" + c);
					nums = "";
				}
				
				// now get the total time
				timeoutTime = secs + (mins * 60) + (hours * 3600) + (days * 86400) + (weeks * 604800);
			}
			catch(Exception e) {
				ColourHandler.sendMessage(player, "&cError: " + e.getMessage());
				return false;
			}
			
			// make sure it's a valid time
			if(timeoutTime < 0 || timeoutTime > 9223372036854775807L) {
				ColourHandler.sendMessage(player, "&cError: that is an invalid time frame!");
				return false;
			}
			
			// fill out the reason
			for(int i = 2; i < args.length; i++) {
				reason += args[i] + " ";
			}
			if(args.length < 3) {
				reason = "no reason";
			}
		}
		else if(args.length == 1) {
			// no time limit, they want to remove them from timeout
			targetName = args[0];
		}
		
		
		// get the targeted player
		Player targetPlayer = plugin.getServer().getPlayer(targetName);
		// make sure they're a valid player
		if(targetPlayer == null) {
			ColourHandler.sendMessage(player, "&cError: I could not find the player '&f" + sArgs.trim() + "&c'!");
			return true;
		}
		
		// see if we're trying to remove them
		if(timeoutTime < 0) {
			if(plugin.chatManager.inTimeout(targetPlayer)) {
				// they ARE in timeout
				// remove them!
				plugin.chatManager.clearTimeout(targetPlayer);
				
				ColourHandler.sendMessage(player, "&f" + targetPlayer.getName() + " &7is no longer on timeout!");
				ColourHandler.sendMessage(targetPlayer, "&aYou are no longer in timeout! You may talk again.");
				plugin.log(player.getName() + " removed " + targetPlayer.getName() + " from timeout!");
				
				String message = plugin.config.options.removedFromTimeoutMessage;
				message = message.replaceAll("%player", targetPlayer.getName());
				message = message.replaceAll("%prefix", plugin.permissions.getUser(targetPlayer).getPrefix());
				message = message.replaceAll("%suffix", plugin.permissions.getUser(targetPlayer).getSuffix());
				
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
			else {
				ColourHandler.sendMessage(player, "&cError: " + targetPlayer.getName() + " was not in a timeout!");
			}
			
			// and we're done
			return true;
		}
		
		// ok, if we got here, they're going for a timeout
		
		// check to see if they're already on a timeout
		if(plugin.chatManager.inTimeout(targetPlayer)) {
			ColourHandler.sendMessage(player, "&cError: " + targetPlayer.getName() + " is already in a timeout!");
			return true;
		}
		
		// book'em, Danno!
		plugin.chatManager.setTimeout(targetPlayer, timeoutTime);
		
		String sentToMessage = new String(plugin.config.options.sentToTimeoutMessage);
		sentToMessage = sentToMessage.replaceAll("%player", targetPlayer.getName());
		sentToMessage = sentToMessage.replaceAll("%prefix", plugin.permissions.getUser(targetPlayer).getPrefix());
		sentToMessage = sentToMessage.replaceAll("%suffix", plugin.permissions.getUser(targetPlayer).getSuffix());
		sentToMessage = sentToMessage.replaceAll("%time", plugin.formatTime(timeoutTime));
		sentToMessage = sentToMessage.replaceAll("%reason", reason);
		
		// and alert!
		ColourHandler.sendMessage(player, plugin.permissions.getUser(targetPlayer).getPrefix() + targetPlayer.getName() + " &7is now on &fTIMEOUT&7!");
		ColourHandler.sendMessage(targetPlayer, sentToMessage);
		plugin.log(player.getName() + " placed " + targetPlayer.getName() + " in timeout!");
		
		String message = plugin.config.options.inTimeoutMessage;
		message = message.replaceAll("%player", targetPlayer.getName());
		message = message.replaceAll("%prefix", plugin.permissions.getUser(targetPlayer).getPrefix());
		message = message.replaceAll("%suffix", plugin.permissions.getUser(targetPlayer).getSuffix());
		message = message.replaceAll("%time", plugin.formatTime(timeoutTime));
		message = message.replaceAll("%reason", reason);
		
		// announce it to the server?
		if(plugin.config.options.announceTimeouts) {
			Player[] players = plugin.getServer().getOnlinePlayers();
			for(int i = 0; i < players.length; i++) {
				if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWALL) >= 0) {
					ColourHandler.sendMessage(players[i], message);
				}
			}
		}
		
		// and we handled it!
		return true;
	}
}