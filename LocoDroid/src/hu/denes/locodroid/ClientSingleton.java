package hu.denes.locodroid;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public class ClientSingleton {
	private static ClientSingleton instance = null;

	private final Client client;
	private final Server server;

	private ClientSingleton() {
		client = new Client();
		server = new Server();
	}

	public static ClientSingleton getInstance() {
		if (instance == null) {
			instance = new ClientSingleton();
		}
		return instance;
	}

	public Client getClient() {
		return client;
	}

	public Server getServer() {
		return server;
	}
}
