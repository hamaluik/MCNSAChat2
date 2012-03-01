package com.mcnsa.mcnsachat2.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class NetworkListener extends Thread {
	private Socket connection = null;
	private boolean done = false;
	private NetworkManager netManager;
	
	public NetworkListener(NetworkManager _netManager, Socket _connection) {
		netManager = _netManager;
		// store our connection!
		connection = _connection;
	}
	
	public Socket getSocket() {
		return connection;
	}

	// this gets run when the thread runs
	public void run() {
		try {
			// get a buffered reader from the client connection
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			// grab a line
			while(!done) {
				try {
					// prefix info and grab a line from the client
					String line = in.readLine();
					// and broadcast it back out!
					netManager.receiveMessage(line);
				}
				catch(Exception e) {
					// close our buffered read
					in.close();
					// disconnect this client
					netManager.disconnect();
					// and finish this thread
					done = true;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
