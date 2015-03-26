package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;
import hu.denes.command_center.storage.Storage;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NetworkConnection {
	// private final Map<Integer, Loco> storage;
	private final Storage storage;
	private Server server;
	private final RailwayConnection railwayConnection;

	public void startServer(final int tcpPort, final int udpPort) {
		if (server != null) {
			server.stop();
			server = null;
		}
		server = new Server();
		System.out.println("Server started!");

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
					if (request.startsWith("STOP")) {
						stopOperations();
						return;
					} else if (request.startsWith("RESUME")) {
						resumeOperations();
						return;
					}
					final String response = processJson(request);
					connection.sendTCP(response);
				}
			}
		});
	}

	protected void stopOperations() {
		railwayConnection.stopOperations();
	}

	protected void resumeOperations() {
		railwayConnection.resumeOperations();
	}

	public NetworkConnection(final Storage storage,
			final RailwayConnection railwayConnection) {
		this.storage = storage;
		this.railwayConnection = railwayConnection;
		server = null;
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
		case "get-loco-details":
			final JSONObject jo0 = new JSONObject();
			jo0.put("target", "client");
			final JSONObject function0 = new JSONObject();
			function0.put("type", "answer-get-loco-details");
			final Loco l0 = storage.getLocoByAddress(Integer
					.parseInt(jsonObject.getString("value")));
			if (l0 == null) {
				break;
			}
			final JSONObject jLoco0 = new JSONObject();
			jLoco0.put("name", l0.getName());
			jLoco0.put("address", l0.getAddress());
			jLoco0.put("direction", l0.getDirection());
			jLoco0.put("max-speed", l0.getMaxSpeed());
			jLoco0.put("speed", l0.getSpeed());
			jLoco0.put("lightsOn", l0.isLightsOn());

			function0.put("value", jLoco0);
			jo0.put("function", function0);
			ret = jo0.toString();
			break;
		case "get-locos":
			final JSONObject jo = new JSONObject();
			jo.put("target", "client");
			final JSONObject function = new JSONObject();
			final JSONArray arr = new JSONArray();
			function.put("type", "answer-get-locos");
			for (final Integer addr : storage.getLocoAddresses()) {
				final Loco l = storage.getLocoByAddress(addr);
				final JSONObject jLoco = new JSONObject();
				jLoco.put("name", l.getName());
				jLoco.put("address", l.getAddress());
				arr.put(jLoco);
			}
			function.put("value", arr);
			jo.put("function", function);
			ret = jo.toString();
			break;
		case "get-attached-locos":
			final JSONObject jo2 = new JSONObject();
			jo2.put("target", "client");
			final JSONObject function2 = new JSONObject();
			final JSONArray arr2 = new JSONArray();
			function2.put("type", "answer-get-attached-locos");
			final Loco drivenLoco = storage.getLocoByAddress(jsonObject
					.getInt("value"));
			for (final Integer addr : storage.getLocoAddresses()) {
				final Loco l = storage.getLocoByAddress(addr);
				if (addr == drivenLoco.getAddress()) {
					continue;
				}
				final JSONObject jLoco = new JSONObject();
				jLoco.put("name", l.getName());
				jLoco.put("address", l.getAddress());
				jLoco.put(
						"attached",
						drivenLoco.getRemoteLocos().contains(
								storage.getLocoByAddress(addr)) ? true : false);
				arr2.put(jLoco);
			}
			function2.put("value", arr2);
			jo2.put("function", function2);
			ret = jo2.toString();
			break;
		case "save-loco":
			final JSONObject jLoco = jsonObject.getJSONObject("value");
			final Loco loco = new Loco(jLoco.getInt("address"),
					railwayConnection);
			loco.setName(jLoco.getString("name"));
			loco.setMaxSpeed(jLoco.getInt("max-speed"));
			if (!storage.isLocoExistByAddress(loco.getAddress())) {
				storage.addLoco(loco);
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
		if (!storage.isLocoExistByAddress(address)) {
			return ret;
		}
		switch (o.getString("type")) {
		case "lights":
			if ("on".equals(value)) {
				storage.getLocoByAddress(address).turnLightsOn();
			} else if ("off".equals(value)) {
				storage.getLocoByAddress(address).turnLightsOff();
			}
			break;
		case "speed":
			storage.getLocoByAddress(address).setSpeed(Integer.parseInt(value));
			break;
		case "direction":
			storage.getLocoByAddress(address).setDirection(
					"forward".equals(value) ? 128 : 0);
			break;
		case "function-on":
			storage.getLocoByAddress(address).activateFunction(value);
			break;
		case "function-off":
			storage.getLocoByAddress(address).deactivateFunction(value);
			break;
		case "add-loco-to-train":
			if (!storage.isLocoExistByAddress(Integer.parseInt(value))) {
				return "";
			}
			storage.getLocoByAddress(address).addRemoteLoco(
					storage.getLocoByAddress(Integer.parseInt(value)));
			removeSelfLocoFromRemote(value, address);
			break;
		case "remove-loco-from-train":
			if (!storage.isLocoExistByAddress(Integer.parseInt(value))) {
				return "";
			}
			storage.getLocoByAddress(address).removeRemoteLoco(
					storage.getLocoByAddress(Integer.parseInt(value)));
			break;
		}
		return ret;
	}

	private void removeSelfLocoFromRemote(final String remoteLocoAddress,
			final Integer thisLocoAddress) {
		final Loco l = storage.getLocoByAddress(Integer
				.parseInt(remoteLocoAddress));
		l.removeRemoteLoco(storage.getLocoByAddress(thisLocoAddress));
	}

}
