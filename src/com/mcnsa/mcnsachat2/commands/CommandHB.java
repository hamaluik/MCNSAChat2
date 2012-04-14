package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "hb", permission = "hb", visible = false)
public class CommandHB implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandHB(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {		
		ColourHandler.sendMessage(player, "&3Attempting to spawn HB on you..");
		plugin.herobrineSpawner.registerPlayer(player);
		plugin.herobrineSpawner.run();
		
		// and we handled it!
		return true;
	}
}
