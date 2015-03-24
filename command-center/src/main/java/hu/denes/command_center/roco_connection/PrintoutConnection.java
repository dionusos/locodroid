package hu.denes.command_center.roco_connection;

public class PrintoutConnection implements RailwayConnection {

	@Override
	public void setSpeed(final int address, final int speed, final int maxSpeed) {
		System.out.println("Speed set to " + speed + " @ " + address);

	}

	@Override
	public void turnLightsOn(final int address) {
		System.out.println("Lights turned on @ " + address);

	}

	@Override
	public void turnLightsOff(final int address) {
		System.out.println("Lights turned off @ " + address);

	}

	@Override
	public void turnFunctionOn(final int address, final int function) {
		System.out.println(function + " turned on @ " + address);

	}

	@Override
	public void turnFunctionOff(final int address, final int function) {
		System.out.println(function + " turned off @ " + address);

	}

	@Override
	public void stopOperations() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumeOperations() {
		// TODO Auto-generated method stub

	}

}
