package com.mcnsa.mcnsachat2.util;

import com.mcnsa.mcnsachat2.MCNSAChat2;

import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
	// store the main plugin for later access
	static MCNSAChat2 plugin = null;
	public ConfigManager(MCNSAChat2 instance) {
		plugin = instance;
	}

	public Boolean configExists(String file) {
		// see if it exists by trying to open it
		File configFile = new File(file);
		Boolean exists = configFile.exists();
		// close up
		configFile.close();
		return exists;
	}
	
	public Boolean load() {
		// make sure the config exists before we try to load it!
		if(!configExists(plugin.getDataFolder() + "/config.yml")) {
			// save the file
			plugin.info("configuration did not exist, creating default..");
			// create the file
			new File(plugin.getDataFolder().toString()).mkdir();
			if(!save()) {
				// if the save failed,
				// get out of here!
				plugin.error("failed to save the configuration!");
				return false;
			}
		}
		// TODO: load the config!
		return true;
	}

	public Boolean save() {
		plugin.info("saving configuration...");
		// open the file
		File configFile = new File(plugin.getDataFolder() + "/config.yml");
		try {
			if(!configFile.exists()) {
				// make the file if it doesn't exist
				configFile.createNewFile();
			}
			// now write out to it
			FileWriter out = new FileWriter(plugin.getDataFolder() + "/config.yml");

			// the header
			out.write("---\r\n");

			// and close up shop
			out.close();
			configFile.close();
		}
		catch(IOException ex) {
			// something went wrong!
			log.error("something went wrong when saving the configuration!");
			return false;
		}
		return true;
	}

	// create a "class" in here to store config options!
	private class ConfigOptions {
		public HashMap<String, ChannelHardConfig> hardChannels = new HashMap<String, ChannelHardConfig>();
		public Float localChatRadius = new Float(200);
		public String defaultChannel = new String("");
		public ColourHandler defaultColour = new ColourHandler("grey");
		public Boolean announceTimeouts = true;
		public SpamConfig spamConfig = new SpamConfig(5.0f, 3.0f, 5.0f);
	}

	// hard-channel configurations
	private class ChannelHardConfig {
		public String name = new String("");
		public ColourHandler colour = new ColourHandler("grey");
		public String permissions = new String("channel");
		public Boolean local = false;
		public String alias = new String("");

		public ChannelHardConfig(String _name, ColourHandler _colour, String _permissions, Boolean _local, String _alias) {
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
