package com.mcnsa.mcnsachat2.util;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class CommandManager {
	public MCNSAChat2 plugin = null;
	public CommandManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	// quick method to register a new alias (for a channel)
	public void registerAlias(String alias, String channel) {
	}
}
