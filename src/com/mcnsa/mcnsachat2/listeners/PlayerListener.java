package com.mcnsa.mcnsachat2.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.tehkode.permissions.PermissionGroup;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;
import com.mcnsa.mcnsachat2.util.SpamManager.MiniBanReason;

public class PlayerListener implements Listener {
	MCNSAChat2 plugin = null;
	public PlayerListener(MCNSAChat2 instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void loginHandler(PlayerLoginEvent event) {
		// see if they're even allowed to join!
		MiniBanReason result = plugin.spamManager.canPlayerJoin(event.getPlayer());
		if(result == MiniBanReason.SPAM) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, plugin.config.options.spamConfig.miniBanMessage);
		}
		else if(result == MiniBanReason.LOCKDOWN) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, plugin.config.options.spamConfig.lockdownMessage);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void joinHandler(PlayerJoinEvent event) {
		
		// move them into their channel!
		String channel = plugin.ph.getOfflineChannel(event.getPlayer().getName());
		// create the channel if it doesn't exist
		plugin.channelManager.createChannelIfNotExists(channel);
		// and move into it!
		plugin.channelManager.movePlayer(channel, event.getPlayer(), true);
		
		// get their stored verbosity level
		Verbosity level = plugin.ph.getOfflineVerbosity(event.getPlayer().getName());
		plugin.chatManager.setVerbosity(event.getPlayer(), level);
		
		// turn off the global join notification
		event.setJoinMessage("");
		
		// and do our own custom notification
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWSOME) >= 0) {
				// they want these messages! show them!
				// TODO: custom join / leave messages
				ColourHandler.sendMessage(players[i], plugin.permissions.getUser(event.getPlayer()).getPrefix() + event.getPlayer().getName() + " &ehas joined the game!");
			}
		}
		
		// and warn them of lockdowns
		if(plugin.spamManager.onLockdown() && plugin.chatManager.getVerbosity(event.getPlayer()).compareTo(Verbosity.SHOWSOME) >= 0) {
			PermissionGroup[] groups = plugin.permissions.getGroups();
			String strGroups = new String("");
			for(int i = 0; i < groups.length; i++) {
				if(!groups[i].has("mcnsachat2.ignorelockdown")) {
					strGroups += groups[i].getPrefix() + groups[i].getName() + "&7, ";
				}
			}
			ColourHandler.sendMessage(event.getPlayer(), "&4ATTENTION!!! &fLockdown mode is activated! The following ranks are temporarily barred from joining the server: " + strGroups);
		}
		
		// now send coloured names to those with voxelchat
		for(int i = 0; i < plugin.chatManager.voxelChat.size(); i++) {
			Player target = plugin.getServer().getPlayerExact(plugin.chatManager.voxelChat.get(i));
			if(target != null) {
				target.sendMessage("\247c\247a\2471\2473\247d\247eq?=$vp=" + event.getPlayer().getName() + "," + plugin.permissions.getUser(event.getPlayer()).getPrefix().replace("&", ""));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void quitHandler(PlayerQuitEvent event) {
		// update their spam status
		plugin.spamManager.playerQuit(event.getPlayer());
		
		// set their persistance
		plugin.ph.setOfflineChannel(event.getPlayer().getName(), plugin.channelManager.getPlayerChannel(event.getPlayer()));
		plugin.ph.setOfflineVerbosity(event.getPlayer().getName(), plugin.chatManager.getVerbosity(event.getPlayer()));
		
		// remove them from their channel
		plugin.channelManager.removePlayer(event.getPlayer());
		// and remove them from the VoxelChat list
		plugin.chatManager.disableVoxelChat(event.getPlayer());
		
		// turn off the global join notification
		event.setQuitMessage("");
		
		// and do our own custom notification
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			if(plugin.chatManager.getVerbosity(players[i]).compareTo(Verbosity.SHOWSOME) >= 0) {
				// they want these messages! show them!
				// TODO: custom join / leave messages
				ColourHandler.sendMessage(players[i], plugin.permissions.getUser(event.getPlayer()).getPrefix() + event.getPlayer().getName() + " &ehas left the game!");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void chatHandler(PlayerChatEvent event) {
		// if the chat is cancelled, back out
		if(event.isCancelled()) return;
		
		// and cancel the event!
		event.setCancelled(true);
		
		// filter the annoying rei's minimap
		if(event.getMessage().startsWith("u00")) {
			return;
		}
		
		// now intercept the chat
		boolean chatSent = plugin.chatManager.handleChat(event.getPlayer(), event.getMessage(), false, "", true);
		
		if(!chatSent) {
			plugin.debug("no network message (chat not sent)");
			return;
		}

		// only if they're not spamming / on timeout and the network layer is enabled!
		if(plugin.netManager != null) {
			// send to our network layer
			plugin.debug("sending network chat");
			plugin.chatManager.sendNetworkChat(event.getPlayer(), event.getMessage(), false, "", true);
		}
		else {
			plugin.debug("message not broadcasted due to netManager being null");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void preprocessHandler(PlayerCommandPreprocessEvent event) {
		// if the command is cancelled, back out
		if(event.isCancelled()) return;
		
		// intercept the command
		if(plugin.commandManager.handleCommand(event.getPlayer(), event.getMessage())) {
			// we handled it, cancel it
			event.setCancelled(true);
		}
	}
}
