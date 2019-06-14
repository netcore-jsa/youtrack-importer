package software.netcore.youtrack.buisness.service.youtrack;

import lombok.RequiredArgsConstructor;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.entity.ConnectionInfo;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class YouTrackService {

    private final YouTrackRestClient restClient;

    public boolean checkProjectAvailability(ConnectionInfo connectionInfo) throws UnauthorizedException,
            HostUnreachableException, InvalidHostnameException {
        Collection<Project> projects = restClient.getProjects(connectionInfo.getApiEndpoint(),
                connectionInfo.getServiceToken());
        Optional<Project> optional = projects.stream().filter(project ->
                Objects.equals(connectionInfo.getProjectName(), project.getName())).findFirst();
        return optional.isPresent();
    }

}
