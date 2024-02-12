package interfuze;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Main Application Entrypoint
 */
public class App {

    // ---- Constants ---- //

    /**
     * Threshold for high rainfall
     */
    public static int THRESHOLD = 30;

    /**
     * Default data file path
     */
    private static String OBSERVATIONS_FILE_PATH = "./data";

    /**
     * Device CSV file path
     */
    public static String DEVICE_CSV_FILE_NAME = "Devices.csv";

    // ---- CLI Options ---- //

    /**
     * Verbose output flag
     */
    public static boolean VERBOSE = false;

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
     * @param devices The map of devices
     * @param filePath The file path of the devices CSV
     * @throws IOException If there is an error reading the file
     */
    private static void parseDevicesCSV(Map<Integer, Device> devices, String filePath) throws IOException {
        // Get file reader and parse the CSV
        Reader reader = Files.newBufferedReader(Paths.get(filePath));
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

        // Performance statistics
        int numErrors = 0;
        int numRecords = 0;

        // Iterate through the records and store the devices
        for (CSVRecord record : csvParser) {
            // Increment the number of records
            numRecords++;

            // Parse the record
            int deviceID;
            String deviceName;
            String location;
            try {
                deviceID = Integer.parseInt(record.get("Device ID"));
                deviceName = record.get("Device Name");
                location = record.get("Location");
            } catch (NumberFormatException e) {
                if (VERBOSE) { System.out.println(ANSI_RED + "Error" + ANSI_RESET + " parsing device ID - Device ID = " + (record.get("Device ID").equals("") ? "N/A" : record.get("Device ID")) + " - Skipping record"); }
                numErrors++;
                continue;
            }

            // Add the device to the map
            Device device = new Device(deviceID, deviceName, location);
            devices.put(deviceID, device);

            // Verbose output
            if (VERBOSE) { System.out.println(device.toString()); }
        }

        // Verbose output
        if (VERBOSE) { System.out.println("\nStatistics:\n - " + (numRecords - numErrors) + " out of " + numRecords + " rows processed successfully"); }
    }

