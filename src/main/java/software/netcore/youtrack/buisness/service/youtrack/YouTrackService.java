package software.netcore.youtrack.buisness.service.youtrack;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;
import software.netcore.youtrack.buisness.client.entity.Issue;
import software.netcore.youtrack.buisness.client.entity.IssueComment;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BuildBundleElement;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.SingleUserIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleBuildIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.UserProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.base.*;
import software.netcore.youtrack.buisness.client.entity.user.User;
import software.netcore.youtrack.buisness.client.exception.*;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.*;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class YouTrackService {

    private final YouTrackRestClient restClient;

    public boolean checkProjectAvailability(@NonNull ConnectionConfig connectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException, BadRequestException {
        return getProject(connectionConfig).isPresent();
    }

    public Collection<ProjectCustomField> getCustomFields(@NonNull ConnectionConfig connectionConfig)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException,
            NotFoundException, BadRequestException {
        Optional<Project> optional = getProject(connectionConfig);
        if (!optional.isPresent()) {
            throw new NotFoundException("Project " + connectionConfig.getProjectName() + " not found");
        }
        Project project = optional.get();
        return restClient.getProjectCustomFields(connectionConfig.getApiEndpoint(),
                connectionConfig.getServiceToken(), project.getId());
    }

    public Collection<User> getUsers(@NonNull ConnectionConfig connectionConfig)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException, BadRequestException {
        return restClient.getUsers(connectionConfig.getApiEndpoint(), connectionConfig.getServiceToken());
    }

    @Async
    public ListenableFuture<Void> importTranslatedIssues(@NonNull ConnectionConfig connectionConfig,
                                                         @NonNull TranslatedIssues translatedIssues) {
        try {
            for (Issue issue : translatedIssues.getIssues()) {
                restClient.createIssue(connectionConfig.getApiEndpoint(), connectionConfig.getServiceToken(), issue);
                List<IssueComment> comments = translatedIssues.getIssueComments().get(issue);
                if (Objects.nonNull(comments)) {
                    for (IssueComment comment : comments) {
                        restClient.createIssueComment(connectionConfig.getApiEndpoint(),
                                connectionConfig.getServiceToken(), comment);
                    }
                }
            }
            return AsyncResult.forValue(null);
        } catch (BadRequestException | InvalidHostnameException | UnauthorizedException | HostUnreachableException e) {
            return AsyncResult.forExecutionException(e);
        }
    }

    @Async
    public ListenableFuture<TranslatedIssues> translateIssuesMapping(
            @NonNull ConnectionConfig connectionConfig,
            @NonNull CsvReadResult csvReadResult,
            @NonNull CustomFieldsMapping customFieldsMapping,
            @NonNull MandatoryFieldsMapping mandatoryFieldsMapping,
            @NonNull UsersMapping usersMapping,
            @NonNull EnumsMapping enumsMapping) {

        TranslatedIssues translatedIssues = new TranslatedIssues();
        Project project;
        try {
            Optional<Project> optional = null;
            try {
                optional = getProject(connectionConfig);
            } catch (BadRequestException e) {
                e.printStackTrace();
            }
            if (!optional.isPresent()) {
                return AsyncResult.forExecutionException(new NotFoundException("Project " +
                        connectionConfig.getProjectName() + " not found"));
            }
            project = optional.get();
        } catch (UnauthorizedException | HostUnreachableException | InvalidHostnameException e) {
            return AsyncResult.forExecutionException(e);
        }

        Map<String, Integer> indices = new HashMap<>();
        List<Integer> commentColumnIndices = new ArrayList<>();
        for (int i = 0; i < csvReadResult.getColumns().size(); i++) {
            indices.put(csvReadResult.getColumns().get(i), i);
            if (Objects.equals(mandatoryFieldsMapping.getComments(), csvReadResult.getColumns().get(i))) {
                commentColumnIndices.add(i);
            }
        }
//        3/22/2019  2:14:00 PM
        DateFormat issueDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        DateFormat issueCommentDateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss aaa", Locale.getDefault());
        for (List<String> row : csvReadResult.getRows()) {

            Issue issue = new Issue();
            issue.setProject(project);
            issue.setIdReadable(row.get(indices.get(mandatoryFieldsMapping.getIssueId())));
            issue.setSummary(row.get(indices.get(mandatoryFieldsMapping.getSummary())));
            issue.setDescription(row.get(indices.get(mandatoryFieldsMapping.getDescription())));
            try {
                issue.setCreated(issueDateFormat.parse(row.get(indices.get(mandatoryFieldsMapping.getCreatedAt())))
                        .toInstant().getEpochSecond());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Translate issue reporter
            String csvReporter = row.get(indices.get(mandatoryFieldsMapping.getReporter()));
            User user = usersMapping.getMapping().get(csvReporter);
            if (Objects.isNull(user)) {
                return AsyncResult.forExecutionException(new MappingException("Missing mapping for CSV '" +
                        csvReporter + "' user"));
            }
            issue.setReporter(user);

            List<IssueCustomField> issueCustomFields = new ArrayList<>();

            for (ProjectCustomField projectCustomField : customFieldsMapping.getMapping().keySet()) {
                if (projectCustomField instanceof BaseBundleProjectCustomField) {
                    Map<String, BundleElement> bundlesMapping = enumsMapping.getEnumsMapping().get(projectCustomField);
                    String csvColumn = customFieldsMapping.getMapping().get(projectCustomField);
                    if (!StringUtils.isEmpty(csvColumn)) {
                        String csvColumnValue = row.get(indices.get(csvColumn));
                        if (!StringUtils.isEmpty(csvColumnValue)) {
                            if (projectCustomField instanceof BuildProjectCustomField) {
                                SingleBuildIssueCustomField issueCustomField = new SingleBuildIssueCustomField();
                                issueCustomField.setId(projectCustomField.getId());
                                issueCustomField.setProjectCustomField(projectCustomField);
                                BundleElement bundleElement = bundlesMapping.get(csvColumnValue);
                                if (bundleElement instanceof BuildBundleElement) {
                                    BuildBundleElement buildBundleElement = (BuildBundleElement) bundleElement;
                                    issueCustomField.setValue(buildBundleElement);
                                } else {
                                    return AsyncResult.forExecutionException(new MappingException("Bundle element type does not match project custom field"));
                                }
                                issueCustomFields.add(issueCustomField);
                            } else if (projectCustomField instanceof EnumProjectCustomField) {

                            } else if (projectCustomField instanceof VersionProjectCustomField) {

                            } else if (projectCustomField instanceof StateProjectCustomField) {
                            } else {
                                return AsyncResult.forExecutionException(new MappingException("Unexpected ProjectCustomField subtype"));
                            }
                        }
                    }
                } else if (projectCustomField instanceof UserProjectCustomField) {
                    SingleUserIssueCustomField issueCustomField = new SingleUserIssueCustomField();
                    issueCustomField.setId(projectCustomField.getId());
                    issueCustomField.setProjectCustomField(projectCustomField);
                    issueCustomField.setValue(usersMapping.getMapping().get(customFieldsMapping.getMapping().get(projectCustomField)));
                    issueCustomFields.add(issueCustomField);
                } else {
                    return AsyncResult.forExecutionException(new MappingException("Unexpected ProjectCustomField subtype"));
                }
            }


            if (!issueCustomFields.isEmpty()) {
                issue.setCustomFields(issueCustomFields.toArray(new IssueCustomField[]{}));
            }

            for (Integer commentColumnIndex : commentColumnIndices) {
                String commentString = row.get(commentColumnIndex);
                if (StringUtils.isEmpty(commentString)) {
                    continue;
                }
                translatedIssues.getIssueComments().put(issue, new ArrayList<>(commentColumnIndices.size()));
                // comment example
                // 07/Sep/18 1:14 PM;Johnny;Current imp don't throw NPE for unregistered views
                String[] values = commentString.split(";");
                if (values.length == 3) {
                    try {
                        IssueComment issueComment = new IssueComment();
                        if (!StringUtils.isEmpty(values[0])) {
                            Date date = issueCommentDateFormat.parse(values[0]);
                            issueComment.setCreated(date.toInstant().getEpochSecond());
                        }
                        issueComment.setAuthor(usersMapping.getMapping().get(values[1]));
                        issueComment.setText(values[2]);
                        issueComment.setIssue(issue);
                        translatedIssues.getIssueComments().get(issue).add(issueComment);
                    } catch (ParseException e) {
                        return AsyncResult.forExecutionException(e);
                    }
                } else if (values.length == 1) {
                    IssueComment issueComment = new IssueComment();
                    issueComment.setText(values[0]);
                    issueComment.setIssue(issue);
                    translatedIssues.getIssueComments().get(issue).add(issueComment);
                } else {
                    return AsyncResult.forExecutionException(
                            new InvalidFormatException("Comment column value unexpected format"));
                }
            }

            translatedIssues.getIssues().add(issue);
        }
        return AsyncResult.forValue(translatedIssues);
    }

    private Optional<Project> getProject(ConnectionConfig connectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException, BadRequestException {
        Collection<Project> projects = restClient.getProjects(connectionConfig.getApiEndpoint(),
                connectionConfig.getServiceToken());
        return projects.stream().filter(project ->
                Objects.equals(connectionConfig.getProjectName(), project.getName())).findFirst();
    }


}
