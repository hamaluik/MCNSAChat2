package com.mcnsa.mcnsachat2.util;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class ConfigManager {
	// store the main plugin for later access
	static MCNSAChat2 plugin = null;
	public ConfigManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public void load(String file) {
		// TODO: load the config!
	}
}
