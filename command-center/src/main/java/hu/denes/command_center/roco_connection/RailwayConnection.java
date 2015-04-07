package hu.denes.command_center.roco_connection;

public interface RailwayConnection {

	public void setSpeed(int address, int speed, int maxSpeed);

	public void switchFunction(int address, int function, int group);

	public void stopOperations();

	public void resumeOperations();

}
