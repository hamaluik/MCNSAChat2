package com.mcnsa.mcnsachat2.util;

import com.mcnsa.mcnsachat2.MCNSAChat2;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ConfigManager {
	// store the main plugin for later access
	static MCNSAChat2 plugin = null;
	public ConfigOptions options = new ConfigOptions();
	public ConfigManager(MCNSAChat2 instance) {
		plugin = instance;
	}

	// load the configuration
	public void load(FileConfiguration config) {
		// load the chat radius
		plugin.debug("loading options...");
		options.localChatRadius = (float)config.getDouble("local-chat-radius");
		plugin.debug("local chat radius: " + options.localChatRadius.toString());
		options.defaultChannel = config.getString("default-channel");
		plugin.debug("default channel: " + options.defaultChannel);
		options.defaultColour = ColourHandler.translateName(config.getString("default-colour"));
		plugin.debug("default colour: " + options.defaultColour.toString());
		options.announceTimeouts = config.getBoolean("announce-timeouts");
		plugin.debug("local chat radius: " + options.localChatRadius.toString());
	}

	// create a "class" in here to store config options!
	private class ConfigOptions {
		public HashMap<String, ChannelHardConfig> hardChannels = new HashMap<String, ChannelHardConfig>();
		public Float localChatRadius = new Float(200);
		public String defaultChannel = new String("");
		public String defaultColour = new String("grey");
		public Boolean announceTimeouts = true;
		public SpamConfig spamConfig = new SpamConfig(5.0f, 3.0f, 5.0f);
	}

	// hard-channel configurations
	private class ChannelHardConfig {
		public String name = new String("");
		public String colour = new String("grey");
		public String permissions = new String("channel");
		public Boolean local = false;
		public String alias = new String("");

		public ChannelHardConfig(String _name, String _colour, String _permissions, Boolean _local, String _alias) {
			name = _name;
			colour = _colour;
			permissions = _permissions;
			local = _local;
			alias = _alias;
		}
	}

	// spam configurations
	private class SpamConfig {
		public Float messageLimit = new Float(5);
		public Float messagePeriod = new Float(3);
		public Float timeoutTime = new Float(5);

		public SpamConfig(Float limit, Float period, Float time) {
			messageLimit = limit;
			messagePeriod = period;
			timeoutTime = time;
		}
	}
}
