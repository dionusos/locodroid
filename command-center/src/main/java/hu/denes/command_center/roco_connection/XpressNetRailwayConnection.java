package hu.denes.command_center.roco_connection;

import jssc.SerialPort;
import jssc.SerialPortException;

public class XpressNetRailwayConnection implements RailwayConnection {
	String serialTerminalName;

	public XpressNetRailwayConnection(final String serialTerminalName) {
		this.serialTerminalName = serialTerminalName;
	}

	@Override
	public void setSpeed(final int address, final int speed) {
		final byte[] bytes = new byte[6];
		bytes[0] = (byte) 0xe4;
		bytes[1] = (byte) 0x13;
		bytes[2] = (byte) 0x00;
		bytes[3] = (byte) address;
		bytes[4] = (byte) speed;
		bytes[5] = (byte) (bytes[0] ^ bytes[1] ^ bytes[2] ^ bytes[3] ^ bytes[4]);

		final SerialPort serialPort = new SerialPort(serialTerminalName);
		try {
			serialPort.openPort();// Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.writeBytes(bytes);
			serialPort.closePort();// Close serial port
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

	}

	@Override
	public void turnLightsOn(final int address) {
		// TODO Auto-generated method stub

	}

	@Override
	public void turnLightsOff(final int address) {
		// TODO Auto-generated method stub

	}

	@Override
	public void turnFunctionOn(final int address, final int function) {
		// TODO Auto-generated method stub

	}

	@Override
	public void turnFunctionOff(final int address, final int function) {
		// TODO Auto-generated method stub

	}

}
