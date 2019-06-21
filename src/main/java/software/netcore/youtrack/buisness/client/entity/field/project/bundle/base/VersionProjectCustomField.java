package software.netcore.youtrack.buisness.client.entity.field.project.bundle.base;

import software.netcore.youtrack.buisness.client.entity.bundle.VersionBundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.VersionBundleElement;

/**
 * @since v. 1.0.0
 */
public class VersionProjectCustomField extends BaseBundleProjectCustomField<VersionBundle, VersionBundleElement> {

    @Override
    public String toString() {
        return "VersionProjectCustomField{" +
                super.toString() +
                "}";
    }

}
