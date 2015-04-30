package hu.denes.command_center.roco_connection;

import hu.denes.command_center.client_connection.NetworkConnection;

import java.util.HashMap;
import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class XpressNetRailwayConnection implements RailwayConnection {
	enum State {
		START, _0xE3, _0x40, _ADDR_H, _ADDR_L, XOR
	};

	State state = State.START;
	int controlOvertakenAddress;
	String serialTerminalName;
	SerialPort serialPort;
	NetworkConnection networkConnection;

	public void setNetworkConnection(final NetworkConnection conn) {
		networkConnection = conn;
	}

	final Map<Integer, Byte> locoArressIdentificationMap = new HashMap<Integer, Byte>();

	private boolean initSerialPort() {
		serialPort = new SerialPort(serialTerminalName);
		try {
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} catch (final SerialPortException e) {
			System.out.println("ERROR: Initing serial port failed!");
			return false;
		}
		try {
			serialPort.addEventListener(new SerialPortEventListener() {

				@Override
				public void serialEvent(final SerialPortEvent event) {

					try {
						final byte[] bytes = serialPort.readBytes();
						if (bytes == null) {
							return;
						}
						for (final Byte b : bytes) {
							if (b == null) {
								continue;
							}
							// System.out.println("Byte: " + b);
							// System.out.println("Sate:" + state);
							switch (b) {
							case (byte) 0xE3:
								if (state == State.START) {
									state = State._0xE3;
									continue;
								}
								break;
							case 0x40:
								if (state == State._0xE3) {
									state = State._0x40;
									continue;
								}
								break;
							}
							if (state == State._0x40) {
								controlOvertakenAddress = 256 * b.intValue();
								state = State._ADDR_H;
							} else if (state == State._ADDR_H) {
								controlOvertakenAddress += b;
								state = State._ADDR_L;
							}
							if (state == State._ADDR_L) {
								networkConnection
								.controlIsOvertakenByMultiMaus(
										controlOvertakenAddress,
										"Control is overtaken by another controller");
								state = State.START;
							}
						}

					} catch (final SerialPortException e) { // TODO
						e.printStackTrace();
					}

				}
			});
		} catch (final SerialPortException e) {
			System.out
			.println("ERROR: Unable to add EventListener to SerialPort!");
			return false;
		}
		return true;
	}

	public XpressNetRailwayConnection(final String serialTerminalName) {
		this.serialTerminalName = serialTerminalName;
		locoArressIdentificationMap.put(13, (byte) 0x10);
		locoArressIdentificationMap.put(26, (byte) 0x11);
		locoArressIdentificationMap.put(27, (byte) 0x12);
		locoArressIdentificationMap.put(127, (byte) 0x13);
		if (!initSerialPort()) {
			return;
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
	public synchronized void setSpeed(final int address, final int speed,
	        final int maxSpeed) {
		final byte[] bytes = new byte[6];

		bytes[0] = (byte) 0xe4;
		bytes[1] = locoArressIdentificationMap.get(maxSpeed);
		calculateAddressBytes(address, bytes);
		bytes[4] = (byte) speed;
		bytes[5] = calculateXor(bytes);

		try {

			serialPort.writeBytes(bytes);
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
	public synchronized void switchFunction(final int address,
	        final int function, final int group) {
		final byte[] bytes = new byte[6];

		bytes[0] = (byte) 0xe4;
		bytes[1] = (byte) (0x20 + group);

		calculateAddressBytes(address, bytes);
		bytes[4] = (byte) function;
		bytes[5] = calculateXor(bytes);

		try {
			serialPort.writeBytes(bytes);
		} catch (final SerialPortException ex) {
			System.out.println(ex);
		}

	}

	private void calculateAddressBytes(final int address, final byte[] bytes) {
		if (address > 255) {
			bytes[2] = (byte) (address / 255);
			bytes[3] = (byte) (address % 255);
		} else {
			bytes[2] = (byte) 0x00;
			bytes[3] = (byte) address;
		}
	}

	@Override
	public synchronized void stopOperations() {
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
	public synchronized void resumeOperations() {
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
