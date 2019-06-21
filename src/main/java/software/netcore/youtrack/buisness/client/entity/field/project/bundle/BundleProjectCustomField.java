package software.netcore.youtrack.buisness.client.entity.field.project.bundle;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.bundle.Bundle;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public abstract class BundleProjectCustomField<T extends Bundle> extends ProjectCustomField {

    private T bundle;

    @Override
    public String toString() {
        return "BundleProjectCustomField{" +
                super.toString() +
                "bundle=" + bundle +
                '}';
    }

}
