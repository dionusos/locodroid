package hu.denes.command_center;

import hu.denes.command_center.client_connection.Loco;
import hu.denes.command_center.client_connection.NetworkConnection;
import hu.denes.command_center.roco_connection.XpressNetRailwayConnection;
import hu.denes.command_center.storage.Storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

		final XpressNetRailwayConnection railwayConnection = new XpressNetRailwayConnection(
				serialTerminalName);
		final Storage storage = new Storage(railwayConnection);
		storage.initDB();
		System.out.println(storage.getLocoAddresses());
		final NetworkConnection networkConnection = new NetworkConnection(
				storage, railwayConnection);
		networkConnection.startServer(tcpPort, udpPort);

		final Scanner keyboard = new Scanner(System.in);
		while (true) {
			System.out
			.println("exit, remove {loco address}, list, save {filename}, load {filename}");
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
					if (l != null) {
						System.out.println(l.getName() + "@" + l.getAddress()
								+ ":" + (1 + l.getMaxSpeed()));
					}
				}

			} else if (in.startsWith("save")) {
				if (in.split(" ").length < 2) {
					System.out.println("WARNING: No file name given!");
					continue;
				}
				final String fileName = in.split(" ")[1];
				if (fileName == null) {
					System.out.println("WARNING: No file name not recognized!");
					continue;
				}
				final List<Integer> locoAddrList = storage.getLocoAddresses();
				try (PrintWriter w = new PrintWriter(fileName)) {
					for (final Integer a : locoAddrList) {
						final Loco l = storage.getLocoByAddress(a);
						final String line = l.getAddress() + "," + l.getName()
								+ "," + (1 + l.getMaxSpeed());
						w.println(line);
					}
					System.out.println("INFO: Locos saved successful!");
				} catch (final FileNotFoundException ex) {
					System.out
					.println("WARNING: There is a problem with that file!");
					continue;
				}

			} else if (in.startsWith("load")) {
				if (in.split(" ").length < 2) {
					System.out.println("WARNING: No file name given!");
					continue;
				}
				final String fileName = in.split(" ")[1];
				try (BufferedReader br = new BufferedReader(new FileReader(
						fileName))) {
					String line;
					while ((line = br.readLine()) != null) {
						final String[] loco = line.split(",");
						if (storage.getLocoByAddress(Integer.parseInt(loco[0])) == null) {
							storage.addLoco(new Loco(Integer.parseInt(loco[0]),
									loco[1], Integer.parseInt(loco[2]),
									railwayConnection));
						} else {
							System.out
							.println("WARNING: Loco exists with address "
									+ loco[0]);
						}
					}
					System.out.println("INFO: Locos loaded successful!");
				} catch (final FileNotFoundException e1) {
					System.out.println("WARNING: File does not exist!");
					e1.printStackTrace();
				} catch (final IOException e1) {
					System.out.println("ERROR: File cannot be closed!");
				}
			}

		}
		networkConnection.stopServer();
		storage.closeDB();
		keyboard.close();
		railwayConnection.close();
		System.out.println("Good Bye!");
	}
}
