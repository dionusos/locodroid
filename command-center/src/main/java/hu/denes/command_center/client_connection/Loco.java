package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.RailwayConnection;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Loco implements Serializable {
	private static final long serialVersionUID = -8941204227321172539L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ID;
	private Integer address;
	private String name;
	private int direction;
	private int speed;
	private int maxSpeed;
	private boolean lightsOn;
	private byte[] functionGroups;

	public byte[] getFunctionGroups() {
		return functionGroups;
	}

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
	@ElementCollection
	@Column(name = "remote_locos")
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
		functionGroups = new byte[] { 0, 0, 0 };
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
		final int oldDir = this.direction;
		this.direction = dir;
		final int newDir = this.direction;
		setSpeed(speed);

		if (oldDir != newDir) {
			for (final Loco l : remoteLocos) {
				l.changeDirection();
			}
		}
	}

	public void changeDirection() {
		if (direction == 128) {
			direction = 0;
		} else {
			direction = 128;
		}
		setSpeed(speed);
	}

	public void activateFunction(final String func) {
		byte fnc = Byte.parseByte(func);
		byte grp = 0;
		if (fnc > 8) {
			grp = 2;
			fnc -= 8;
		} else if (fnc > 4) {
			grp = 1;
			fnc -= 4;
		}
		functionGroups[grp] |= (byte) Math.pow(2, (fnc % 5) - 1);
		connection.switchFunction(address, functionGroups[grp], grp);
		activatedFunctions.add(func);
	}

	public void deactivateFunction(final String func) {
		byte fnc = Byte.parseByte(func);
		byte grp = 0;
		if (fnc > 8) {
			grp = 2;
			fnc -= 8;
		} else if (fnc > 4) {
			grp = 1;
			fnc -= 4;
		}
		functionGroups[grp] &= (byte) (255 - Math.pow(2, (fnc % 5) - 1));
		connection.switchFunction(address, functionGroups[grp], grp);
		activatedFunctions.remove(func);
	}

	public Loco(final Integer address, final RailwayConnection connection) {
		this.address = address;
		this.connection = connection;
		direction = 128;
		init();
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
		functionGroups[0] |= (byte) 16;
		connection.switchFunction(address, functionGroups[0], 0);
		lightsOn = true;
	}

	public void turnLightsOff() {
		functionGroups[0] &= (byte) (255 - 16);
		connection.switchFunction(address, functionGroups[0], 0);
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
