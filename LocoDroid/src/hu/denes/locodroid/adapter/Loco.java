package hu.denes.locodroid.adapter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Loco implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 8258095225720305183L;

	private Integer address;

	public Integer getAddress() {
		return address;
	}

	public void setAddress(final Integer address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	private String name;

	private int maxSpeed;

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(final int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Loco(final Integer address) {
		this.address = address;
		activatedFunctions = new HashSet<String>();
	}

	private boolean ticked;

	public void setTicked(final boolean ticked) {
		this.ticked = ticked;
	}

	public boolean getTicked() {
		return ticked;
	}

	private int speed;
	private boolean lightsOn;
	private int direction;

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	public boolean isLightsOn() {
		return lightsOn;
	}

	public void setLightsOn(final boolean lightsOn) {
		this.lightsOn = lightsOn;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(final int direction) {
		this.direction = direction;
	}

	private final Set<String> activatedFunctions;

	public void setActivatedFunctions(final String functions) {
		activatedFunctions.clear();
		for (final String f : functions.split(",")) {
			activatedFunctions.add(f);
		}
	}

	public Set<String> getActivatedFunctions() {
		return activatedFunctions;
	}

}
