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
public class BuildBundle extends BaseBundle {

    @Override
    public String toString() {
        return "BuildBundle{" +
                super.toString() +
                "}";
    }

}
