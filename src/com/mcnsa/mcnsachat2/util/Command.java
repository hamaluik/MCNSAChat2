package com.mcnsa.mcnsachat2.util;

import org.bukkit.entity.Player;

public interface Command {
	public Boolean handle(Player player, String sArgs);
}
