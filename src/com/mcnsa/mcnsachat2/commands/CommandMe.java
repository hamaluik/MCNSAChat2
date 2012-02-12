package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "me", permission = "me", usage = "<message>", description = "emotes your message")
public class CommandMe implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandMe(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// make sure we have args
		if(sArgs.length() < 1) {
			// no args :(
			return false;
		}
		
		// handle the /me
		plugin.chatManager.handleChat(player, sArgs, true);
		
		// and we handled it!
		return true;
	}
}
