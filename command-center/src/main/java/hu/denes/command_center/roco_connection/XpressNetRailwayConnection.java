package hu.denes.command_center.roco_connection;

import java.util.HashMap;
import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortException;

public class XpressNetRailwayConnection implements RailwayConnection {
	String serialTerminalName;

	public XpressNetRailwayConnection(final String serialTerminalName) {
		this.serialTerminalName = serialTerminalName;
	}

	@Override
	public void setSpeed(final int address, final int speed, final int maxSpeed) {
		final byte[] bytes = new byte[6];
		final Map<Integer, Byte> map = new HashMap<Integer, Byte>();
		map.put(13, (byte) 0x10);
		map.put(26, (byte) 0x11);
		map.put(27, (byte) 0x12);
		map.put(127, (byte) 0x13);

		bytes[0] = (byte) 0xe4;
		bytes[1] = map.get(maxSpeed);
		if (address > 255) {
			bytes[2] = (byte) (address / 255);
			bytes[3] = (byte) (address % 255);
		} else {
			bytes[2] = (byte) 0x00;
			bytes[3] = (byte) address;
		}
		bytes[4] = (byte) speed;
		bytes[5] = calculateXor(bytes);

		final SerialPort serialPort = new SerialPort(serialTerminalName);
		try {
			serialPort.openPort();// Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.writeBytes(bytes);
			final StringBuilder print = new StringBuilder();
			for (int i = 0; i < bytes.length; ++i) {
				print.append(bytes[i] + " ");
			}
			System.out.println("Speed set: " + print.toString());
			serialPort.closePort();// Close serial port
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

	}

	private byte calculateXor(final byte[] array) {
		byte ret = array[0];
		for (int i = 1; i < array.length - 1; ++i) {
			ret ^= array[i];
		}
		return ret;
	}

	@Override
	public void turnLightsOn(final int address) {
		final byte[] bytes = new byte[6];

		bytes[0] = (byte) 228;
		bytes[1] = (byte) 0x20;
		if (address > 255) {
			bytes[2] = (byte) (address / 255);
			bytes[3] = (byte) (address % 255);
		} else {
			bytes[2] = (byte) 0x00;
			bytes[3] = (byte) address;
		}
		bytes[4] = (byte) 0x10;
		bytes[5] = calculateXor(bytes);

		final SerialPort serialPort = new SerialPort(serialTerminalName);
		try {
			serialPort.openPort();// Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.writeBytes(bytes);
			final StringBuilder print = new StringBuilder();
			for (int i = 0; i < bytes.length; ++i) {
				print.append(bytes[i] + " ");
			}
			System.out.println("Lights turned off: " + print.toString());
			serialPort.closePort();// Close serial port
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

	}

	@Override
	public void turnLightsOff(final int address) {
		final byte[] bytes = new byte[6];

		bytes[0] = (byte) 228;
		bytes[1] = (byte) 0x20;
		if (address > 255) {
			bytes[2] = (byte) (address / 255);
			bytes[3] = (byte) (address % 255);
		} else {
			bytes[2] = (byte) 0x00;
			bytes[3] = (byte) address;
		}
		bytes[4] = (byte) 0;
		bytes[5] = calculateXor(bytes);

		final SerialPort serialPort = new SerialPort(serialTerminalName);
		try {
			serialPort.openPort();// Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.writeBytes(bytes);
			final StringBuilder print = new StringBuilder();
			for (int i = 0; i < bytes.length; ++i) {
				print.append(bytes[i] + " ");
			}
			System.out.println("Lights turned off: " + print.toString());
			serialPort.closePort();// Close serial port
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

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