    /**
     * Parses the observations CSV file and stores the observations in the devices.
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy k:mm");

        // Current time
        long currentTime = Long.MIN_VALUE;

        // Performance statistics
        int numErrors = 0;
        int numRecords = 0;

        // Iterate through the records and store the observations
        for (CSVRecord record : csvParser) {
            // Increment the number of records
            numRecords++;

            // Parse the record
            int deviceID;
            long observationTime;
            int rainfall;
            try {
                deviceID = Integer.parseInt(record.get("Device ID"));
                observationTime = dateFormat.parse(record.get("Time")).getTime();  // Time is stored in epoch milliseconds as it is easier to work with
                rainfall = Integer.parseInt(record.get("Rainfall"));
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Error" + ANSI_RESET + " parsing observation - Device ID = " + (record.get("Device ID").equals("") ? "N/A" : record.get("Device ID")) + " - Skipping record");
                numErrors++;
                continue;
            } catch (ParseException e) {
                System.out.println(ANSI_RED + "Error" + ANSI_RESET + " parsing observation time - Observation Time = " + (record.get("Time").equals("") ? "N/A" : record.get("Time")) + " - Skipping record");
                numErrors++;
                continue;
            }

            // Add the observation to the device
            Observation observation = new Observation(deviceID, observationTime, rainfall);
            devices.get(deviceID).addObservation(observation);

            // Update the current time
            if (observationTime > currentTime) { currentTime = observationTime; }

            // Verbose output
            if (VERBOSE) { System.out.println(observation.toString()); }
        }

        // Verbose output
        if (VERBOSE) { System.out.println("\nStatistics:\n - " + (numRecords - numErrors) + " out of " + numRecords + " rows processed successfully"); }

        return currentTime;
    }

    /**
     * Parses multiple observations CSV files and stores the observations in the devices.
     *
     * @param devices The map of devices
     * @param  dirPath The directory path of the observations CSVs
     * @throws IOException If there is an error reading the file
     * @return The current time (the time of the last observation parsed)
     */
    public static long parseObservationsCSVs(Map<Integer, Device> devices, String dirPath) throws IOException {
        // Current time
        long currentTime = Long.MIN_VALUE;
        
        // Get directory
        File dir = new File(OBSERVATIONS_FILE_PATH);

        // Get all CSV files in the directory
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));

        // Iterate through the files
        for (File file : files) {
            // Skip the devices CSV file
            if (file.getName().equals(DEVICE_CSV_FILE_NAME)) { continue; }

            // Verbose output
            if (VERBOSE) { System.out.println("\n Loading Observations CSV: " + file.getName() + "\n");}

            // Parse the observations CSV
            long fileTime = parseObservationsCSV(devices, file.getPath());

            // Update the current time
            if (fileTime > currentTime) { currentTime = fileTime; }
        }

        return currentTime;
    }

    /**
     * Formats the average rainfall with ANSI colours and a decimal format.
     * 
     * @param df The decimal format
     * @param averageRainfall The average rainfall
     * @return The formatted average rainfall
     */
    private static String formatAverageRainfall(DecimalFormat df, double averageRainfall, boolean exceedThreshold) {
        // Check if NaN
        if (Double.isNaN(averageRainfall)) { return ANSI_RED + "No data" + ANSI_RESET; }

        // Check if the average rainfall has surpassed a threshold
        if (exceedThreshold) {
            return ANSI_RED + df.format(averageRainfall) + ANSI_RESET + " mm " + ANSI_RED + "!!!" + ANSI_RESET;
        }

        // Format the average rainfall with ANSI colours
        if (averageRainfall < 10.0d) {
            return ANSI_GREEN + df.format(averageRainfall) + ANSI_RESET + " mm";
        } else if (averageRainfall < 15.0d) {
            return ANSI_YELLOW + df.format(averageRainfall) + ANSI_RESET + " mm";
        } else {
            return ANSI_RED + df.format(averageRainfall) + ANSI_RESET + " mm";
        }
    }

    /**
     * Formats the change in rainfall to a decimal format.
     * 
     * @param df The decimal format
     * @param changeInRainfall The change in rainfall
     * @return The formatted change in rainfall
     */
    private static String formatRainfallChange(DecimalFormat df, double changeInRainfall) {
        return df.format(changeInRainfall) + " mm";
    }

    /**
     * Parses the command line arguments.
     *
     * @param args The command line arguments
     */
    private static void parseArgs(String[] args) {
        // Create iterator
        Iterator<String> iter = Arrays.stream(args).iterator();

        // Iterate through the arguments
        while (iter.hasNext()) {
            String arg = iter.next();
            switch (arg) {

                // Verbose
                case "-v":
                case "--verbose":
                    VERBOSE = true;
                    break;

                // Manually set threshold
                case "-t":
                case "--threshold":
                    if (iter.hasNext()) {
                        try {
                            THRESHOLD = Integer.parseInt(iter.next());
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing threshold - Threshold = " + (iter.next().equals("") ? "N/A" : iter.next()));
                            System.exit(1);
                        }
                    } else {
                        System.out.println("Error parsing threshold - Threshold = N/A");
                        System.exit(1);
                    }
                    break;
                
                // Devices file name
                case "-d":
                case "--devices":
                    if (iter.hasNext()) {
                        DEVICE_CSV_FILE_NAME = iter.next();
                    } else {
                        System.out.println("Error parsing devices file name - Devices File Path = N/A");
                        System.exit(1);
                    }
                    break;
                
                // Observations file path
                case "-o":
                case "--observations":
                    if (iter.hasNext()) {
                        OBSERVATIONS_FILE_PATH = iter.next();
                    } else {
                        System.out.println("Error parsing observations file path - Observations File Path = N/A");
                        System.exit(1);
                    }
                    break;

                // Help
                case "-h":
                case "--help":
                    System.out.println("Usage: java -jar interfuze.jar [OPTIONS]");
                    System.out.println("Options:");
                    System.out.println("  -h, --help\t\t\t\t\t\tShow this help message");
                    System.out.println("  -v, --verbose\t\t\t\t\t\tVerbose output");
                    System.out.println("  -t, --threshold <THRESHOLD>\t\t\t\tManually set rainfall threshold (default = 30)");
                    System.out.println("  -d, --devices <DEVICES_FILE_NAME>\t\t\tSet devices file name, within observations directory (default = Devices.csv)");
                    System.out.println("  -o, --observations <OBSERVATIONS_FILE_PATH>\t\tSet observations file path (default = ./data)");
                    System.exit(0);
                    break;

                // Unknown argument
                default:
                    System.out.println("Error parsing arguments - Unknown argument: " + arg + " - try using --help");
                    System.exit(1);
                    break;
            }
        }
    }

    // ---- Main ---- //

    /**
     * Main method
     * 
     * @param args The arguments
     */
    public static void main( String[] args ) {

        // -- Command Line Arguments -- //

        parseArgs(args);

        // -- Initialising Data Structures -- //

        // Map of devices
        Map<Integer, Device> devices = new HashMap<>();

        // Current time
        long currentTime;

        // -- Parsing CSVs -- //

        // Parse the devices CSV
        if (VERBOSE) { System.out.println("\n==== Loading Devices ====\n"); }
        try {
            parseDevicesCSV(devices, OBSERVATIONS_FILE_PATH + "/" + DEVICE_CSV_FILE_NAME);
        } catch (Exception e) {
            if (VERBOSE) { e.printStackTrace(); }
            System.out.println("Error parsing devices CSV at " + OBSERVATIONS_FILE_PATH + "/" + DEVICE_CSV_FILE_NAME);
            System.exit(1);
            return;
        }

        // Parse the observations CSV
        if (VERBOSE) { System.out.println("\n==== Loading Observations ====\n"); }
        try {
            currentTime = parseObservationsCSVs(devices, OBSERVATIONS_FILE_PATH);
        } catch (IOException e) {
            if (VERBOSE) { e.printStackTrace(); }
            System.out.println("Error parsing observations CSV at " + OBSERVATIONS_FILE_PATH);
            System.exit(1);
            return;
        }

        // Verbose output
        if (VERBOSE) { System.out.println("Current time set to  epoch = " + currentTime); }

        // Verbose - Print devices
        if (VERBOSE) {
            System.out.println("\n==== Devices ====\n");
            for (Device device : devices.values()) {
                System.out.println("\n");
                System.out.println(device.toString());
                for (Observation observation : device.getObservations()) {
                    System.out.println(observation.toString());
                }
            }
        }

        // -- Data Processing & Output-- //

        // Verbose output
        if (VERBOSE) { System.out.println("\n==== Data Processing & Outputs ====\n"); }

        // Calculate observation lookback window
        long lookbackWindow = currentTime - TimeUnit.HOURS.toMillis(4); // 4 hours

        // Verbose output
        if (VERBOSE) { System.out.println("Lookback time set to  epoch = " + lookbackWindow); }

        // DecimalFormat class (rounds doubles to 2 decimal places)
        DecimalFormat df = new DecimalFormat("#.##");

        // Creating table header
        System.out.printf("---------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.printf("| %-20s | %-20s | %-20s | %-25s | %-28s |\n", "Device Name", "Device ID", "Location", "Average Rainfall (4 hrs)", "Change in Rainfall (4 hrs)");
        System.out.printf("---------------------------------------------------------------------------------------------------------------------------------\n");
        for (Device device : devices.values()) {
            // Get average rainfall and check if it has surpassed a threshold
            double averageRainfall = device.getAverageRainfallSince(lookbackWindow);
            boolean exceedThreshold = device.isObservationsExceedingThresholdSince(THRESHOLD, lookbackWindow);
            String averageRainfallOutput = formatAverageRainfall(df, averageRainfall, exceedThreshold);


            // Get change in rainfall
            double changeInRainfall = device.getChangeInRainfallSince(lookbackWindow);
            String changeInRainfallOutput = formatRainfallChange(df, changeInRainfall);

            // Calculating output adjustment for ANSI colour codes (if threshold is exceeded an adjustment is made to the output width to account for the extra characters in the ANSI colour codes)
            int outputAdjustment = 34 + (exceedThreshold ? 9 : 0);

            // Creating table row
            System.out.printf("| %-20s | %-20s | %-20s | %-" + outputAdjustment + "s | %-28s |\n", device.getDeviceName(), device.getDeviceID(), device.getLocation(), averageRainfallOutput, changeInRainfallOutput);
        }

        // Creating table footer
        System.out.printf("---------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.printf("| %-161s |\n", "Legend: " + ANSI_GREEN + "Green" + ANSI_RESET + " = Low (< 10 mm), " + ANSI_YELLOW + "Amber" + ANSI_RESET + " = Medium (< 15 mm), " + ANSI_RED + "Red" + ANSI_RESET + " = High (>= 15 mm), " + ANSI_RED + "!!!" + ANSI_RESET + " = " + THRESHOLD + " mm Threshold Exceeded");
        System.out.printf("---------------------------------------------------------------------------------------------------------------------------------\n");
    }
}
