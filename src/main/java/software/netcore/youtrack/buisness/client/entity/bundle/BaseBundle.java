package software.netcore.youtrack.buisness.client.entity.bundle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;

import java.util.Arrays;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseBundle extends Bundle {

    private BundleElement[] values;

    @Override
    public String toString() {
        return "BaseBundle{" +
                super.toString() +
                ",values=" + Arrays.toString(values) +
                '}';
    }

}
