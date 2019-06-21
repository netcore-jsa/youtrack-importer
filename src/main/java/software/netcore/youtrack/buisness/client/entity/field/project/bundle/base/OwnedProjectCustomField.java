package software.netcore.youtrack.buisness.client.entity.field.project.bundle.base;

import software.netcore.youtrack.buisness.client.entity.bundle.OwnedBundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.OwnedBundleElement;

/**
 * @since v. 1.0.0
 */
public class OwnedProjectCustomField extends BaseBundleProjectCustomField<OwnedBundle, OwnedBundleElement> {

    @Override
    public String toString() {
        return "OwnedProjectCustomField{" +
                super.toString() +
                "}";
    }

}
