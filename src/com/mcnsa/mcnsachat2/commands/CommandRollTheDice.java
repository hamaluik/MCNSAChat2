package com.mcnsa.mcnsachat2.commands;

import java.util.Random;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "rtd", permission = "rtd", usage = "[max number]", description = "rolls the dice!")
public class CommandRollTheDice implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandRollTheDice(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// default roll max is 6
		Integer max = 6;
		
		if(sArgs.trim().length() > 0) {
			try {
				max = Integer.parseInt(sArgs.trim());
			}
			catch(Exception e) {
				
			}
		}
		
		Random generator = new Random();
		Integer roll = generator.nextInt(max) + 1;
		
		// and announce!
		plugin.chatManager.handleChat(player, ColourHandler.processColours("rolled a &e" + roll + " &7on his &f" + max + "&7-sided die!"), true, "", false);
		
		// and we handled it!
		return true;
	}
}

