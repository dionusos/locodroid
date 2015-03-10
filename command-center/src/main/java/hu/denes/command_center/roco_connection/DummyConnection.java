package hu.denes.command_center.roco_connection;

public class DummyConnection implements Connection {

	@Override
	public void setSpeed(final int address, final int speed) {
		System.out.println("Speed set to " + speed + " @ " + address);

	}

	@Override
	public void setDirection(final int address, final DIRECTION direction) {
		System.out.println("Direction set to " + direction + " @ " + address);

	}

	@Override
	public void turnLightsOn(final int address) {
		System.out.println("Lights turned on @ " + address);

	}

	@Override
	public void turnLightsOff(final int address) {
		System.out.println("Lights turned on @ " + address);

	}

	@Override
	public void turnFunctionOn(final int address, final FUNCTION function) {
		System.out.println(function + " turned on @ " + address);

	}

	@Override
	public void turnFunctionOff(final int address, final FUNCTION function) {
		System.out.println(function + " turned off @ " + address);

	}

}
