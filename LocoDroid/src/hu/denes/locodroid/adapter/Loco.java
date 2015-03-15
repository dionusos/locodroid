package hu.denes.locodroid.adapter;

public class Loco {
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
	}

}
