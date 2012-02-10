package com.mcnsa.mcnsachat2.util;

import com.mcnsa.mcnsachat2.MCNSAChat2;

import com.mcnsa.mcnsachat2.commands.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;

public class CommandManager {
	// keep track of the plugin
	public MCNSAChat2 plugin = null;

	// and the commands
	private HashMap<String, Command> commands = new HashMap<String, Command>();

	public CommandManager(MCNSAChat2 instance) {
		plugin = instance;

		// develop the list of all commands here!
		// TODO: dynamically load commands ALA CommandBook
		registerCommand(new CommandChannel(plugin));
	}

	// register new command
	public void registerCommand(Command command) {
		Class cClass = command.class;
	}

	// quick method to register a new alias (for a channel)
	public void registerAlias(String alias, String channel) {
	}
}
