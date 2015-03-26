package hu.denes.command_center;

import hu.denes.command_center.client_connection.Loco;
import hu.denes.command_center.client_connection.NetworkConnection;
import hu.denes.command_center.roco_connection.XpressNetRailwayConnection;
import hu.denes.command_center.storage.Storage;

import java.util.List;
import java.util.Scanner;

public class CommandCenter {
	public static void main(final String[] args) {
		if (args.length < 3) {
			return;
		}
		System.out.println("Welcome! Command Center!");

		final int tcpPort = Integer.parseInt(args[0]);
		final int udpPort = Integer.parseInt(args[1]);
		final String serialTerminalName = args[2];

		final XpressNetRailwayConnection rc = new XpressNetRailwayConnection(
				serialTerminalName);
		final Storage storage = new Storage(rc);
		storage.initDB();
		System.out.println(storage.getLocoAddresses());
		final NetworkConnection networkConnection = new NetworkConnection(
				storage, rc);
		networkConnection.startServer(tcpPort, udpPort);

		/*
		 * final List<Loco> locos = new ArrayList<Loco>(); Loco l = new Loco(1,
		 * rc); l.setName("Taurus"); locos.add(l); l = new Loco(2, rc);
		 * l.setName("Traxx"); locos.add(l); l = new Loco(3, rc);
		 * l.setName("Hercules"); locos.add(l);
		 */

		final Scanner keyboard = new Scanner(System.in);
		while (true) {
			System.out.println("exit, remove {loco address}, list");
			final String in = keyboard.nextLine();

			if ("exit".equals(in)) {
				break;
			} else if (in.startsWith("remove")) {
				final Integer addr = Integer.parseInt(in.split(" ")[1]);
				if (addr != null) {
					storage.removeLoco(addr);
					System.out.println("Loco @" + addr + " removed.");
				}
			} else if ("list".equals(in)) {
				final List<Integer> locoAddrList = storage.getLocoAddresses();
				for (final Integer a : locoAddrList) {
					final Loco l = storage.getLocoByAddress(a);
					System.out.println(l.getName() + "@" + l.getAddress() + ":"
							+ (1 + l.getMaxSpeed()));
				}
			}
		}
		networkConnection.stopServer();
		storage.closeDB();
		keyboard.close();
		rc.close();
		System.out.println("Good Bye!");
	}
}
