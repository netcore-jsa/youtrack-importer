package software.netcore.youtrack.buisness.client.entity.bundle.element;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class VersionBundleElement extends BundleElement {

    @Override
    public String toString() {
        return "VersionBundleElement{" +
                super.toString() +
                "}";
    }

}
