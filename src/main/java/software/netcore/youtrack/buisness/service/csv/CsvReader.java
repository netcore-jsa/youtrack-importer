package software.netcore.youtrack.buisness.service.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since v. 1.0.0
 */
public class CsvReader {

    public CsvReadResult read(String fileName, InputStream content) throws IOException {
        InputStreamReader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
        CSVReader csvReader = new CSVReaderBuilder(reader).build();
        List<String[]> newLines = csvReader.readAll();

        List<String> columns = Arrays.asList(newLines.get(0));
        List<List<String>> rows = new ArrayList<>(newLines.size() - 1);

        for (int i = 1; i < newLines.size(); i++) {
            rows.add(Arrays.asList(newLines.get(i)));
        }

        return CsvReadResult.builder()
                .fileName(fileName)
                .columns(columns)
                .rows(rows)
                .build();
    }

}
