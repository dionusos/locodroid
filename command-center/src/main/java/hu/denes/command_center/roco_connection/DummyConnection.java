package hu.denes.command_center.roco_connection;

import hu.denes.command_center.client_connection.NetworkConnection;

public class DummyConnection implements RailwayConnection {

	@Override
	public void setSpeed(final int address, final int speed, final int maxSpeed) {
		System.out.println("setSpeed()");

	}

	@Override
	public void switchFunction(final int address, final int function,
	        final int group) {
		System.out.println("switchFunction()");

	}

	@Override
	public void stopOperations() {
		System.out.println("stopOperations()");

	}

	@Override
	public void resumeOperations() {
		System.out.println("resumeOperations()");

	}

	@Override
	public void setNetworkConnection(final NetworkConnection networkConnection) {
		System.out.println("setNetworkConnection()");

	}

	@Override
	public void close() {
		System.out.println("close()");

	}

}
