package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Loco {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;
	private Integer address;
	private String name;
	private int direction;
	private int speed;
	private int maxSpeed;
	private boolean lightsOn;

	public boolean isLightsOn() {
		return lightsOn;
	}

	public void setLightsOn(final boolean lightsOn) {
		this.lightsOn = lightsOn;
	}

	@Transient
	private Map<String, Integer> functionMap;
	@Transient
	private Set<String> activatedFunctions;
	@Transient
	private Set<Loco> remoteLocos;
	@Transient
	private RailwayConnection connection;

	public void setRailwayConnection(final RailwayConnection c) {
		connection = c;
	}

	private void init() {
		remoteLocos = new HashSet<Loco>();
		functionMap = new HashMap<String, Integer>();
		activatedFunctions = new HashSet<String>();
	}

	public Loco() {
		init();
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(final int dir) {
		this.direction = dir;
		setSpeed(speed);
	}

	public void activateFunction(final String func) {
		connection.turnFunctionOn(address, functionMap.get(func));
		activatedFunctions.add(func);
	}

	public void deactivateFunction(final String func) {
		connection.turnFunctionOff(address, functionMap.get(func));
		activatedFunctions.remove(func);
	}

	public Loco(final Integer address, final RailwayConnection connection) {
		this.address = address;
		this.connection = connection;
		direction = 128;
		remoteLocos = new HashSet<Loco>();
		functionMap = new HashMap<String, Integer>();
		activatedFunctions = new HashSet<String>();
		maxSpeed = 127;
		speed = 0;
		lightsOn = false;
	}

	public void addFunction(final String function, final Integer val) {
		functionMap.put(function, val);
	}

	public Integer getAddress() {
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
		lightsOn = true;
	}

	public void turnLightsOff() {
		connection.turnLightsOff(address);
		lightsOn = false;
	}

	public void setSpeed(final int speed) {
		if (speed > maxSpeed) {
			this.speed = maxSpeed;
		} else if (speed < 0) {
			this.speed = 0;
		} else {
			this.speed = speed;
		}
		connection.setSpeed(address, this.speed + direction, maxSpeed);
		setRemoteLocoSpeed(this.speed);
	}

	public int getSpeed() {
		return speed;
	}

	public Collection<Loco> getRemoteLocos() {
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
