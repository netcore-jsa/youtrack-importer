package software.netcore.youtrack.buisness.client.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class UserGroup {

    private String name;

    private String ringId;

    private Long usersCount;

    private String icon;

    private boolean allUsersGroup;

    private Project teamForProject;

    @Override
    public String toString() {
        return "UserGroup{" +
                "name='" + name + '\'' +
                ", ringId='" + ringId + '\'' +
                ", usersCount=" + usersCount +
                ", icon='" + icon + '\'' +
                ", allUsersGroup=" + allUsersGroup +
                ", teamForProject=" + teamForProject +
                '}';
    }

}
