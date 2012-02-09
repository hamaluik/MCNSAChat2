package com.mcnsa.mcnsachat2;

import com.mcnsa.mcnsachat2.listeners.PlayerListener;
import com.mcnsa.mcnsachat2.util.*;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class MCNSAChat2 extends JavaPlugin {
	// load the minecraft logger
	Logger log = Logger.getLogger("Minecraft");
	
	// keep track of permissions
	public PermissionManager permissions = null;
	
	// keep track of configuration options
	ConfigManager config = new ConfigManager(this);
	
	// keep track of listeners
	PlayerListener playerListener = null;
	
	// and keep track of the chat and channel handlers
	ChannelHandler channelHandler = null;
	
	public void onEnable() {
		// set up permissions
		this.setupPermissions();
		
		// load configuration
		// and save it again (for defaults)
		this.getConfig().options().copyDefaults(true);
		config.load(getConfig());
		this.saveConfig();
		
		// set up listeners
		playerListener = new PlayerListener(this);
		
		// set up the channel handler
		channelHandler = new ChannelHandler(config);
		
		// 
		
		// routines for when the plugin gets enabled
		log("plugin enabled!");
	}
	
	public void onDisable() {
		// shut the plugin down
		log("plugin disabled!");
	}
	
	// for simpler logging
	public void log(String info) {
		log.info("[MCNSAChat2] " + info);
	}
	
	// for error reporting
	public void error(String info) {
		log.info("[MCNSAChat2] <ERROR> " + info);
	}
	
	// for debugging
	// (disable for final release)
	public void debug(String info) {
		log.info("[MCNSAChat2] <DEBUG> " + info);
	}

	// load the permissions plugin
	public void setupPermissions() {
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
			this.permissions = PermissionsEx.getPermissionManager();
			log("permissions successfully loaded!");
		}
		else {
			error("PermissionsEx not found!");
		}
	}
	
	// just an interface function for checking permissions
	// if permissions are down, default to OP status.
	public boolean hasPermission(Player player, String permission) {
		if(permissions != null) {
			return permissions.has(player, permission);
		}
		else {
			return player.isOp();
		}
	}
	
	// allow for colour tags to be used in strings..
	public String processColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	
	// strip colour tags from strings..
	public String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "");
	}
}
