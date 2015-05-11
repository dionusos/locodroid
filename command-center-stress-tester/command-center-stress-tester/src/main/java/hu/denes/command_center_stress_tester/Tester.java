package hu.denes.command_center_stress_tester;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.esotericsoftware.kryonet.Client;

public class Tester {
	static String ipAddress;
	final static Client client = new Client();

	private static void sendCommand(final String command) {

		try {
			client.connect(5000, InetAddress.getByName(ipAddress), 54555, 54777);
			client.sendTCP(command);
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		final int sleep = Integer.parseInt(args[0]);
		ipAddress = args[1];
		client.start();
		System.out.println("Stress tester will send speed command in every "
				+ sleep + " milliseconds to " + ipAddress);
		System.out.println("Press Ctrl + C to exit!");

		while (true) {
			try {
				Thread.sleep(sleep);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String command = "{\"target\": \"loco\",\"function\": {\"address\": "
					+ 5 + ",	\"type\": \"speed\", \"value\": \"" + 10 + "\"} }";
			sendCommand(command);
			System.out.print(".");
		}
	}
}
