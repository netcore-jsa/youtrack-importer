package software.netcore.youtrack.buisness.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import software.netcore.youtrack.buisness.client.entity.Project;
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

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public YouTrackRestClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.client = new OkHttpClient.Builder().build();
    }

    public Collection<Project> getProjects(String apiEndpoint, String authToken) throws InvalidHostnameException,
            UnauthorizedException, HostUnreachableException {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(apiEndpoint + PROJECTS_PATH)
                    .headers(buildHeaders(authToken))
                    .build();
            Response response = client.newCall(request).execute();
            validateResponse(response);
            String stringResponse = response.body().string();
            return objectMapper.<List<Project>>readValue(stringResponse, new TypeReference<List<Project>>() {
            });
        } catch (UnknownHostException e) {
            log.warn("Failed to get projects. Invalid YouTrack Rest API endpoint");
            throw new InvalidHostnameException("Invalid YouTrack Rest API endpoint");
        } catch (IOException e) {
            log.warn("Failed to get projects. YouTrack Rest API endpoint is unreachable. Reason = '{}'", e.getMessage());
            throw new HostUnreachableException("YouTrack Rest API endpoint is unreachable");
        }
    }

    private void validateResponse(Response response) throws InvalidHostnameException, UnauthorizedException {
        switch (response.code()) {
            case 401:
                log.warn("Failed to get projects. Unauthorized request");
                throw new UnauthorizedException("Invalid YouTrack service token");
            case 404:
                log.warn("Failed to get projects. Invalid YouTrack REST API endpoint");
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
