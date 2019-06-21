package software.netcore.youtrack.buisness.service.youtrack;

import lombok.RequiredArgsConstructor;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.entity.user.User;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.entity.YouTrackConnectionConfig;
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

    public boolean checkProjectAvailability(YouTrackConnectionConfig youTrackConnectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException {
        return getProject(youTrackConnectionConfig).isPresent();
    }

    public Collection<ProjectCustomField> getCustomFields(YouTrackConnectionConfig youTrackConnectionConfig)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException, NotFoundException {
        Optional<Project> optional = getProject(youTrackConnectionConfig);
        if (!optional.isPresent()) {
            throw new NotFoundException("Project " + youTrackConnectionConfig.getProjectName() + " not found");
        }
        Project project = optional.get();
        return restClient.getProjectCustomFields(youTrackConnectionConfig.getApiEndpoint(),
                youTrackConnectionConfig.getServiceToken(), project.getId());
    }

    public Collection<User> getUsers(YouTrackConnectionConfig youTrackConnectionConfig)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException {
        return restClient.getUsers(youTrackConnectionConfig.getApiEndpoint(), youTrackConnectionConfig.getServiceToken());
    }

    private Optional<Project> getProject(YouTrackConnectionConfig youTrackConnectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException {
        Collection<Project> projects = restClient.getProjects(youTrackConnectionConfig.getApiEndpoint(),
                youTrackConnectionConfig.getServiceToken());
        return projects.stream().filter(project ->
                Objects.equals(youTrackConnectionConfig.getProjectName(), project.getName())).findFirst();
    }

}
