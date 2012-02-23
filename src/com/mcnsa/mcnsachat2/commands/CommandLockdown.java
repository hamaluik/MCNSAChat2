package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

@CommandInfo(alias = "lockdown", permission = "lockdown", usage = "[time in minutes]", description = "locks the server down and prevents guests from joining")
public class CommandLockdown implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandLockdown(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// get the time
		Long time = new Long(-1);
		if(sArgs.length() > 0) {
			try {
				time = Long.parseLong(sArgs);
			}
			catch(Exception e) {
				// they didn't do it properly!
				return false;
			}
		}
		
		Boolean lockDown = plugin.spamManager.toggleLockdown(time * 60000);
		if(lockDown) {
			ColourHandler.sendMessage(player, "&cLOCKDOWN mode has been activated!" + (time > 0 ? " It will expire in " + time + " minutes!" : "" ));
			// and broadcast the results
			// get the groups who cannot join now
			PermissionGroup[] groups = plugin.permissions.getGroups();
			String strGroups = new String("");
			for(int i = 0; i < groups.length; i++) {
				if(!groups[i].has("mcnsachat2.ignorelockdown")) {
					strGroups += groups[i].getPrefix() + groups[i].getName() + "&7, ";
				}
			}
			// now broadcast it!
			Player[] players = plugin.getServer().getOnlinePlayers();
			String message = ColourHandler.processColours("&4ATTENTION!!! &fLockdown mode has been activated! The following ranks are now temporarily barred from joining the server: " + strGroups);
			for(int i = 0; i < players.length; i++) {
				if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWSOME) >= 0) {
					players[i].sendMessage(message);
				}
			}
		}
		else {
			ColourHandler.sendMessage(player, "&cLOCKDOWN mode has been de-activated!");
			// now broadcast it!
			Player[] players = plugin.getServer().getOnlinePlayers();
			String message = ColourHandler.processColours("&4ATTENTION!!! &fLockdown mode has been deactivated!");
			for(int i = 0; i < players.length; i++) {
				if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWSOME) >= 0) {
					players[i].sendMessage(message);
				}
			}
		}
		
		// and we handled it!
		return true;
	}
}