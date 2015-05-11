package hu.denes.command_center.roco_connection;

import hu.denes.command_center.client_connection.NetworkConnection;

public interface RailwayConnection {

	public void setSpeed(int address, int speed, int maxSpeed);

	public void switchFunction(int address, int function, int group);

	public void stopOperations();

	public void resumeOperations();

	public void setNetworkConnection(NetworkConnection networkConnection);

	public void close();

}
