package hu.denes.command_center.client_connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.esotericsoftware.kryonet.Client;

public class ClientMessageSenderThread extends Thread {
	private final String addr;
	private final String msg;
	private final Client client;

	public ClientMessageSenderThread(final Client client,
			final String clientAddress, final String message) {
		this.client = client;
		addr = clientAddress;
		msg = message;
	}

	@Override
	public void run() {
		try {
			client.connect(5000, InetAddress.getByName(addr), 54556, 54778);
			client.sendTCP(msg);
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
