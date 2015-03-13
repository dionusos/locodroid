package hu.denes.command_center;

import hu.denes.command_center.client_connection.Loco;
import hu.denes.command_center.client_connection.NetworkConnection;
import hu.denes.command_center.roco_connection.PrintoutConnection;
import hu.denes.command_center.roco_connection.RailwayConnection;
import hu.denes.command_center.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandCenter {
	public static void main(final String[] args) {
		if (args.length < 2) {
			return;
		}
		System.out.println("Welcome! Command Center!");
		final int tcpPort = Integer.parseInt(args[0]);
		final int udpPort = Integer.parseInt(args[1]);
		final Storage storage = new Storage();
		final RailwayConnection rc = new PrintoutConnection();
		final NetworkConnection networkConnection = new NetworkConnection(
				storage, rc);
		networkConnection.startServer(tcpPort, udpPort);

		final List<Loco> locos = new ArrayList<Loco>();
		locos.add(new Loco(1, rc));
		networkConnection.setLocos(locos);

		final Scanner keyboard = new Scanner(System.in);
		while (true) {
			final String in = keyboard.nextLine();

			if ("exit".equals(in)) {
				break;
			}
		}
		networkConnection.stopServer();
		keyboard.close();
		System.out.println("Good Bye!");
	}
}
