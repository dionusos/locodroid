package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;
import hu.denes.command_center.storage.Storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NetworkConnection {
	private final Map<Integer, Loco> locoMap;
	private final Storage storage;
	private Server server;
	private final RailwayConnection railwayConnection;

	public void startServer(final int tcpPort, final int udpPort) {
		if (server != null) {
			server.stop();
			server = null;
		}
		server = new Server();

		try {
			server.bind(tcpPort, udpPort);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.start();
		addListenerToServer();
	}

	private void addListenerToServer() {
		server.addListener(new Listener() {
			@Override
			public void received(final Connection connection,
					final Object object) {
				// System.out.println("Package received!");
				if (object instanceof String) {
					final String request = (String) object;
					// System.out.println(request + " arrived");
					if (request.startsWith("lights on")) {
						final Loco l = locoMap.get(Integer.parseInt(request
								.split("#")[1]));
						if (l != null) {
							l.turnLightsOn();
						}
					} else if (request.startsWith("lights off")) {
						final Loco l = locoMap.get(Integer.parseInt(request
								.split("#")[1]));
						if (l != null) {
							l.turnLightsOff();
						}
					}

					final String response = "OK";
					connection.sendTCP(response);
				}
			}
		});
	}

	public NetworkConnection(final Storage storage,
			final RailwayConnection railwayConnection) {
		this.storage = storage;
		this.railwayConnection = railwayConnection;
		locoMap = new HashMap<Integer, Loco>();
		server = null;
	}

	public void setLocos(final List<Loco> locoList) {
		for (final Loco l : locoList) {
			locoMap.put(l.getAddress(), l);
		}
	}

	public void loadLocos() {
		storage.getLocoList();
	}

	public void saveLocos() {
		storage.saveLocos(locoMap.values());
	}

	public void stopServer() {
		server.stop();

	}
}
