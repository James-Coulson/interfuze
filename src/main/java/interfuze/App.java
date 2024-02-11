package interfuze;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Main Application Entrypoint
 */
public class App {

    // ---- Variables ---- //

    /**
     * CSV file path
     */
    public static String CSV_FILE_PATH = "./test.csv";

    /**
     * Device CSV file path
     */
    public static String DEVICE_CSV_FILE_PATH = "./Devices.csv";

    /**
     * Observations CSV file path
     * TODO: Change to use command line arguments (possible hanbdle multiple observations files).
     */
    public static String OBSERVATIONS_CSV_FILE_PATH = "./Data1.csv";

    // ---- ANSI Colours ---- //

    /**
     * ANSI colour reset
     */
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * ANSI colour red
     */
    public static final String ANSI_RED = "\u001B[31m";

    /**
     * ANSI colour green
     */
    public static final String ANSI_GREEN = "\u001B[32m";

    /**
     * ANSI colour yellow (amber)
     */
    public static final String ANSI_YELLOW = "\u001B[33m";

    // ---- Methods ---- //

    /**
     * Parses the devices CSV file and stores the devices in a map.
     * 
     * TODO: Add statistics about the successrate of parsing the CSV (number of errors, etc).
     * 
     * @param devices The map of devices
     * @param filePath The file path of the devices CSV
     * @throws IOException If there is an error reading the file
     */
    private static void parseDevicesCSV(Map<Integer, Device> devices, String filePath) throws IOException {
        // Get file reader and parse the CSV
        Reader reader = Files.newBufferedReader(Paths.get(filePath));
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

        // Iterate through the records and store the devices
        for (CSVRecord record : csvParser) {
            int deviceID;
            String deviceName;
            String location;
            try {
                deviceID = Integer.parseInt(record.get("Device ID"));
                deviceName = record.get("Device Name");
                location = record.get("Location");
            } catch (NumberFormatException e) {
                System.out.println("Error parsing device ID - Device ID = " + (record.get("Device ID").equals("") ? "N/A" : record.get("Device ID")) + " - Skipping record");
                continue;
            }

            Device device = new Device(deviceID, deviceName, location);
            devices.put(deviceID, device);

            // TODO: Verbose flag only?
            System.out.println(device.toString());
        }
    }

    /**
     * Parses the observations CSV file and stores the observations in the devices.
     *
     * TODO: Add statistics about the successrate of parsing the CSV (number of errors, etc).
     * 
     * @param devices The map of devices
     * @param filePath The file path of the observations CSV
     * @throws IOException If there is an error reading the file
     * @return The current time (the time of the last observation parsed)
     */
    private static long parseObservationsCSV(Map<Integer, Device> devices, String filePath) throws IOException {
        // Get file reader and parse the CSV
        Reader reader = Files.newBufferedReader(Paths.get(filePath));
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

        // Parser for date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy h:mm");

        // Current time
        long currentTime = Long.MIN_VALUE;

        // Iterate through the records and store the observations
        for (CSVRecord record : csvParser) {
            int deviceID;
            long observationTime;
            int rainfall;
            try {
                deviceID = Integer.parseInt(record.get("Device ID"));
                observationTime = dateFormat.parse(record.get("Time")).getTime();
                rainfall = Integer.parseInt(record.get("Rainfall"));
            } catch (NumberFormatException e) {
                System.out.println("Error parsing observation - Device ID = " + (record.get("Device ID").equals("") ? "N/A" : record.get("Device ID")) + " - Skipping record");
                continue;
            } catch (ParseException e) {
                System.out.println("Error parsing observation time - Observation Time = " + (record.get("Time").equals("") ? "N/A" : record.get("Time")) + " - Skipping record");
                continue;
            }

            // Add the observation to the device
            Observation observation = new Observation(deviceID, observationTime, rainfall);
            devices.get(deviceID).addObservation(observation);

            // Update the current time
            if (observationTime > currentTime) { currentTime = observationTime; }

            // TODO: Verbose flag only?
            System.out.println(observation.toString());
        }

        return currentTime;
    }

    // ---- Main ---- //

    /**
     * Main method
     * 
     * @param args The arguments
     */
    public static void main( String[] args )
    {
        // -- Initialising Data Structures -- //

        // Map of devices
        Map<Integer, Device> devices = new HashMap<>();

        // Current time
        long currentTime;

        // ---- Parsing CSVs ---- //

        // Parse the devices CSV
        System.out.println("\n==== Loading Devices ====\n");
        try {
            parseDevicesCSV(devices, DEVICE_CSV_FILE_PATH);
        } catch (IOException e) {
            // TODO: Add verbose option to help debugging - e.printStackTrace();
            System.out.println("Error parsing devices CSV at " + DEVICE_CSV_FILE_PATH);
            System.exit(1);
            return;
        }

        // Parse the observations CSV
        // TODO: Allow multiple observations files
        System.out.println("\n==== Loading Observations ====\n");
        try {
            currentTime = parseObservationsCSV(devices, OBSERVATIONS_CSV_FILE_PATH);
        } catch (IOException e) {
            // TODO: Add verbose option to help debugging - e.printStackTrace();
            System.out.println("Error parsing observations CSV at " + OBSERVATIONS_CSV_FILE_PATH);
            System.exit(1);
            return;
        }

        // -- Data Processing -- //

        // Calculate observation lookback window
        long lookbackWindow = currentTime - TimeUnit.HOURS.toMillis(4); // 4 hours

        // DecimalFormat class (rounds doubles to 2 decimal places)
        DecimalFormat df = new DecimalFormat("#.##");

        // Iterate through the devices and print the observations
        System.out.println("\n==== Observations ====\n");
        for (Device device : devices.values()) {
            String output = device.getDeviceName() + "\t(" + device.getLocation() + ")\t- ";

            // Get average rainfall
            double averageRainfall = device.getAverageRainfallSince(lookbackWindow);

            // Format the output
            output +=  df.format(averageRainfall) + " mm";

            // Get change in rainfall
            double changeInRainfall = device.getChangeInRainfallSince(lookbackWindow);

            // Format the output
            output += "\t\t" + ANSI_RED + df.format(changeInRainfall) + ANSI_RESET + " mm";

            System.out.println(output);
        }
    }
}
