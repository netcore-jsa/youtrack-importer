package software.netcore.youtrack.buisness.service.csv.pojo;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@Getter
@Builder
public class CsvReadResult {

    private String fileName;
    private List<String> columns;
    private List<List<String>> rows;

    public Collection<String> getUniqueColumns() {
        return new HashSet<>(columns);
    }

    public Collection<String> getUniqueValuesFromColumn(String column) {
        return getUniqueValuesFromColumns(Collections.singleton(column));
    }

    public Collection<String> getUniqueValuesFromColumns(Collection<String> columns) {
        Collection<String> values = new HashSet<>();
        List<Integer> columnsIndexes = new ArrayList<>();
        // determine columns indexes
        for (int i = 0; i < this.columns.size(); i++) {
            for (String selectedColumn : columns) {
                if (Objects.equals(this.columns.get(i), selectedColumn)) {
                    columnsIndexes.add(i);
                }
            }
        }

        // read values from columns
        rows.forEach(row -> columnsIndexes.forEach(index -> {
            String value = row.get(index);
            if (!StringUtils.isEmpty(value)) {
                values.add(value);
            }
        }));
        return values;
    }

    @Override
    public String toString() {
        return "CsvReadResult{" +
                "fileName='" + fileName + '\'' +
                ", columns=" + Arrays.toString(columns.toArray()) +
                ", rowsCount=" + rows.size() +
                '}';
    }

}
