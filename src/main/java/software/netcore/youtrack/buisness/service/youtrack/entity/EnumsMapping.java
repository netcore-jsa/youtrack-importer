package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;

import java.util.*;

/**
 * Maps CSV enums to YouTrack enum fields
 *
 * @since v. 1.0.0
 */
@Getter
public class EnumsMapping implements CsvColumnValuesMapping<ProjectCustomField> {

    /**
     * CSV users to YouTrack users mapping
     */
    private final Map<String, ProjectCustomField> mapping = new HashMap<>();

    /**
     * CSV columns which rows values are supposed to be Jira users
     */
    private final Set<String> csvColumns = new HashSet<>();

    @Override
    public String toString() {
        return "EnumsMapping{" +
                "mapping=" + StringUtils.join(mapping) +
                ", csvColumns=" + Arrays.toString(csvColumns.toArray()) +
                '}';
    }

}
