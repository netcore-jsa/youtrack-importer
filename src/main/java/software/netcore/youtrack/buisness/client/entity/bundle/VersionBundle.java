package software.netcore.youtrack.buisness.client.entity.bundle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class VersionBundle extends BaseBundle {

    @Override
    public String toString() {
        return "VersionBundle{" +
                super.toString() +
                "}";
    }

}
