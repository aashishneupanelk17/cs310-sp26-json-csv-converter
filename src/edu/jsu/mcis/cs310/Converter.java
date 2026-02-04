package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // Read CSV
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> rows = reader.readAll();
            reader.close();

            // Create JSON containers
            JsonObject root = new JsonObject();
            JsonArray prodNums = new JsonArray();
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();

            // Header row
            String[] header = rows.get(0);
            for (int i = 0; i < header.length; i++) {
                colHeadings.add(header[i]);
            }

            // Data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                // ProdNum
                prodNums.add(row[0]);

                JsonArray dataRow = new JsonArray();
                dataRow.add(row[1]);                       // Title
                dataRow.add(Integer.parseInt(row[2]));     // Season
                dataRow.add(Integer.parseInt(row[3]));     // Episode
                dataRow.add(row[4]);                       // Stardate
                dataRow.add(row[5]);                       // OriginalAirdate
                dataRow.add(row[6]);                       // RemasteredAirdate

                data.add(dataRow);
            }

            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);

            result = Jsoner.serialize(root);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            // Read JSON
            JsonObject root = Jsoner.deserialize(jsonString, new JsonObject());

            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray dataRows = (JsonArray) root.get("Data");

            // CSV Writer
            StringWriter sw = new StringWriter();
            CSVWriter writer = new CSVWriter(sw);

            // Header row
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                header[i] = colHeadings.get(i).toString();
            }
            writer.writeNext(header);

            // Data rows
            for (int i = 0; i < dataRows.size(); i++) {
                JsonArray dataRow = (JsonArray) dataRows.get(i);
                String[] row = new String[7];

                row[0] = prodNums.get(i).toString();
                row[1] = dataRow.get(0).toString();
                row[2] = dataRow.get(1).toString();

                // Pad Episode to 2 digits
                int episode = Integer.parseInt(dataRow.get(2).toString());
                row[3] = String.format("%02d", episode);

                row[4] = dataRow.get(3).toString();
                row[5] = dataRow.get(4).toString();
                row[6] = dataRow.get(5).toString();

                writer.writeNext(row);
            }

            writer.close();
            result = sw.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
