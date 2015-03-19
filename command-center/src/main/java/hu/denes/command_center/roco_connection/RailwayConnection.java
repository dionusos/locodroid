package hu.denes.command_center.roco_connection;

public interface RailwayConnection {

	public void setSpeed(int address, int speed, int maxSpeed);

	public void turnLightsOn(int address);

	public void turnLightsOff(int address);

	public void turnFunctionOn(int address, int function);

	public void turnFunctionOff(int address, int function);

}
