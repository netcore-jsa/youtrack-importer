package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import software.netcore.youtrack.buisness.client.entity.CustomField;

import java.util.*;

/**
 * Maps CSV enums to YouTrack enum fields
 *
 * @since v. 1.0.0
 */
@Getter
public class EnumsConfig {

    /**
     * CSV users to YouTrack users mapping
     */
    private final Map<String, CustomField> mapping = new HashMap<>();

    /**
     * Selected CSV columns which rows values are supposed to be Jira users
     */
    private final Set<String> selectedCsvColumns = new HashSet<>();

    @Override
    public String toString() {
        return "EnumsConfig{" +
                "mapping=" + StringUtils.join(mapping) +
                ", selectedCsvColumns=" + Arrays.toString(selectedCsvColumns.toArray()) +
                '}';
    }

}
