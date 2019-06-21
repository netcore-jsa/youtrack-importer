package software.netcore.youtrack.buisness.service.youtrack.entity;

import java.util.Map;
import java.util.Set;

/**
 * @since v. 1.0.0
 * @param <T>
 */
public interface CsvColumnValuesMapping<T> {

    Map<String, T> getMapping();

    Set<String> getCsvColumns();

}
