package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import software.netcore.youtrack.buisness.client.entity.CustomField;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps YouTrack custom fields to CSV columns
 *
 * @since v. 1.0.0
 */
@Getter
public class CustomFieldsConfig {

    private final Map<CustomField, String> mapping = new HashMap<>();

    public Collection<CustomField> getCustomFields() {
        return mapping.keySet();
    }

    @Override
    public String toString() {
        return "CustomFieldsConfig{" +
                "mapping=" + StringUtils.join(mapping) +
                '}';
    }

}
