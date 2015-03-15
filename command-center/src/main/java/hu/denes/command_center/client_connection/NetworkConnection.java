package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;
import hu.denes.command_center.roco_connection.RailwayConnection.DIRECTION;
import hu.denes.command_center.storage.Storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

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
				if (object instanceof String) {
					final String request = (String) object;
					System.out.println("Request: " + request);
					final String response = processJson(request);
					System.out.println("Response: " + response);
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

	private String processJson(final String request) {
		String ret = "";
		final JSONObject jo = new JSONObject(request);
		switch (jo.getString("target")) {
		case "loco":
			ret = processLoco(jo.getJSONObject("function"));
			break;

		case "command-center":
			ret = processCCCommand(jo.getJSONObject("function"));
			break;
		}
		return ret;
	}

	private String processCCCommand(final JSONObject jsonObject) {
		final String query = jsonObject.getString("type");
		String ret = "";
		switch (query) {
		case "get-locos":
			final JSONObject jo = new JSONObject();
			jo.put("target", "client");
			final JSONObject function = new JSONObject();
			final JSONArray arr = new JSONArray();
			function.put("type", "answer-get-locos");
			for (final Integer addr : locoMap.keySet()) {
				final Loco l = locoMap.get(addr);
				final JSONObject jLoco = new JSONObject();
				jLoco.put("name", l.getName());
				jLoco.put("address", l.getAddress());
				arr.put(jLoco);
			}
			function.put("value", arr);
			jo.put("function", function);
			System.out.println(jo.toString());
			ret = jo.toString();
			break;
		case "save-loco":
			final JSONObject jLoco = jsonObject.getJSONObject("value");
			System.out.println(jLoco.toString());
			final Loco loco = new Loco(jLoco.getInt("address"),
					railwayConnection);
			loco.setName(jLoco.getString("name"));
			loco.setMaxSpeed(jLoco.getInt("max-speed"));
			if (!locoMap.containsKey(loco.getAddress())) {
				locoMap.put(loco.getAddress(), loco);
				ret = "OK";
			} else {
				ret = "ERROR!";
			}

			break;

		default:
			break;
		}

		return ret;

	}

	private String processLoco(final JSONObject o) {
		final String value = o.getString("value");
		final Integer address = o.getInt("address");
		final String ret = "";
		if (!locoMap.containsKey(address)) {
			return ret;
		}
		switch (o.getString("type")) {
		case "lights":
			if ("on".equals(value)) {
				locoMap.get(address).turnLightsOn();
			} else if ("off".equals(value)) {
				locoMap.get(address).turnLightsOff();
			}
			break;
		case "speed":
			locoMap.get(address).setSpeed(Integer.parseInt(value));
			break;
		case "direction":
			locoMap.get(address).setDirection(
					"forward".equals(value) ? DIRECTION.FORWARD
							: DIRECTION.BACKWARD);
			break;
		case "function-on":
			locoMap.get(address).activateFunction(value);
			break;
		case "function-off":
			locoMap.get(address).deactivateFunction(value);
			break;
		case "add-loco-to-train":
			if (!locoMap.containsKey(Integer.parseInt(value))) {
				return "";
			}
			locoMap.get(address).addRemoteLoco(
					locoMap.get(Integer.parseInt(value)));
			removeSelfLocoFromRemote(value, address);
			break;
		case "remove-loco-from-train":
			if (!locoMap.containsKey(Integer.parseInt(value))) {
				return "";
			}
			locoMap.get(address).removeRemoteLoco(
					locoMap.get(Integer.parseInt(value)));
			break;
		}
		return ret;
	}

	private void removeSelfLocoFromRemote(final String remoteLocoAddress,
			final Integer thisLocoAddress) {
		locoMap.get(Integer.parseInt(remoteLocoAddress)).removeRemoteLoco(
				locoMap.get(thisLocoAddress));
	}

}
