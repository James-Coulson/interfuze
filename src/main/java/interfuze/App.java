package interfuze;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Hello world!
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
            System.out.println(device.toString());
        }
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

        // CSVParser csvParser;
        // try {
        //         Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));
        //         csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
        // } catch (IOException e) {
        //         e.printStackTrace();
        //         System.exit(1);
        //         return;
        // }

        // // ! ---- TESTING CSV READING ---- ! //
        // for (CSVRecord record : csvParser) {
        //         System.out.println(record.get("Phone"));
        // }
    }
}
