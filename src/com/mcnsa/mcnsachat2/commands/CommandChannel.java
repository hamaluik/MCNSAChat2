package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "ch", usage = "<channel>", description = "changes your channel to <channel>")
public class CommandChannel implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandChannel(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		player.sendMessage(plugin.processColours("&cwut?"));
		return true;
	}
}
