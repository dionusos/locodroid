package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;
import hu.denes.command_center.storage.Storage;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NetworkConnection {
	private final Storage storage;
	private Server server;
	Client client;
	private final RailwayConnection railwayConnection;

	public void startServer(final int tcpPort, final int udpPort) {
		if (server != null) {
			server.stop();
			server = null;
		}
		server = new Server();
		client = new Client();

		try {
			server.bind(tcpPort, udpPort);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.start();
		client.start();
		System.out.println("Server started!");
		addListenerToServer();
	}

	public synchronized void controlIsOvertakenByMultiMaus(final int address,
			final String info) {
		final Loco l = storage.getLocoByAddress(address);
		if (l.getLastControllerIP() == "") {
			return;
		}
		new ClientMessageSenderThread(client, l.getLastControllerIP(), info)
		.start();

		l.setLastControllerIP("");
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
					final String response = processJson(request, connection
							.getRemoteAddressTCP().getAddress()
					        .getHostAddress());
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

	private String processJson(final String request, final String clientIP) {
		String ret = "";
		final JSONObject jo = new JSONObject(request);
		switch (jo.getString("target")) {
		case "loco":
			ret = processLoco(jo.getJSONObject("function"), clientIP);
			break;

		case "command-center":
			ret = processCCCommand(jo.getJSONObject("function"));
			break;
		}
		return ret;
	}

	protected String getLocoDetails(final Integer address) {
		final JSONObject json = new JSONObject();
		json.put("target", "client");
		final JSONObject function = new JSONObject();
		function.put("type", "answer-get-loco-details");
		final Loco loco = storage.getLocoByAddress(address);
		if (loco == null) {
			return "";
		}
		final JSONObject jLoco = new JSONObject();
		jLoco.put("name", loco.getName());
		jLoco.put("address", loco.getAddress());
		jLoco.put("direction", loco.getDirection());
		jLoco.put("max-speed", loco.getMaxSpeed());
		jLoco.put("speed", loco.getSpeed());
		jLoco.put("lightsOn", loco.isLightsOn());
		jLoco.put("activated-functions", loco.getActivatedFunctions());
		function.put("value", jLoco);
		json.put("function", function);
		return json.toString();
	}

	private String getLocos() {
		final JSONObject jsonObj = new JSONObject();
		jsonObj.put("target", "client");
		final JSONObject function = new JSONObject();
		final JSONArray jLocoArray = new JSONArray();
		function.put("type", "answer-get-locos");
		for (final Integer addr : storage.getLocoAddresses()) {
			final Loco l = storage.getLocoByAddress(addr);
			if (l == null) {
				continue;
			}
			final JSONObject jLoco = new JSONObject();
			jLoco.put("name", l.getName());
			jLoco.put("address", l.getAddress());
			jLocoArray.put(jLoco);
		}
		function.put("value", jLocoArray);
		jsonObj.put("function", function);
		return jsonObj.toString();
	}

	private String getAttachedLocos(final Integer address) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("target", "client");
		final JSONObject function = new JSONObject();
		final JSONArray jLocoArray = new JSONArray();
		function.put("type", "answer-get-attached-locos");
		final Loco drivenLoco = storage.getLocoByAddress(address);
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
			jLocoArray.put(jLoco);
		}
		function.put("value", jLocoArray);
		jsonObject.put("function", function);
		return jsonObject.toString();
	}

	private String processCCCommand(final JSONObject jsonObject) {
		final String query = jsonObject.getString("type");
		String ret = "";
		switch (query) {
		case "get-loco-details":
			ret = getLocoDetails(Integer
					.parseInt(jsonObject.getString("value")));
			break;
		case "get-locos":
			ret = getLocos();
			break;
		case "get-attached-locos":
			ret = getAttachedLocos(jsonObject.getInt("value"));
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

	private String processLoco(final JSONObject o, final String clientIP) {
		final String value = o.getString("value");
		final Integer address = o.getInt("address");
		final String ret = "";
		if (!storage.isLocoExistByAddress(address)) {
			return ret;
		}
		final Loco l = storage.getLocoByAddress(address);
		switch (o.getString("type")) {
		case "lights":
			if ("on".equals(value)) {
				l.turnLightsOn();
			} else if ("off".equals(value)) {
				l.turnLightsOff();
			}
			break;
		case "speed":
			l.setSpeed(Integer.parseInt(value));
			break;
		case "direction":
			l.setDirection("forward".equals(value) ? 128 : 0);
			break;
		case "function-on":
			l.activateFunction(value);
			break;
		case "function-off":
			l.deactivateFunction(value);
			break;
		case "add-loco-to-train":
			if (!storage.isLocoExistByAddress(Integer.parseInt(value))) {
				return "";
			}
			final Loco remoteLoco = storage.getLocoByAddress(Integer
			        .parseInt(value));
			l.addRemoteLoco(remoteLoco);
			controlIsOvertakenByMultiMaus(remoteLoco.getAddress(),
			        "Control is overtaken by another controller");
			remoteLoco.setLastControllerIP(clientIP);
			removeSelfLocoFromRemote(value, address);
			break;
		case "remove-loco-from-train":
			if (!storage.isLocoExistByAddress(Integer.parseInt(value))) {
				return "";
			}
			final Loco remoteLoco2 = storage.getLocoByAddress(Integer
			        .parseInt(value));
			remoteLoco2.setLastControllerIP("");
			l.removeRemoteLoco(remoteLoco2);
			break;
		default:
			return ret;
		}
		final String oldIP = l.getLastControllerIP();
		if (!oldIP.equals(clientIP)) {
			controlIsOvertakenByMultiMaus(l.getAddress(),
			        "Control is overtaken by another controller");
		}
		l.setLastControllerIP(clientIP);
		return ret;
	}

	private void removeSelfLocoFromRemote(final String remoteLocoAddress,
			final Integer thisLocoAddress) {
		final Loco l = storage.getLocoByAddress(Integer
				.parseInt(remoteLocoAddress));
		l.removeRemoteLoco(storage.getLocoByAddress(thisLocoAddress));
	}

}
