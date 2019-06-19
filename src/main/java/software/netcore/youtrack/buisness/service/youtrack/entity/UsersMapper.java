package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import software.netcore.youtrack.buisness.client.entity.User;

import java.util.*;

/**
 * Maps CSV users to YouTrack users
 *
 * @since v. 1.0.0
 */
@Getter
public class UsersMapper implements UniqueValuesMapper {

    /**
     * CSV users to YouTrack users mapping
     */
    private final Map<String, User> mapping = new HashMap<>();

    /**
     * CSV columns which rows values are supposed to be Jira users
     */
    private final Set<String> csvColumns = new HashSet<>();

    @Override
    public String toString() {
        return "UsersMapper{" +
                "mapping=" + StringUtils.join(mapping) +
                ", csvColumns=" + Arrays.toString(csvColumns.toArray()) +
                '}';
    }

}
