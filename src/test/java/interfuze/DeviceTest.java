package interfuze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for the Device class
 * 
 * @see Device
 */
public class DeviceTest {

    /**
     * Tests the Device class
     */
	@Test
    public void testDevice() {
        // Test data
        int deviceID = 1;
        String deviceName = "Device1";
        String location = "Location1";

        // Create a device
        Device device = new Device(deviceID, deviceName, location);

        // Test getters
        assertEquals(deviceID, device.getDeviceID());
        assertEquals(deviceName, device.getDeviceName());
        assertEquals(location, device.getLocation());

        // Test toString
        String expectedToString = "Device [deviceID=" + deviceID + ", deviceName=" + deviceName + ", location=" + location + "]";
        assertEquals(expectedToString, device.toString());

        // Test hashCode and equals
        Device sameDevice = new Device(deviceID, deviceName, location);
        assertEquals(device.hashCode(), sameDevice.hashCode());
        assertTrue(device.equals(sameDevice));
        Device differentDevice = new Device(2, deviceName, location);
        assertNotEquals(device.hashCode(), differentDevice.hashCode());
        assertFalse(device.equals(differentDevice));

        // Test adding observations and calculating average rainfall
        long currentTime = System.currentTimeMillis();
        device.addObservation(new Observation(deviceID, currentTime - 1000, 10)); // 1 second ago
        device.addObservation(new Observation(deviceID, currentTime - 2000, 20)); // 2 seconds ago
        device.addObservation(new Observation(deviceID, currentTime - 3000, 30)); // 3 seconds ago

        // The average rainfall since 2.5 seconds ago should be (10 + 20) / 2 = 15
        assertEquals(15, device.getAverageRainfallSince(currentTime - 2500), 0.001);

        // The average rainfall since 1.5 seconds ago should be 10
        assertEquals(10, device.getAverageRainfallSince(currentTime - 1500), 0.001);

        // Test change in rainfall since
        assertEquals(-10, device.getChangeInRainfallSince(currentTime - 2500), 0.001);

        // Test exceed threshold
        assertFalse(device.isObservationsExceedingThresholdSince(50, currentTime - 2500));
        assertFalse(device.isObservationsExceedingThresholdSince(20, currentTime - 2500));
        assertTrue(device.isObservationsExceedingThresholdSince(5, currentTime - 2500));
    }
}
