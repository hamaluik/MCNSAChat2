package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class VanishManager {
	private MCNSAChat2 plugin = null;
	public ArrayList<Player> vanishedPlayers = new ArrayList<Player>();
	
	public VanishManager(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public void refreshAllVanished() {
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			for(int j = 0; j < vanishedPlayers.size(); j++) {
				if(!plugin.hasPermission(players[i], "seevanished")) {
					players[i].hidePlayer(vanishedPlayers.get(j));
				}
			}
		}
	}
	
	public boolean isVanished(Player player) {
		return vanishedPlayers.contains(player);
	}
	
	public void hidePlayer(Player player) {
		if(!vanishedPlayers.contains(player)) {
			vanishedPlayers.add(player);
		}
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			if(!plugin.hasPermission(players[i], "seevanished")) {
				players[i].hidePlayer(player);
			}
		}
	}
	
	public void showPlayer(Player player) {
		if(vanishedPlayers.contains(player)) {
			vanishedPlayers.remove(player);
		}
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++) {
			players[i].showPlayer(player);
		}
	}
	
	public boolean togglePlayerVisibility(Player player) {
		if(isVanished(player)) {
			showPlayer(player);
		}
		else {
			hidePlayer(player);
		}
		return isVanished(player);
	}
}
