package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class ConnectionInfo {

    private String apiEndpoint;
    private String serviceToken;
    private String projectName;

}
