package interfuze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for the Observation class
 * 
 * @see Observation
 */
public class ObservationTest {

    /**
     * Tests the Observation class
     */
	@Test
    public void testObservation() {
        // Test data
        int deviceID = 1;
        long observationTime = 1627552800000L; // Thursday, 29 July 2021 10:00:00 GMT
        int rainfall = 10;

        // Create an observation
        Observation observation = new Observation(deviceID, observationTime, rainfall);

        // Test getters
        assertEquals(deviceID, observation.getDeviceID());
        assertEquals(observationTime, observation.getObservationTime());
        assertEquals(rainfall, observation.getRainfall());

        // Test hashCode and equals
        Observation sameObservation = new Observation(deviceID, observationTime, rainfall);
        assertEquals(observation.hashCode(), sameObservation.hashCode());
        assertTrue(observation.equals(sameObservation));
        Observation differentObservation = new Observation(2, observationTime, rainfall);
        assertNotEquals(observation.hashCode(), differentObservation.hashCode());
        assertFalse(observation.equals(differentObservation));

        // Test toString
        String expectedToString = "Observations [deviceID=" + deviceID + ", observationTime=" + observationTime + ", rainfall=" + rainfall + "]";
        assertEquals(expectedToString, observation.toString());
    }
}
