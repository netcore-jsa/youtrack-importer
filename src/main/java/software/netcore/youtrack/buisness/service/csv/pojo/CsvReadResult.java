package software.netcore.youtrack.buisness.service.csv.pojo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @since v. 1.0.0
 */
@Getter
@Builder
public class CsvReadResult {

    private String fileName;
    private List<String> columns;
    private List<List<String>> rows;

}
