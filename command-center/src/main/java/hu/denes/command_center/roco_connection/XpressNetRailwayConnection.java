package hu.denes.command_center.roco_connection;

import java.util.HashMap;
import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class XpressNetRailwayConnection implements RailwayConnection {
	String serialTerminalName;
	final SerialPort serialPort;
	final Map<Integer, Byte> locoArressIdentificationMap = new HashMap<Integer, Byte>();

	public XpressNetRailwayConnection(final String serialTerminalName) {
		this.serialTerminalName = serialTerminalName;
		serialPort = new SerialPort(serialTerminalName);
		try {
			serialPort.openPort(); // Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} catch (final SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		locoArressIdentificationMap.put(13, (byte) 0x10);
		locoArressIdentificationMap.put(26, (byte) 0x11);
		locoArressIdentificationMap.put(27, (byte) 0x12);
		locoArressIdentificationMap.put(127, (byte) 0x13);

		try {
			serialPort.addEventListener(new SerialPortEventListener() {

				@Override
				public void serialEvent(final SerialPortEvent event) {
					try {

						for (final byte b : serialPort.readBytes()) {
							System.out.print(b);
						}
						System.out.println();

					} catch (final SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		} catch (final SerialPortException e) {
			System.out
					.println("ERROR: Unable to add EventListener to SerialPort!");
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			serialPort.closePort();
		} catch (final SerialPortException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSpeed(final int address, final int speed, final int maxSpeed) {
		final byte[] bytes = new byte[6];

		bytes[0] = (byte) 0xe4;
		bytes[1] = locoArressIdentificationMap.get(maxSpeed);
		if (address > 255) {
			bytes[2] = (byte) (address / 255);
			bytes[3] = (byte) (address % 255);
		} else {
			bytes[2] = (byte) 0x00;
			bytes[3] = (byte) address;
		}
		bytes[4] = (byte) speed;
		bytes[5] = calculateXor(bytes);

		try {

			serialPort.writeBytes(bytes);
			System.out.println("Speed is set to " + speed + "@" + address);
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

		try {
			serialPort.writeBytes(bytes);
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

		try {
			serialPort.writeBytes(bytes);
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

	@Override
	public void stopOperations() {
		final byte[] bytes = new byte[3];

		bytes[0] = (byte) 0x21;
		bytes[1] = (byte) 0x80;
		bytes[2] = (byte) 0xA1;

		try {
			serialPort.writeBytes(bytes);
			System.out.println("EMERGENCY STOP");
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

	}

	@Override
	public void resumeOperations() {
		final byte[] bytes = new byte[3];

		bytes[0] = (byte) 0x21;
		bytes[1] = (byte) 0x81;
		bytes[2] = (byte) 0xA0;

		try {
			serialPort.writeBytes(bytes);
			System.out.println("RESUME OPERATION");
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

	}

}
