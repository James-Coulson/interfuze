package interfuze;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the information of a device.
 */
public class Device {
	
	// ---- Variables ---- //

	/**
	 * Device ID
	 */
	private final int deviceID;

	/**
	 * Device name
	 */
	private final String deviceName;

	/**
	 * Device location
	 */
	private final String location;

	/**
	 * List of observations
	 */
	private List<Observation> observations = new ArrayList<>();

	// ---- Constructor ---- //

	/**
	 * Constructor
	 * 
	 * @param deviceID The unique ID of the device
	 * @param deviceName The name of the device
	 * @param location The location of the device
	 */
	public Device(int deviceID, String deviceName, String location) {
		this.deviceID = deviceID;
		this.deviceName = deviceName;
		this.location = location;
	}

	// ---- Getters ---- //

	public int getDeviceID() {
		return deviceID;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getLocation() {
		return location;
	}

	// ---- Primitive Methods ---- //

	@Override
	public String toString() {
		return "Device [deviceID=" + deviceID + ", deviceName=" + deviceName + ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		return deviceID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (deviceID != other.deviceID)
			return false;
		if (deviceName == null) {
			if (other.deviceName != null)
				return false;
		} else if (!deviceName.equals(other.deviceName))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}
}
