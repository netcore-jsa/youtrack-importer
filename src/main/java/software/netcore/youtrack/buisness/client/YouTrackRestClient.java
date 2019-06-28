package software.netcore.youtrack.buisness.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import software.netcore.youtrack.buisness.client.entity.Issue;
import software.netcore.youtrack.buisness.client.entity.IssueComment;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.user.User;
import software.netcore.youtrack.buisness.client.exception.BadRequestException;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

/**
 * @since v. 1.0.0
 */
@Slf4j
public class YouTrackRestClient {

    private final static String MEDIA_TYPE_JSON = "application/json";

    private final static String PROJECTS_PATH = "/admin/projects?fields=id,name,shortName,description";
    private final static String USERS_PATH = "/admin/users?fields=login,fullName,email";
    private final static String PROJECT_CUSTOM_FIELDS_PATH = "/admin/projects/%s/customFields?fields=id,name," +
            "canBeEmpty,emptyFieldText,field(id,name,type),bundle(id,name,type,values(id,name,description,type))";
    private static final String ISSUES_PATH = "/issues";
    private static final String ISSUE_COMMENTS_PATH = "/issues";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public YouTrackRestClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.client = new OkHttpClient.Builder().build();
    }

    public Collection<Project> getProjects(@NonNull String apiEndpoint,
                                           @NonNull String authToken) throws InvalidHostnameException,
            UnauthorizedException, HostUnreachableException, BadRequestException {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(apiEndpoint + PROJECTS_PATH)
                    .headers(buildHeaders(authToken))
                    .build();
            Response response = client.newCall(request).execute();
            validateResponse(response);
            String stringResponse = response.body().string();
            Collection<Project> projects = objectMapper.<List<Project>>readValue(stringResponse,
                    new TypeReference<List<Project>>() {
                    });
            log.debug("[getProjects] Response body = '{}'",
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(projects));
            return projects;
        } catch (UnknownHostException e) {
            log.warn("Failed to get projects. Invalid YouTrack Rest API endpoint");
            throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        } catch (IOException e) {
            log.warn("Failed to get projects. YouTrack Rest API endpoint is unreachable. " +
                    "Reason = '{}'", e.getMessage());
            throw new HostUnreachableException("YouTrack Rest API endpoint is unreachable");
        }
    }

    public Collection<User> getUsers(@NonNull String apiEndpoint,
                                     @NonNull String authToken)
            throws UnauthorizedException, InvalidHostnameException, HostUnreachableException, BadRequestException {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(apiEndpoint + USERS_PATH)
                    .headers(buildHeaders(authToken))
                    .build();
            Response response = client.newCall(request).execute();
            validateResponse(response);
            String stringResponse = response.body().string();
            Collection<User> users = objectMapper.<List<User>>readValue(stringResponse,
                    new TypeReference<List<User>>() {
                    });
            log.debug("[getUsers] Response body = '{}'",
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(users));
            return users;
        } catch (UnknownHostException e) {
            log.warn("Failed to get users. Invalid YouTrack Rest API endpoint");
            throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        } catch (IOException e) {
            log.warn("Failed to get users. YouTrack Rest API endpoint is unreachable. " +
                    "Reason = '{}'", e.getMessage());
            throw new HostUnreachableException("YouTrack Rest API endpoint is unreachable");
        }
    }

    public Collection<ProjectCustomField> getProjectCustomFields(@NonNull String apiEndpoint,
                                                                 @NonNull String authToken,
                                                                 @NonNull String projectId)
            throws UnauthorizedException, InvalidHostnameException, HostUnreachableException, BadRequestException {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(apiEndpoint + String.format(PROJECT_CUSTOM_FIELDS_PATH, projectId))
                    .headers(buildHeaders(authToken))
                    .build();
            Response response = client.newCall(request).execute();
            validateResponse(response);
            String stringResponse = response.body().string();
            Collection<ProjectCustomField> customFields = objectMapper.<List<ProjectCustomField>>readValue(stringResponse,
                    new TypeReference<List<ProjectCustomField>>() {
                    });
            log.debug("[getProjectCustomFields] Response body = '{}'",
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(customFields));
            return customFields;
        } catch (UnknownHostException e) {
            log.warn("Failed to get project's custom fields. Invalid YouTrack Rest API endpoint");
            throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        } catch (IOException e) {
            log.warn("Failed to get project's custom fields. YouTrack Rest API endpoint is unreachable. " +
                    "Reason = '{}'", e.getMessage());
            throw new HostUnreachableException("YouTrack Rest API endpoint is unreachable");
        }
    }

    public void createIssue(@NonNull String apiEndpoint,
                            @NonNull String authToken,
                            @NonNull Issue issue)
            throws InvalidHostnameException, UnauthorizedException, HostUnreachableException, BadRequestException {
        try {
            String json = objectMapper.writeValueAsString(issue);
            doPost(json, apiEndpoint + ISSUE_COMMENTS_PATH, authToken);
        } catch (UnknownHostException e) {
            log.warn("Failed to create issue comment. Invalid YouTrack Rest API endpoint");
            throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        } catch (IOException e) {
            log.warn("Failed to create issue. YouTrack Rest API endpoint is unreachable. " +
                    "Reason = '{}'", e.getMessage());
            throw new HostUnreachableException("YouTrack Rest API endpoint is unreachable");
        }
    }

    public void createIssueComment(@NonNull String apiEndpoint,
                                   @NonNull String authToken,
                                   @NonNull IssueComment issueComment)
            throws InvalidHostnameException, HostUnreachableException, UnauthorizedException, BadRequestException {
        try {
            String json = objectMapper.writeValueAsString(issueComment);
            doPost(json, apiEndpoint + ISSUES_PATH, authToken);
        } catch (UnknownHostException e) {
            log.warn("Failed to create issue. Invalid YouTrack Rest API endpoint");
            throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        } catch (IOException e) {
            log.warn("Failed to create issue. YouTrack Rest API endpoint is unreachable. " +
                    "Reason = '{}'", e.getMessage());
            throw new HostUnreachableException("YouTrack Rest API endpoint is unreachable");
        }
    }

    private void doPost(String json, String url, String authToken)
            throws UnauthorizedException, InvalidHostnameException, IOException, BadRequestException {
        RequestBody body = RequestBody.create(MediaType.parse(MEDIA_TYPE_JSON), json);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .headers(buildHeaders(authToken))
                .build();
        Response response = client.newCall(request).execute();
        validateResponse(response);
    }

    private void validateResponse(Response response) throws InvalidHostnameException, UnauthorizedException, BadRequestException {
        switch (response.code()) {
            case 401:
                log.warn("YouTrack request failed. Unauthorized request");
                throw new UnauthorizedException("Invalid YouTrack service token");
            case 400:
                log.warn("Invalid YouTrack request");
                throw new BadRequestException(response.message());
            case 404:
                log.warn("YouTrack request failed. Invalid YouTrack REST API endpoint");
                throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        }
    }

    private Headers buildHeaders(String authToken) {
        return new Headers.Builder()
                .add("accept", MEDIA_TYPE_JSON)
                .add("content-type", MEDIA_TYPE_JSON)
                .add("authorization", "Bearer " + authToken)
                .build();
    }

}
