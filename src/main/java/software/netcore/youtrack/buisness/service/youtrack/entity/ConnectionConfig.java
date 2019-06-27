package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class ConnectionConfig {

    private String apiEndpoint;
    private String serviceToken;
    private String projectName;

    @Override
    public String toString() {
        return "ConnectionConfig{" +
                "apiEndpoint='" + apiEndpoint + '\'' +
                ", serviceToken='" + serviceToken + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }

}
