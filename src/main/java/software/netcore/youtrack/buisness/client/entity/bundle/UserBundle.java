package software.netcore.youtrack.buisness.client.entity.bundle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.User;
import software.netcore.youtrack.buisness.client.entity.UserGroup;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class UserBundle extends Bundle {

    private UserGroup[] groups;

    private User[] individuals;

    private User[] aggregatedUsers;

    @Override
    public String toString() {
        return "UserBundle{" +
                super.toString() +
                "}";
    }

}
