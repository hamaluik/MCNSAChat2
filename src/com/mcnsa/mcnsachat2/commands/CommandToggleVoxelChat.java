package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "cvc", usage = "", description = "toggles whether you see VoxelChat bubbles or not")
public class CommandToggleVoxelChat implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandToggleVoxelChat(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		boolean voxelChat = plugin.chatManager.toggleVoxelChat(player);
		
		// and alert them to their status
		if(voxelChat) {
			player.sendMessage(plugin.processColours("&7You can now see chat bubbles!"));
		}
		else {
			player.sendMessage(plugin.processColours("&7You can no longer see chat bubbles!"));
		}
		
		// and we handled it!
		return true;
	}
}
