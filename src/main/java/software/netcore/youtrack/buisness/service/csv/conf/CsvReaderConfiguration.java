package software.netcore.youtrack.buisness.service.csv.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.netcore.youtrack.buisness.service.csv.CsvReader;

/**
 * @since v. 1.0.0
 */
@Configuration
public class CsvReaderConfiguration {

    @Bean
    CsvReader csvReader() {
        return new CsvReader();
    }

}
