package hu.denes.locodroid;

import com.esotericsoftware.kryonet.Client;

public class ClientSingleton {
	private static ClientSingleton instance = null;

	private final Client client;

	private ClientSingleton() {
		client = new Client();
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
}
