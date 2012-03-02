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
		boolean chatSent = plugin.chatManager.handleChat(player, sArgs, true, "", true);
		
		if(!chatSent) {
			plugin.debug("no network message (chat not sent)");
			return true;
		}

		// only if they're not spamming / on timeout and the network layer is enabled!
		if(plugin.netManager != null) {
			// send to our network layer
			plugin.debug("sending network chat");
			plugin.chatManager.sendNetworkChat(player, sArgs, true, "", true);
		}
		else {
			plugin.debug("message not broadcasted due to netManager being null");
		}
		
		// and we handled it!
		return true;
	}
}
