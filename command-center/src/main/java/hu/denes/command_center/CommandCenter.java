package hu.denes.command_center;

import hu.denes.command_center.client_connection.Loco;
import hu.denes.command_center.roco_connection.Connection;
import hu.denes.command_center.roco_connection.DummyConnection;

public class CommandCenter {
	public static void main(final String[] args) {
		System.out.println("Welcome! Command Center!");
		final Connection conn = new DummyConnection();
		final Loco taurus = new Loco(1, conn);
		final Loco traxx = new Loco(2, conn);
		taurus.addRemoteLoco(traxx);
		taurus.addRemoteLoco(taurus);
		taurus.setSpeed(50);
		taurus.turnLightsOn();
		System.out.println("Good Bye!");
	}
}
