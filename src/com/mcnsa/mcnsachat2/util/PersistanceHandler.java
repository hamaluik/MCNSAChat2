package com.mcnsa.mcnsachat2.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mcnsa.mcnsachat2.MCNSAChat2;

@SuppressWarnings("unchecked")
public class PersistanceHandler {
	private MCNSAChat2 plugin = null;
	
	private JSONArray onTimeout = new JSONArray();
	private JSONArray seeAll = new JSONArray();
	private Map<String, String> playerChannels = new LinkedHashMap<String, String>();
	
	public PersistanceHandler(MCNSAChat2 instance) {
		plugin = instance;
	}
	
	public void updateTimeout(ArrayList<String> timeout) {
		onTimeout.clear();
		// loop through and add them all
		for(int i = 0; i < timeout.size(); i++) {
			onTimeout.add(timeout.get(i));
		}
	}
	
	public void updateSeeAll(ArrayList<String> see) {
		seeAll.clear();
		// loop through and add them all
		for(int i = 0; i < see.size(); i++) {
			seeAll.add(see.get(i));
		}
	}
	
	public void updatePlayers(HashMap<String, String> players) {
		playerChannels.clear();
		for(String player: players.keySet()) {
			playerChannels.put(player, players.get(player));
		}
	}
	
	public void writePersistance() {
		FileWriter outFile;
		try {
			outFile = new FileWriter(plugin.getDataFolder().toString() + "/persist.json");
			PrintWriter out = new PrintWriter(outFile);
			
			JSONObject obj = new JSONObject();
			obj.put("onTimeout", onTimeout);
			obj.put("seeAll", seeAll);
			obj.put("players", playerChannels);
			
			out.print(obj);
			
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
