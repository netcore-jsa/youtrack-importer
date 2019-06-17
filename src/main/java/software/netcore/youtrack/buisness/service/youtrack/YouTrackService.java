package software.netcore.youtrack.buisness.service.youtrack;

import lombok.RequiredArgsConstructor;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;
import software.netcore.youtrack.buisness.client.entity.CustomField;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.entity.User;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.entity.ConnectionInfo;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;

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
        return getProject(connectionInfo).isPresent();
    }

    public Collection<CustomField> getCustomFields(ConnectionInfo connectionInfo) throws InvalidHostnameException,
            HostUnreachableException, UnauthorizedException, NotFoundException {
        Optional<Project> optional = getProject(connectionInfo);
        if (!optional.isPresent()) {
            throw new NotFoundException("Project " + connectionInfo.getProjectName() + " not found");
        }
        Project project = optional.get();
        return restClient.getCustomFields(connectionInfo.getApiEndpoint(),
                connectionInfo.getServiceToken(), project.getId());
    }

    public Collection<User> getUsers(ConnectionInfo connectionInfo) throws InvalidHostnameException,
            HostUnreachableException, UnauthorizedException {
        return restClient.getUsers(connectionInfo.getApiEndpoint(), connectionInfo.getServiceToken());
    }

    private Optional<Project> getProject(ConnectionInfo connectionInfo) throws UnauthorizedException,
            HostUnreachableException, InvalidHostnameException {
        Collection<Project> projects = restClient.getProjects(connectionInfo.getApiEndpoint(),
                connectionInfo.getServiceToken());
        return projects.stream().filter(project ->
                Objects.equals(connectionInfo.getProjectName(), project.getName())).findFirst();
    }

}
