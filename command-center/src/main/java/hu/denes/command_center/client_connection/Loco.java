package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.Connection;
import hu.denes.command_center.roco_connection.Connection.DIRECTION;
import hu.denes.command_center.roco_connection.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Loco {
	private final int address;
	private String name;
	private final DIRECTION direction;
	private int speed;
	private int maxSpeed;
	private boolean lightsOn;
	private Map<String, Functions.FUNCTION> functionMap;
	private final List<Loco> remoteLocos;
	private final Connection connection;

	public Loco(final int address, final Connection connection) {
		this.address = address;
		this.connection = connection;
		direction = DIRECTION.FORWARD;
		remoteLocos = new ArrayList<Loco>();
		maxSpeed = 128;
		speed = 0;
		lightsOn = false;
	}

	public void setMaxSpeed(final int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void turnLightsOn() {
		lightsOn = true;
		connection.turnLightsOn(address);
	}

	public void turnLightsOff() {
		lightsOn = false;
		connection.turnLightsOff(address);
	}

	public void setSpeed(final int speed) {
		if (speed > maxSpeed) {
			this.speed = maxSpeed;
		} else if (speed < 0) {
			this.speed = 0;
		} else {
			this.speed = speed;
		}
		connection.setSpeed(address, this.speed);
		setRemoteLocoSpeed(this.speed);
	}

	public int getSpeed() {
		return speed;
	}

	public List<Loco> getRemoteLocos() {
		return remoteLocos;
	}

	public void addRemoteLoco(final Loco loco) {
		if (remoteLocos == null) {
			return;
		}
		if (loco == this) {
			return;
		}
		remoteLocos.add(loco);
	}

	public void removeRemoteLoco(final Loco loco) {
		if (remoteLocos == null) {
			return;
		}
		remoteLocos.remove(loco);
	}

	public void removeAllRemoteLocos() {
		if (remoteLocos == null) {
			return;
		}
		remoteLocos.clear();
	}

	private void setRemoteLocoSpeed(final int speed) {
		if (remoteLocos == null) {
			return;
		}
		for (final Loco loco : remoteLocos) {
			loco.setSpeed(speed);
		}
	}
}
