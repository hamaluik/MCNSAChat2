package com.mcnsa.mcnsachat2.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mcnsa.mcnsachat2.MCNSAChat2;
import com.mcnsa.mcnsachat2.util.ChatManager.Verbosity;

@SuppressWarnings("unchecked")
public class PersistanceHandler {
	private MCNSAChat2 plugin = null;
	
	private HashMap<String, String> offlineChannels = new HashMap<String, String>();
	private HashMap<String, Long> offlineVerbosity = new HashMap<String, Long>();
	
	public PersistanceHandler(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public void setOfflineChannel(String player, String channel) {
		offlineChannels.put(player, channel);
	}
	
	public String getOfflineChannel(String player) {
		// see if we're tracking their offline channel first
		if(!offlineChannels.containsKey(player)) {
			return plugin.config.options.defaultChannel;
		}
		
		String channel = offlineChannels.get(player);
		offlineChannels.remove(player);
		return channel;
	}
	
	public void setOfflineVerbosity(String player, Verbosity level) {
		// only set verbosity for those who aren't at default!
		if(level.compareTo(Verbosity.SHOWALL) != 0) {
			if(level.compareTo(Verbosity.SHOWNONE) == 0) {
				//plugin.debug(player + "'s verbosity has been set to: none");
				offlineVerbosity.put(player, (long)0);
			}
			else {
				//plugin.debug(player + "'s verbosity has been set to: some");
				offlineVerbosity.put(player, (long)1);
			}
		}
	}
	
	public Verbosity getOfflineVerbosity(String player) {		
		// see if we're tracking their offline channel first
		if(!offlineVerbosity.containsKey(player)) {
			//plugin.debug(player + " did not have verbosity saved");
			return Verbosity.SHOWALL;
		}
		
		Long intLevel = offlineVerbosity.get(player);
		Verbosity level = Verbosity.SHOWALL;
		if(intLevel == 0) {
			//plugin.debug(player + " had a saved verbosity of none");
			level = Verbosity.SHOWNONE;
		}
		else if(intLevel == 1) {
			//plugin.debug(player + " had a saved verbosity of some");
			level = Verbosity.SHOWSOME;
		}
		offlineVerbosity.remove(player);
		return level;
	}
	
	public void writePersistance() {
		FileWriter outFile;
		try {
			outFile = new FileWriter(plugin.getDataFolder().toString() + "/persist.json");
			PrintWriter out = new PrintWriter(outFile);
			
			JSONObject obj = new JSONObject();
			
			// put some basic lists
			obj.put("onTimeout", plugin.chatManager.onTimeout);
			obj.put("seeAll", plugin.channelManager.seeAll);
			obj.put("poofed", plugin.channelManager.poofed);
			
			// now put the player channels
			for(String player: plugin.channelManager.players.keySet()) {
				offlineChannels.put(player, plugin.channelManager.players.get(player));
			}
			obj.put("playerChannels", offlineChannels);
			
			// now player verbosities
			for(String player: plugin.chatManager.verbosity.keySet()) {
				if(plugin.chatManager.verbosity.get(player).compareTo(Verbosity.SHOWALL) != 0) {
					if(plugin.chatManager.verbosity.get(player).compareTo(Verbosity.SHOWNONE) == 0) {
						offlineVerbosity.put(player, (long)0);
					}
					else {
						offlineVerbosity.put(player, (long)1);
					}
				}
			}
			obj.put("playerVerbosity", offlineVerbosity);
			
			// now muted
			JSONObject mutedObj = new JSONObject();
			for(String player: plugin.chatManager.muted.keySet()) {
				mutedObj.put(player, plugin.chatManager.muted.get(player));
			}
			obj.put("muted", mutedObj);
			
			// now vanished
			ArrayList<String> vanished = new ArrayList<String>();
			for(int i = 0; i < plugin.vanishManager.vanishedPlayers.size(); i++) {
				vanished.add(plugin.vanishManager.vanishedPlayers.get(i).getName());
			}
			obj.put("vanished", vanished);
			
			out.print(obj);
			
			out.close();
		} catch (IOException e) {
			plugin.error("failed to write persistance: " + e.getMessage());
		}
	}
	
	public void readPersistance() {
		try {
			// load the file
			String lineSep = System.getProperty("line.separator");
			FileInputStream fin = new FileInputStream(plugin.getDataFolder().toString() + "/persist.json");
			BufferedReader input = new BufferedReader(new InputStreamReader(fin));
			String nextLine = "";
			StringBuffer sb = new StringBuffer();
			while((nextLine = input.readLine()) != null) {
				sb.append(nextLine);
				sb.append(lineSep);
			}
			
			// and parse it!
			Map<String, Object> obj = (HashMap<String, Object>)JSONValue.parse(sb.toString());
			
			// and grab the objects!
			if(obj != null) {
				plugin.chatManager.onTimeout = (ArrayList<String>)obj.get("onTimeout");
				plugin.channelManager.seeAll = (ArrayList<String>)obj.get("seeAll");
				plugin.channelManager.poofed = (ArrayList<String>)obj.get("poofed");
				offlineChannels = (HashMap<String, String>)obj.get("playerChannels");
				offlineVerbosity = (HashMap<String, Long>)obj.get("playerVerbosity");
				plugin.chatManager.muted = (HashMap<String, ArrayList<String>>)obj.get("muted");
				
				// load the vanished
				ArrayList<String> vanished = (ArrayList<String>)obj.get("vanished");
				plugin.vanishManager.vanishedPlayers.clear();
				for(int i = 0; i < vanished.size(); i++) {
					if(plugin.getServer().getPlayerExact(vanished.get(i)) != null) {
						// only if the player still exists
						plugin.vanishManager.vanishedPlayers.add(plugin.getServer().getPlayerExact(vanished.get(i)));
					}
				}
				
				/*plugin.debug("loaded verbosities:");
				for(String player: offlineVerbosity.keySet()) {
					plugin.debug("loaded verbosity of " + offlineVerbosity.get(player) + " for player: " + player);
				}*/
			}
		}
		catch(Exception e) {
			plugin.error("failed to read persistance: " + e.getMessage());
		}
	}
}
