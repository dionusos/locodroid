package hu.denes.command_center.roco_connection;

public interface Connection extends Functions {
	public enum DIRECTION {
		FORWARD, BACKWARD
	}

	public void setSpeed(int address, int speed);

	public void setDirection(int address, DIRECTION direction);

	public void turnLightsOn(int address);

	public void turnLightsOff(int address);

	public void turnFunctionOn(int address, FUNCTION function);

	public void turnFunctionOff(int address, FUNCTION function);

}
