package interfuze;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Hello world!
 */
public class App {

    /**
     * CSV file path
     */
    public static String CSV_FILE_PATH = "./test.csv";

    public static void main( String[] args )
    {
       // ---- Parsing CSV ---- //

       CSVParser csvParser;
       try {
            Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));
            csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
       } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
       }

       // ! ---- TESTING CSV READING ---- ! //
       for (CSVRecord record : csvParser) {
            System.out.println(record.get("Phone"));
       }
    }
}
