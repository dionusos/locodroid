package hu.denes.command_center.roco_connection;

public class PrintoutConnection implements RailwayConnection {

	public void setSpeed(final int address, final int speed) {
		System.out.println("Speed set to " + speed + " @ " + address);

	}

	public void setDirection(final int address, final DIRECTION direction) {
		System.out.println("Direction set to " + direction + " @ " + address);

	}

	public void turnLightsOn(final int address) {
		System.out.println("Lights turned on @ " + address);

	}

	public void turnLightsOff(final int address) {
		System.out.println("Lights turned off @ " + address);

	}

	public void turnFunctionOn(final int address, final int function) {
		System.out.println(function + " turned on @ " + address);

	}

	public void turnFunctionOff(final int address, final int function) {
		System.out.println(function + " turned off @ " + address);

	}

}
