package software.netcore.youtrack.buisness.client.entity.bundle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.UserGroup;
import software.netcore.youtrack.buisness.client.entity.user.User;

import java.util.Arrays;

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
                ", groups=" + Arrays.toString(groups) +
                ", individuals=" + Arrays.toString(individuals) +
                ", aggregatedUsers=" + Arrays.toString(aggregatedUsers) +
                '}';
    }

}
