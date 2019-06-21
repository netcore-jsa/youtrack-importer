package software.netcore.youtrack.buisness.client.entity.field.project.bundle.base;

import software.netcore.youtrack.buisness.client.entity.bundle.BuildBundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BuildBundleElement;

/**
 * @since v. 1.0.0
 */
public class BuildProjectCustomField extends BaseBundleProjectCustomField<BuildBundle, BuildBundleElement> {

    @Override
    public String toString() {
        return "BuildProjectCustomField{" +
                super.toString() +
                "}";
    }

}
