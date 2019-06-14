package software.netcore.youtrack.buisness.service.youtrack.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;

/**
 * @since v. 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class YouTrackServiceConfiguration {

    private final YouTrackRestClient restClient;

    @Bean
    YouTrackService youTrackService() {
        return new YouTrackService(restClient);
    }

}
