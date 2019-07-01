package software.netcore.youtrack.buisness.client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserGroup {

    private String name;

    private String ringId;

    private Long usersCount;

    private String icon;

    private Boolean allUsersGroup;

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
