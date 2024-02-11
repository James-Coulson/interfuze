package interfuze;

/**
 * Stores the infromation of an individual rainfall observation.
 */
public class Observation {
	
	// ---- Variables ---- //

	/**
	 * Device ID
	 */
	private final int deviceID;

	/**
	 * Observation time (stored in as epoch milliseconds)
	 */
	private final long observationTime;

	/**
	 * Rainfall amount (test data only specifies integers for rainfall)
	 */
	private final int rainfall;

	// ---- Constructor ---- //

	/**
	 * Constructor
	 * 
	 * @param deviceID The unique ID of the device
	 * @param observationTime The time of the observation
	 * @param rainfall The amount of rainfall
	 */
	public Observation(int deviceID, long observationTime, int rainfall) {
		this.deviceID = deviceID;
		this.observationTime = observationTime;
		this.rainfall = rainfall;
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
	 * Gets the observation time
	 * 
	 * @return The observation time
	 */
	public long getObservationTime() {
		return observationTime;
	}

	/**
	 * Gets the rainfall amount
	 * 
	 * @return The rainfall amount
	 */
	public int getRainfall() {
		return rainfall;
	}

	// ---- Primitive Methods ---- //

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceID;
		result = prime * result + (int) (observationTime ^ (observationTime >>> 32));
		result = prime * result + rainfall;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Observation other = (Observation) obj;
		if (deviceID != other.deviceID)
			return false;
		if (observationTime != other.observationTime)
			return false;
		if (rainfall != other.rainfall)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Observations [deviceID=" + deviceID + ", observationTime=" + observationTime + ", rainfall=" + rainfall
				+ "]";
	}

	
}
