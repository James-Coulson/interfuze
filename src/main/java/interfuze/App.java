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

    /**
     * Formats the average rainfall with ANSI colours and a decimal format.
     * 
     * TODO: If any reading has surpassed a threshold, print a warning message.
     * 
     * @param df The decimal format
     * @param averageRainfall The average rainfall
     * @return The formatted average rainfall
     */
    private static String formatAverageRainfall(DecimalFormat df, double averageRainfall, boolean exceedThreshold) {
        if (exceedThreshold) {
            return ANSI_RED + df.format(averageRainfall) + ANSI_RESET + " mm " + ANSI_RED + "!!!" + ANSI_RESET;
        }

        if (averageRainfall < 10.0d) {
            return ANSI_GREEN + df.format(averageRainfall) + ANSI_RESET + " mm";
        } else if (averageRainfall < 15.0d) {
            return ANSI_YELLOW + df.format(averageRainfall) + ANSI_RESET + " mm";
        } else {
            return ANSI_RED + df.format(averageRainfall) + ANSI_RESET + " mm";
        }
    }

    /**
     * Formats the change in rainfall with ANSI colours and a decimal format.
     * 
     * @param df The decimal format
     * @param changeInRainfall The change in rainfall
     * @return The formatted change in rainfall
     */
    private static String formatRainfallChange(DecimalFormat df, double changeInRainfall) {
        if (changeInRainfall > 0.005d) {
            return ANSI_GREEN + df.format(changeInRainfall) + ANSI_RESET + " mm";
        } else if (changeInRainfall < 0.005d) {
            return ANSI_YELLOW + df.format(changeInRainfall) + ANSI_RESET + " mm";
        }

        return df.format(changeInRainfall) + " mm";
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

        // -- Data Processing & Output-- //

        // Calculate observation lookback window
        long lookbackWindow = currentTime - TimeUnit.HOURS.toMillis(4); // 4 hours

        // DecimalFormat class (rounds doubles to 2 decimal places)
        DecimalFormat df = new DecimalFormat("#.##");

        // Creating table header
        System.out.printf("--------------------------------------------------------------------------------------------------------------------\n");
        System.out.printf("| %-20s | %-20s | %-20s | %-20s | %-20s |\n", "Device Name", "Device ID", "Location", "Average Rainfall", "Change in Rainfall");
        System.out.printf("--------------------------------------------------------------------------------------------------------------------\n");
        for (Device device : devices.values()) {
            // Get average rainfall and check if it has surpassed a threshold
            double averageRainfall = device.getAverageRainfallSince(lookbackWindow);
            boolean exceedThreshold = device.isObservationsExceedingThresholdSince(10, lookbackWindow);
            String averageRainfallOutput = formatAverageRainfall(df, averageRainfall, exceedThreshold);


            // Get change in rainfall
            double changeInRainfall = device.getChangeInRainfallSince(lookbackWindow);
            String changeInRainfallOutput = formatRainfallChange(df, changeInRainfall);

            // Calculating output adjustment for ANSI colour codes (if threshold is exceeded an adjustment is made to the output width to account for the extra characters in the ANSI colour codes)
            int outputAdjustment = 29 + (exceedThreshold ? 9 : 0);

            // Creating table row
            // * Note: The final two fields speocify %-29s to allow for the ANSI colour codes.
            System.out.printf("| %-20s | %-20s | %-20s | %-" + outputAdjustment + "s | %-29s |\n", device.getDeviceName(), device.getDeviceID(), device.getLocation(), averageRainfallOutput, changeInRainfallOutput);
        }

        // Creating table footer
        System.out.printf("--------------------------------------------------------------------------------------------------------------------\n");
        System.out.printf("| %-148s |\n", "Legend: " + ANSI_GREEN + "Green" + ANSI_RESET + " = Low (< 10mm), " + ANSI_YELLOW + "Yellow" + ANSI_RESET + " = Medium (< 15mm), " + ANSI_RED + "Red" + ANSI_RESET + " = High (>= 15mm), " + ANSI_RED + "!!!" + ANSI_RESET + " = " + 30 + " mm Threshold Exceeded");
        System.out.printf("--------------------------------------------------------------------------------------------------------------------\n");
    }
}
