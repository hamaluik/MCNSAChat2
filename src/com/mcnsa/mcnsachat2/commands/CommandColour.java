package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "ccolour", permission = "setcolour", usage = "<colour>", description = "changes the channel colour")
public class CommandColour implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandColour(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// make sure we have args
		if(sArgs.length() < 1) {
			// no args :(
			return false;
		}
		
		// parse the colour
		// first try to parse the text for colour
		String col = new String("");
		Integer colourInt = new Integer(0);
		try {
			colourInt = Integer.parseInt(sArgs);
			// make sure it's in range!
			if(colourInt >= 0 && colourInt <= 15) {
				col = "&" + Integer.toHexString(colourInt);
			}
		}
		catch(Exception e) {
			// ok, colour wouldn't parse as int. Try parsing as text
			col = ColourHandler.translateName(sArgs);
		}
		
		// catch an empty colour string
		if(col.equals("")) {
			ColourHandler.sendMessage(player, "&cInvalid colour! &aValid colours: &0black, &1dark blue, &2dark green, &3dark teal, &4dark red, &5purple, &6gold, &7grey, &8dark grey, &9blue, &agreen, &bteal, &cred, &dpink, &eyellow, &fwhite");
			return true;
		}
		
		// and change the colour!
		plugin.channelManager.setColour(plugin.channelManager.getPlayerChannel(player), col);
		
		// and we handled it!
		return true;
	}
}
