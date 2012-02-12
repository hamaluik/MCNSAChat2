package com.mcnsa.mcnsachat2.commands;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ChannelManager.Channel;
import com.mcnsa.mcnsachat2.util.Command;
import com.mcnsa.mcnsachat2.util.CommandInfo;

@CommandInfo(alias = "clist", permission = "list", usage = "", description = "lists all available channels")
public class CommandList implements Command {
	private static MCNSAChat2 plugin = null;
	public CommandList(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean handle(Player player, String sArgs) {
		// get a list of all channels from the channel manager
		Channel[] channels = plugin.channelManager.listChannels();
		
		// loop through them and make sure the player has permission to see each one
		String chStr = new String("Available channels: ");
		for(int i = 0; i < channels.length; i++) {
			plugin.debug("channel " + channels[i].name + " has permission: mcnsachat2.channel." + channels[i].permission);
			if(channels[i].permission.equals("") || plugin.hasPermission(player, "channel." + channels[i].permission)) {
				// the channel has permissions and we have them!
				chStr += channels[i].colour + channels[i].name + "&7, ";
			}
		}
		
		// and send it!
		player.sendMessage(plugin.processColours(chStr));
		
		// and we handled it!
		return true;
	}
}
