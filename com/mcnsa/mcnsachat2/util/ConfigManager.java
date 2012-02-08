package com.mcnsa.mcnsachat2.util

public class ConfigManager {
	// store the main plugin for later access
	static MCNSAChat2 plugin = null;
	ConfigManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public void load(String file) {
		// TODO: load the config!
	}
}