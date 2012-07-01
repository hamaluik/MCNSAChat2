package com.mcnsa.mcnsachat2.util;

import com.mcnsa.mcnsachat2.MCNSAChat2;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ConfigManager {
	// store the main plugin for later access
	static MCNSAChat2 plugin = null;
	public ConfigOptions options = new ConfigOptions();
	public ConfigManager(MCNSAChat2 instance) {
		plugin = instance;
	}

	// load the configuration
	public Boolean load(FileConfiguration config) {
		// load the chat radius
		//plugin.debug("loading options...");
		
		options.universeName = config.getString("universe-name");
		
		options.localChatRadius = (float)config.getDouble("local-chat-radius");
		//plugin.debug("local chat radius: " + options.localChatRadius.toString());
		
		options.defaultChannel = config.getString("default-channel");
		//plugin.debug("default channel: " + options.defaultChannel);
		
		options.defaultColour = ColourHandler.translateName(config.getString("default-colour"));
		//plugin.debug("default colour: " + options.defaultColour.toString());
		
		options.announceTimeouts = config.getBoolean("announce-timeouts");
		//plugin.debug("announce timeouts: " + options.announceTimeouts.toString());

		options.inTimeoutMessage = config.getString("in-timeout-message");
		options.removedFromTimeoutMessage = config.getString("removed-from-timeout-message");
		options.timeoutExpiredMessage = config.getString("timeout-expired-message");
		options.sentToTimeoutMessage = config.getString("sent-to-timeout-message");
		
		options.chatFormat = config.getString("chat-format");
		//plugin.debug("chat format: " + options.chatFormat);
		
		options.emoteFormat = config.getString("emote-format");
		//plugin.debug("emote format: " + options.emoteFormat);
		
		options.networkConfig.enabled = config.getBoolean("network.enable", false);
		
		options.networkConfig.hostName = config.getString("network.host-name", "localhost");
		
		options.networkConfig.hostPort = config.getInt("network.host-port", 9345);

		options.spamConfig.messageLimit = (float)config.getDouble("spam.chat-limit");
		//plugin.debug("spam message limit: " + options.spamConfig.messageLimit);
		
		options.spamConfig.messagePeriod = (float)config.getDouble("spam.chat-period") * 1000;
		//plugin.debug("spam message period: " + options.spamConfig.messagePeriod);

		options.spamConfig.minOnlineTime = (float)config.getDouble("spam.min-online-time") * 1000;
		//plugin.debug("options.spamConfig.minOnlineTime = " + options.spamConfig.minOnlineTime);
		
		options.spamConfig.miniBanTime = (float)config.getDouble("spam.mini-ban-time") * 60000;
		//plugin.debug("options.spamConfig.miniBanTime = " + options.spamConfig.miniBanTime);
		
		options.spamConfig.miniBanMessage = config.getString("spam.mini-ban-message");
		//plugin.debug("options.spamConfig.miniBanMessage = " + options.spamConfig.miniBanMessage);
		
		options.spamConfig.lockdownMessage = config.getString("spam.lockdown-message");
		//plugin.debug("options.spamConfig.miniBanMessage = " + options.spamConfig.miniBanMessage);
		
		options.spamConfig.timeoutTime = (float)config.getDouble("spam.timeout-time") * 60;
		//plugin.debug("options.spamConfig.timeoutTime = " + options.spamConfig.timeoutTime);
		
		// now get the hard channels!
		ConfigurationSection channelSection = config.getConfigurationSection("hard-channels");
		// and the list of all keys in here
		Set<String> channels = channelSection.getKeys(false);
		// and loop through each channel
		Iterator<String> it = channels.iterator();
		while(it.hasNext()) {
			String channelName = it.next();
			//plugin.debug("found hard channel: " + channelName);
			
			// now create the hard config for this channel
			ChannelHardConfig channelConfig = new ChannelHardConfig();
			channelConfig.name = channelName;
			
			channelConfig.colour = ColourHandler.translateName(channelSection.getString(channelName + ".colour", "grey"));
			//plugin.debug("\tchannel colour: " + channelConfig.colour);
			
			channelConfig.alias = channelSection.getString(channelName + ".alias", "");
			//plugin.debug("\tchannel alias: " + channelConfig.alias);
			
			channelConfig.permission = channelSection.getString(channelName + ".permission", "");
			//plugin.debug("\tchannel permission: " + channelConfig.permission);
			
			channelConfig.listeners = channelSection.getString(channelName + ".listeners", "");
			//plugin.debug("\tchannel listeners: " + channelConfig.listeners);
			
			channelConfig.local = channelSection.getBoolean(channelName + ".local", false);
			//plugin.debug("\tchannel local: " + channelConfig.local.toString());
			
			channelConfig.broadcast = channelSection.getBoolean(channelName + ".broadcast", false);
			//plugin.debug("\tchannel broadcast: " + channelConfig.broadcast.toString());
			
			channelConfig.networked = channelSection.getBoolean(channelName + ".networked", false);
			
			// and add it!
			options.hardChannels.put(channelName, channelConfig);
		}
		
		// now make sure our default channel is there
		if(!options.hardChannels.containsKey(options.defaultChannel)) {
			plugin.error("default channel doesn't exist!");
			return false;
		}
		
		// successful
		return true;
	}

	// create a "class" in here to store config options!
	public class ConfigOptions {
		public HashMap<String, ChannelHardConfig> hardChannels = new HashMap<String, ChannelHardConfig>();
		public String universeName = new String("");
		public Float localChatRadius = new Float(200);
		public String defaultChannel = new String("");
		public String defaultColour = new String("grey");
		public Boolean announceTimeouts = true;
		public String inTimeoutMessage = new String("%player was sent to timeout for %time for %reason");
		public String removedFromTimeoutMessage = new String("%player was removed from timeout!");
		public String timeoutExpiredMessage = new String("%player is no longer in timeout!");
		public String sentToTimeoutMessage = new String("You have been sent to timeout for %time for %reason");
		public String chatFormat = new String("<%channel&f> [%prefix%suffix&f] %player: &7%message");
		public String emoteFormat = new String("<%channel&f> [%prefix%suffix&f] %player: &7%message");
		public NetworkConfig networkConfig = new NetworkConfig();
		public SpamConfig spamConfig = new SpamConfig();
	}
	
	// network configurations
	public class NetworkConfig {
		public Boolean enabled = new Boolean(false);
		public String hostName = new String("localhost");
		public Integer hostPort = new Integer(9345);
	}

	// spam configurations
	public class SpamConfig {
		public Float messageLimit = new Float(5);
		public Float messagePeriod = new Float(3);
		public Float minOnlineTime = new Float(5);
		public Float miniBanTime = new Float(2);
		public Float timeoutTime = new Float(5);
		public String miniBanMessage = new String("gtfo");
		public String lockdownMessage = new String("gtfo");
	}

	// hard-channel configurations
	public class ChannelHardConfig {
		public String name = new String("");
		public String colour = new String("grey");
		public String permission = new String("");
		public String listeners = new String("");
		public Boolean local = false;
		public String alias = new String("");
		public Boolean broadcast = false;
		public Boolean networked = false;
	}
}
