package com.mcnsa.mcnsachat2.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ColourHandler;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

public class PlayerListener implements Listener {
	MCNSAChat2 plugin = null;
	public PlayerListener(MCNSAChat2 instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void quitHandler(PlayerQuitEvent event) {
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
		
		// now intercept the chat
		plugin.chatManager.handleChat(event.getPlayer(), event.getMessage(), false, "", true);
		
		// and cancel the event!
		event.setCancelled(true);
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
