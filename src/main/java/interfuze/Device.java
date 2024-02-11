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

	// ---- Methods ---- //

	/**
	 * Calculates the average rainfall since a given time in millimeters (mm)
	 * 
	 * @param time The time to get the average rainfall since
	 * @return The average rainfall since the given time in millimeters (mm)
	 */
	public double getAverageRainfallSince(long time) {
		double totalRainfall = 0;
		int count = 0;
		for (Observation observation : observations) {
			if (observation.getObservationTime() > time) {
				totalRainfall += observation.getRainfall();
				count++;
			}
		}
		return (double) (totalRainfall / count);
	}

	/**
	 * Calculates the change in rainfall since a given time in millimeters (mm)
	 * 
	 * @param time The time to get the change in rainfall since
	 * @return The change in rainfall since the given time in millimeters (mm)
	 */
	public double getChangeInRainfallSince(long time) {
		// Sorting the observations by time	
		observations.sort((o1, o2) -> Long.compare(o1.getObservationTime(), o2.getObservationTime()));

		// Getting the first observation
		Observation first = null;
		for (Observation observation : observations) {
			if (observation.getObservationTime() > time) {
				first = observation;
				break;
			}
		}

		// Checking if there are no observations
		if (first == null) { return 0; }

		// Getting the last observation
		Observation last = observations.get(observations.size() - 1);

		// Calculating the change in rainfall
		return last.getRainfall() - first.getRainfall();
	}

	/**
	 * Checks if the observations exceed a threshold since a given time
	 * 
	 * @param threshold The threshold to check
	 * @param time The time to check since
	 * @return True if the observations exceed the threshold since the given time, false otherwise
	 */
	public boolean isObservationsExceedingThresholdSince(int threshold, long time) {
		for (Observation observation : observations) {
			if (observation.getObservationTime() > time && observation.getRainfall() > threshold) {
				return true;
			}
		}
		return false;
	}

	// ---- Setters ---- //

	/**
	 * Adds an observation to the device
	 * 
	 * @param observation The observation to add
	 */
	public void addObservation(Observation observation) {
		observations.add(observation);
	}

	// ---- Getters ---- //

	/**
	 * Gets the device ID
	 * 
	 * @return The device ID
	 */
	public int getDeviceID() {
		return deviceID;
	}

	/**
	 * Gets the device name
	 * 
	 * @return The device name
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * Gets the device location
	 * 
	 * @return The device location
	 */
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
