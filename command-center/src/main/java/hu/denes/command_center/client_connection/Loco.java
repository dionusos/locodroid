package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;
import hu.denes.command_center.roco_connection.RailwayConnection.DIRECTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Loco {
	private final int address;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	private DIRECTION direction;

	public DIRECTION getDirection() {
		return direction;
	}

	public void setDirection(final DIRECTION dir) {
		this.direction = dir;
		connection.setDirection(address, dir);
	}

	private int speed;
	private int maxSpeed;
	private final Map<String, Integer> functionMap;
	private final Set<String> activatedFunctions;
	private final List<Loco> remoteLocos;
	private final RailwayConnection connection;

	public void activateFunction(final String func) {
		connection.turnFunctionOn(address, functionMap.get(func));
		activatedFunctions.add(func);
	}

	public void deactivateFunction(final String func) {
		connection.turnFunctionOff(address, functionMap.get(func));
		activatedFunctions.remove(func);
	}

	public Loco(final int address, final RailwayConnection connection) {
		this.address = address;
		this.connection = connection;
		direction = DIRECTION.FORWARD;
		remoteLocos = new ArrayList<Loco>();
		functionMap = new HashMap<String, Integer>();
		activatedFunctions = new HashSet<String>();
		maxSpeed = 128;
		speed = 0;
	}

	public void addFunction(final String function, final Integer val) {
		functionMap.put(function, val);
	}

	public int getAddress() {
		return address;
	}

	public void setMaxSpeed(final int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void turnLightsOn() {
		connection.turnLightsOn(address);
	}

	public void turnLightsOff() {
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
		if (remoteLocos.contains(loco)) {
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
