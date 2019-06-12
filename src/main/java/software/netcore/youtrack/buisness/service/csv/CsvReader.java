package software.netcore.youtrack.buisness.service.csv;

import org.apache.commons.io.IOUtils;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since v. 1.0.0
 */
public class CsvReader {

    private static final String SPLIT_CHARACTER = ";";

    public CsvReadResult read(String fileName, InputStream content) throws IOException {
        List<String> lines = IOUtils.readLines(content, "UTF-8");
        List<String> columns = Arrays.asList(lines.get(0).split(SPLIT_CHARACTER));
        List<List<String>> rows = new ArrayList<>(lines.size() - 1);
        for (int i = 1; i < lines.size(); i++) {
            rows.add(Arrays.asList(lines.get(i).split(SPLIT_CHARACTER)));
        }
        return CsvReadResult.builder()
                .fileName(fileName)
                .columns(columns)
                .rows(rows)
                .build();
    }

}
