package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import software.netcore.youtrack.buisness.client.entity.ProjectCustomField;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps YouTrack custom fields to CSV columns
 *
 * @since v. 1.0.0
 */
@Getter
public class CustomFieldsMapper {

    private final Map<ProjectCustomField, String> mapping = new HashMap<>();

    @Override
    public String toString() {
        return "CustomFieldsConfig{" +
                "mapping=" + StringUtils.join(mapping) +
                '}';
    }

}
