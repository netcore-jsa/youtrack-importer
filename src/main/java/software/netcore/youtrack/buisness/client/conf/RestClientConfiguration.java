package software.netcore.youtrack.buisness.client.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.netcore.youtrack.buisness.client.YouTrackRestClient;

/**
 * @since v. 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private final ObjectMapper objectMapper;

    @Bean
    YouTrackRestClient youTrackRestClient() {
        return new YouTrackRestClient(objectMapper);
    }

}
