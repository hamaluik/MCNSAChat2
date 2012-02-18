package com.mcnsa.mcnsachat2;

import com.mcnsa.mcnsachat2.listeners.PlayerListener;
import com.mcnsa.mcnsachat2.util.*;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class MCNSAChat2 extends JavaPlugin {
	// load the minecraft logger
	Logger log = Logger.getLogger("Minecraft");

	// keep track of permissions
	public PermissionManager permissions = null;

	// keep track of configuration options
	public ConfigManager config = null;

	// and commands
	public CommandManager commandManager = null;

	// keep track of listeners
	public PlayerListener playerListener = null;

	// and keep track of the chat and channel handlers
	public ChannelManager channelManager = null;
	public ChatManager chatManager = null;
	
	// and peristance!
	public PersistanceHandler ph = null;

	public void onEnable() {
		// set up permissions
		this.setupPermissions();
		
		// set up
		//debug("loading configuration manager..");
		config = new ConfigManager(this);
		//debug("loading command manager..");
		commandManager = new CommandManager(this);

		// load configuration
		// and save it again (for defaults)
		this.getConfig().options().copyDefaults(true);
		if(!config.load(getConfig())) {
			// shit
			// BAIL
			error("configuration failed - bailing");
			getServer().getPluginManager().disablePlugin(this);
		}
		this.saveConfig();
		
		// set persistance handler
		ph = new PersistanceHandler(this);

		// set up listeners
		playerListener = new PlayerListener(this);

		// and the chat manager
		chatManager = new ChatManager(this);

		// set up the channel handler
		channelManager = new ChannelManager(this, config);
		
		// and set the chat manager's channel manager
		chatManager.setChannelManager(channelManager);
		
		// and load the persistance
		log("loading persistance..");
		ph.readPersistance();
		
		// and send people to their appropriate channels (in case of reload)
		channelManager.reloadChannels();
		chatManager.reloadVerbosities();
		
		// routines for when the plugin gets enabled
		log("plugin enabled!");
	}

	public void onDisable() {
		ph.writePersistance();
		
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
			return permissions.has(player, "mcnsachat2." + permission);
		}
		else {
			return player.isOp();
		}
	}
	
	public boolean playerWithinRadius(Player player1, Player player2, Integer radius) {
		// make sure they're in the same world
		if(player1.getWorld() != player2.getWorld()) return false;
		
		List<Entity> nearby = player1.getNearbyEntities(radius, radius, radius);
		for(int i = 0; i < nearby.size(); i++) {
			if(nearby.get(i) instanceof Player) {
				if(((Player)nearby.get(i)).getName().equals(player2.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
