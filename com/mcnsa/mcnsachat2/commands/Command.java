package com.mcnsa.mcnsachat2.commands;

import org.bukkit.command.CommandSender;

public interface Command {
	public boolean onCommand(CommandSender sender, String[] args);
	public String requiredPermission();
	public String getCommand();
	public String getArguments();
	public String getDescription();
}