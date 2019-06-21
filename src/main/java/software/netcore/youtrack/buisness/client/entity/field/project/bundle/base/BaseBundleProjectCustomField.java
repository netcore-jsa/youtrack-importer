package software.netcore.youtrack.buisness.client.entity.field.project.bundle.base;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.bundle.Bundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.BundleProjectCustomField;

import java.util.Arrays;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public abstract class BaseBundleProjectCustomField<T extends Bundle, U extends BundleElement>
        extends BundleProjectCustomField<T> {

    private U[] defaultValues;

    @Override
    public String toString() {
        return "BaseBundleProjectCustomField{" +
                super.toString() +
                "defaultValues=" + Arrays.toString(defaultValues) +
                '}';
    }

}
