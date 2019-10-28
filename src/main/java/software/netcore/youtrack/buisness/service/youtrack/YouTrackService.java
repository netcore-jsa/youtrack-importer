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
import software.netcore.youtrack.buisness.client.entity.bundle.element.*;
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.SingleUserIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleBuildIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleEnumIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleVersionIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.StateIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.UserProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.base.*;
import software.netcore.youtrack.buisness.client.entity.user.User;
import software.netcore.youtrack.buisness.client.exception.*;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.*;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class YouTrackService {

    private static final List<DateFormat> DATE_FORMATS = new ArrayList<>();

    static {
        DATE_FORMATS.add(new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault()));
        DATE_FORMATS.add(new SimpleDateFormat("MM/dd/yy hh:mm:ss aaa", Locale.getDefault()));
        DATE_FORMATS.add(new SimpleDateFormat("dd/MMM/yyyy hh:mm", Locale.getDefault()));
        DATE_FORMATS.add(new SimpleDateFormat("dd/MMM/yyyy hh:mm aaa", Locale.getDefault()));
    }

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
                Issue createdIssue = restClient.createIssue(connectionConfig.getApiEndpoint(),
                        connectionConfig.getServiceToken(), issue);

                List<IssueComment> comments = translatedIssues.getIssueComments().get(issue);
                if (Objects.nonNull(comments)) {
                    for (IssueComment comment : comments) {
                        issue.setIdReadable(createdIssue.getIdReadable());
                        comment.setIssue(issue);
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
            Optional<Project> optional = getProject(connectionConfig);
            if (!optional.isPresent()) {
                return AsyncResult.forExecutionException(new NotFoundException("Project " +
                        connectionConfig.getProjectName() + " not found"));
            }
            project = optional.get();
        } catch (UnauthorizedException | HostUnreachableException | InvalidHostnameException | BadRequestException e) {
            return AsyncResult.forExecutionException(e);
        }

        Map<String, Integer> indices = new HashMap<>();
        List<Integer> commentColumnIndices = new ArrayList<>();
        for (int i = 0; i < csvReadResult.getColumns().size(); i++) {
            indices.put(csvReadResult.getColumns().get(i), i);
            if (mandatoryFieldsMapping.getComments() != null &&
                    Objects.equals(mandatoryFieldsMapping.getComments(), csvReadResult.getColumns().get(i))) {
                commentColumnIndices.add(i);
            }
        }
        try {
            for (List<String> row : csvReadResult.getRows()) {
                Issue issue = new Issue();
                issue.setProject(project);
                issue.setIdReadable(row.get(indices.get(mandatoryFieldsMapping.getIssueId())));
                issue.setSummary(row.get(indices.get(mandatoryFieldsMapping.getSummary())));
                issue.setDescription(row.get(indices.get(mandatoryFieldsMapping.getDescription())));
//                issue.setCreated(translateDateString(row.get(indices.get(mandatoryFieldsMapping.getCreatedAt()))));
                String csvReporter = row.get(indices.get(mandatoryFieldsMapping.getReporter()));
                User reporter = usersMapping.getMapping().get(csvReporter);
                if (Objects.isNull(reporter)) {
                    return AsyncResult.forExecutionException(new MappingException("Missing mapping for CSV '" +
                            csvReporter + "' user"));
                }
                issue.setReporter(reporter);

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
                                    issueCustomField.setName(projectCustomField.getCustomField().getName());
                                    BundleElement bundleElement = bundlesMapping.get(csvColumnValue);
                                    if (bundleElement instanceof BuildBundleElement) {
                                        BuildBundleElement buildBundleElement = (BuildBundleElement) bundleElement;
                                        issueCustomField.setValue(buildBundleElement);
                                    } else {
                                        return AsyncResult.forExecutionException(new MappingException("Bundle " +
                                                "element type does not match project custom field"));
                                    }
                                    issueCustomFields.add(issueCustomField);
                                } else if (projectCustomField instanceof EnumProjectCustomField) {
                                    SingleEnumIssueCustomField issueCustomField = new SingleEnumIssueCustomField();
                                    issueCustomField.setId(projectCustomField.getId());
                                    issueCustomField.setProjectCustomField(projectCustomField);
                                    issueCustomField.setName(projectCustomField.getCustomField().getName());
                                    BundleElement bundleElement = bundlesMapping.get(csvColumnValue);
                                    if (bundleElement instanceof EnumBundleElement) {
                                        EnumBundleElement enumBundleElement = (EnumBundleElement) bundleElement;
                                        issueCustomField.setValue(enumBundleElement);
                                    } else {
                                        return AsyncResult.forExecutionException(new MappingException("Bundle element " +
                                                "type does not match project custom field"));
                                    }
                                    issueCustomFields.add(issueCustomField);
                                } else if (projectCustomField instanceof VersionProjectCustomField) {
                                    SingleVersionIssueCustomField issueCustomField = new SingleVersionIssueCustomField();
                                    issueCustomField.setId(projectCustomField.getId());
                                    issueCustomField.setProjectCustomField(projectCustomField);
                                    issueCustomField.setName(projectCustomField.getCustomField().getName());
                                    BundleElement bundleElement = bundlesMapping.get(csvColumnValue);
                                    if (bundleElement instanceof VersionBundleElement) {
                                        VersionBundleElement versionBundleElement = (VersionBundleElement) bundleElement;
                                        issueCustomField.setValue(versionBundleElement);
                                    } else {
                                        return AsyncResult.forExecutionException(new MappingException("Bundle element " +
                                                "type does not match project custom field"));
                                    }
                                    issueCustomFields.add(issueCustomField);
                                } else if (projectCustomField instanceof StateProjectCustomField) {
                                    StateIssueCustomField issueCustomField = new StateIssueCustomField();
                                    issueCustomField.setId(projectCustomField.getId());
                                    issueCustomField.setProjectCustomField(projectCustomField);
                                    issueCustomField.setName(projectCustomField.getCustomField().getName());
                                    BundleElement bundleElement = bundlesMapping.get(csvColumnValue);
                                    if (bundleElement instanceof StateBundleElement) {
                                        StateBundleElement stateBundleElement = (StateBundleElement) bundleElement;
                                        issueCustomField.setValue(stateBundleElement);
                                    } else {
                                        return AsyncResult.forExecutionException(new MappingException("Bundle element " +
                                                "type does not match project custom field"));
                                    }
                                    issueCustomFields.add(issueCustomField);
                                } else {
                                    return AsyncResult.forExecutionException(new MappingException("Unexpected " +
                                            "ProjectCustomField subtype"));
                                }
                            }
                        }
                    } else if (projectCustomField instanceof UserProjectCustomField) {
                        SingleUserIssueCustomField issueCustomField = new SingleUserIssueCustomField();
                        issueCustomField.setId(projectCustomField.getId());
                        issueCustomField.setProjectCustomField(projectCustomField);
                        issueCustomField.setName(projectCustomField.getCustomField().getName());

                        String csvColumn = customFieldsMapping.getMapping().get(projectCustomField);
                        String csvColumnValue = row.get(indices.get(csvColumn));
                        User user = usersMapping.getMapping().get(csvColumnValue);

                        issueCustomField.setValue(user);
                        issueCustomFields.add(issueCustomField);
                    } else {
                        return AsyncResult.forExecutionException(
                                new MappingException("Unexpected ProjectCustomField subtype"));
                    }
                }

                issue.setCustomFields(issueCustomFields.toArray(new IssueCustomField[]{}));
                for (Integer commentColumnIndex : commentColumnIndices) {
                    String commentString = row.get(commentColumnIndex);
                    if (StringUtils.isEmpty(commentString)) {
                        continue;
                    }
                    translatedIssues.getIssueComments().put(issue, new ArrayList<>(commentColumnIndices.size()));
                    String[] values = commentString.split(";");
                    if (values.length == 3) {
                        IssueComment issueComment = new IssueComment();
                        if (!StringUtils.isEmpty(values[0])) {
                            issueComment.setCreated(translateDateString(values[0]));
                        }
                        issueComment.setAuthor(usersMapping.getMapping().get(values[1]));
                        issueComment.setText(values[2]);
                        translatedIssues.getIssueComments().get(issue).add(issueComment);
                    } else if (values.length == 1) {
                        IssueComment issueComment = new IssueComment();
                        issueComment.setText(values[0]);
                        translatedIssues.getIssueComments().get(issue).add(issueComment);
                    } else {
                        return AsyncResult.forExecutionException(
                                new InvalidFormatException("Comment column value unexpected format"));
                    }
                }
                translatedIssues.getIssues().add(issue);
            }
        } catch (MappingException e) {
            return AsyncResult.forExecutionException(e);
        }

        String issueNumberPrefix = getIssueNumberPrefix(translatedIssues.getIssues().iterator().next());
        int highestIssueNumber = 0;
        Set<Integer> issueNumbers = new HashSet<>();
        for (Issue issue : translatedIssues.getIssues()) {
            int number = getIssueNumber(issue);
            issueNumbers.add(number);
            if (number > highestIssueNumber) {
                highestIssueNumber = number;
            }
        }
        for (int i = 1; i < highestIssueNumber; i++) {
            if (!issueNumbers.contains(i)) {
                Issue issue = new Issue();
                issue.setIdReadable(issueNumberPrefix + "-" + i);
                issue.setSummary("Dummy issue");
                issue.setDescription("Dummy description");
                issue.setProject(project);
                translatedIssues.getIssues().add(issue);
                translatedIssues.getDummyIssues().add(issue);
            }
        }

        translatedIssues.getIssues().sort((o1, o2) -> {
            int n1 = getIssueNumber(o1);
            int n2 = getIssueNumber(o2);
            return Integer.compare(n1, n2);
        });
        return AsyncResult.forValue(translatedIssues);
    }

    private int getIssueNumber(Issue issue) {
        return Integer.parseInt(issue.getIdReadable().split("-")[1]);
    }

    private String getIssueNumberPrefix(Issue issue) {
        return issue.getIdReadable().split("-")[0];
    }

    private Long translateDateString(String dateString) throws MappingException {
        for (DateFormat dateFormat : DATE_FORMATS) {
            try {
                return dateFormat.parse(dateString).toInstant().getEpochSecond();
            } catch (Exception ignored) {
            }
        }
        throw new MappingException("Unsupported date string format");
    }

    private Optional<Project> getProject(ConnectionConfig connectionConfig)
            throws UnauthorizedException, HostUnreachableException, InvalidHostnameException, BadRequestException {
        Collection<Project> projects = restClient.getProjects(connectionConfig.getApiEndpoint(),
                connectionConfig.getServiceToken());
        return projects.stream().filter(project ->
                Objects.equals(connectionConfig.getProjectName(), project.getName())).findFirst();
    }


}
