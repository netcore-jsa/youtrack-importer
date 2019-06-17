package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.User;

import java.util.Collection;

@Getter
@Setter
public class UsersMapping {

    private Collection<User> youTrackUsers;

}
