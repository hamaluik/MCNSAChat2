package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "@$&#_voxelplayer", visible = false)
public class CommandVoxelChat implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandVoxelChat(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {		
		// handle the hidden command!
		plugin.chatManager.enableVoxelChat(player);
		
		// and we handled it!
		return true;
	}
}
