package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import software.netcore.youtrack.buisness.client.entity.bundle.BaseBundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.base.BaseBundleProjectCustomField;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps CSV enums to YouTrack enum fields
 *
 * @since v. 1.0.0
 */
@Getter
public class EnumsMapping {

    private final Map<String, Collection<String>> uniqueColumnValuesMapping = new HashMap<>();

    private final Map<BaseBundleProjectCustomField<? extends BaseBundle, ? extends BundleElement>,
            Map<String, BundleElement>> enumsMapping = new LinkedHashMap<>();

    @Override
    public String toString() {
        return "EnumsMapping{" +
                "uniqueColumnValuesMapping=" + StringUtils.join(uniqueColumnValuesMapping) +
                ", enumsMapping=" + StringUtils.join(uniqueColumnValuesMapping) +
                '}';
    }

}
