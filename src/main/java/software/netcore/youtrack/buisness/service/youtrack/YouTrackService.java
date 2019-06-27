package software.netcore.youtrack.buisness.service.youtrack;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;
import software.netcore.youtrack.buisness.client.entity.Issue;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.entity.bundle.BaseBundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.base.BaseBundleProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.user.User;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.MappingException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.*;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class YouTrackService {

    private final YouTrackRestClient restClient;

    public boolean checkProjectAvailability(@NonNull ConnectionConfig connectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException {
        return getProject(connectionConfig).isPresent();
    }

    public Collection<ProjectCustomField> getCustomFields(@NonNull ConnectionConfig connectionConfig)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException, NotFoundException {
        Optional<Project> optional = getProject(connectionConfig);
        if (!optional.isPresent()) {
            throw new NotFoundException("Project " + connectionConfig.getProjectName() + " not found");
        }
        Project project = optional.get();
        return restClient.getProjectCustomFields(connectionConfig.getApiEndpoint(),
                connectionConfig.getServiceToken(), project.getId());
    }

    public Collection<User> getUsers(@NonNull ConnectionConfig connectionConfig)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException {
        return restClient.getUsers(connectionConfig.getApiEndpoint(), connectionConfig.getServiceToken());
    }

    @Async
    public ListenableFuture<TranslatedIssues> createIssuesFromMapping(
            @NonNull ConnectionConfig connectionConfig,
            @NonNull CsvReadResult csvReadResult,
            @NonNull CustomFieldsMapping customFieldsMapping,
            @NonNull MandatoryFieldsMapping mandatoryFieldsMapping,
            @NonNull UsersMapping usersMapping,
            @NonNull EnumsMapping enumsMapping) {

        TranslatedIssues translatedIssues = new TranslatedIssues();
        Project project;
        try {
            Optional<Project> optional = getProject(connectionConfig);
            if (!optional.isPresent()) {
                return AsyncResult.forExecutionException(new NotFoundException("Project " +
                        connectionConfig.getProjectName() + " not found"));
            }
            project = optional.get();
        } catch (UnauthorizedException | HostUnreachableException | InvalidHostnameException e) {
            return AsyncResult.forExecutionException(e);
        }

        // Map columns names to theirs indices. CSV can contain multiple columns with the same name.
        // For instance, by Jira exported CSV has multiple 'Affects Version/s' if an issues
        Map<String, List<Integer>> indices = new HashMap<>();
        for (int i = 0; i < csvReadResult.getColumns().size(); i++) {
            indices.putIfAbsent(csvReadResult.getColumns().get(i), new ArrayList<>());
            List<Integer> columnIndices = indices.get(csvReadResult.getColumns().get(i));
            columnIndices.add(i);
        }

        for (List<String> row : csvReadResult.getRows()) {

            Issue issue = new Issue();
            issue.setProject(project);
            issue.setIdReadable(row.get(indices.get(mandatoryFieldsMapping.getIssueId())));
            issue.setSummary(row.get(indices.get(mandatoryFieldsMapping.getSummary())));
            issue.setDescription(row.get(indices.get(mandatoryFieldsMapping.getDescription())));
            // Translate issue reporter
            String csvReporter = row.get(indices.get(mandatoryFieldsMapping.getReporter()));
            User user = usersMapping.getMapping().get(csvReporter);
            if (Objects.isNull(user)) {
                return AsyncResult.forExecutionException(new MappingException("Missing mapping for CSV '" +
                        csvReporter + "' user"));
            }
            issue.setReporter(user);

            List<IssueCustomField> issueCustomFields = new ArrayList<>();
            for (BaseBundleProjectCustomField<? extends BaseBundle, ? extends BundleElement>
                    projectCustomField : enumsMapping.getEnumsMapping().keySet()) {

                Map<String, BundleElement> bundlesMapping = enumsMapping.getEnumsMapping().get(projectCustomField);

                String csvColumn = customFieldsMapping.getMapping().get(projectCustomField);
                if (!StringUtils.isEmpty(csvColumn)) {
                    String csvColumnValue = row.get(indices.get(csvColumn));
                    if (!StringUtils.isEmpty(csvColumnValue)) {
                        IssueCustomField issueCustomField = new IssueCustomField();
                        issueCustomField.setProjectCustomField(projectCustomField);
                        issueCustomField.setValue(bundlesMapping.get(csvColumnValue));
                        issueCustomFields.add(issueCustomField);
                    }
                }
            }
            if (!issueCustomFields.isEmpty()) {
                issue.setCustomFields(issueCustomFields.toArray(new IssueCustomField[]{}));
            }
            translatedIssues.getIssues().add(issue);
        }
        return AsyncResult.forValue(translatedIssues);
    }

    private Optional<Project> getProject(ConnectionConfig connectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException {
        Collection<Project> projects = restClient.getProjects(connectionConfig.getApiEndpoint(),
                connectionConfig.getServiceToken());
        return projects.stream().filter(project ->
                Objects.equals(connectionConfig.getProjectName(), project.getName())).findFirst();
    }

}
