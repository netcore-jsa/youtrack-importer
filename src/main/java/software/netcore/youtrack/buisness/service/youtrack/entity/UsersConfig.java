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
public class UsersConfig {

    /**
     * CSV users to YouTrack users mapping
     */
    private final Map<String, User> mapping = new HashMap<>();

    /**
     * Selected CSV columns which rows values are supposed to be Jira users
     */
    private final Set<String> selectedCsvColumns = new HashSet<>();

    @Override
    public String toString() {
        return "UsersConfig{" +
                "mapping=" + StringUtils.join(mapping) +
                ", selectedCsvColumns=" + Arrays.toString(selectedCsvColumns.toArray()) +
                '}';
    }

}
