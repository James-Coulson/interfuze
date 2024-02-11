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
	@Test
    public void testDevice() {
        int deviceID = 1;
        String deviceName = "Device1";
        String location = "Location1";

        Device device = new Device(deviceID, deviceName, location);

        assertEquals(deviceID, device.getDeviceID());
        assertEquals(deviceName, device.getDeviceName());
        assertEquals(location, device.getLocation());

        String expectedToString = "Device [deviceID=" + deviceID + ", deviceName=" + deviceName + ", location=" + location + "]";
        assertEquals(expectedToString, device.toString());

        Device sameDevice = new Device(deviceID, deviceName, location);
        assertEquals(device.hashCode(), sameDevice.hashCode());
        assertTrue(device.equals(sameDevice));

        Device differentDevice = new Device(2, deviceName, location);
        assertNotEquals(device.hashCode(), differentDevice.hashCode());
        assertFalse(device.equals(differentDevice));
    }
}
