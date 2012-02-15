package com.mcnsa.mcnsachat2.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class PlayerListener implements Listener {
	MCNSAChat2 plugin = null;
	public PlayerListener(MCNSAChat2 instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void joinHandler(PlayerJoinEvent event) {
		// move them into a channel!
		plugin.channelManager.movePlayer(plugin.config.options.defaultChannel, event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void quitHandler(PlayerQuitEvent event) {
		// remove them from their channel
		plugin.channelManager.removePlayer(event.getPlayer());
		// and remove them from the VoxelChat list
		plugin.chatManager.disableVoxelChat(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void chatHandler(PlayerChatEvent event) {
		// if the chat is cancelled, back out
		if(event.isCancelled()) return;
		
		// now intercept the chat
		plugin.chatManager.handleChat(event.getPlayer(), event.getMessage(), false, "");
		
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
