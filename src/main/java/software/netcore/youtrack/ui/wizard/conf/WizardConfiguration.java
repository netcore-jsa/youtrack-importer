package software.netcore.youtrack.ui.wizard.conf;

import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.netcore.youtrack.ui.wizard.view.ConnectionView;
import software.netcore.youtrack.ui.wizard.view.CsvLoadView;
import software.netcore.youtrack.ui.wizard.view.CustomFieldsMappingView;
import software.netcore.youtrack.ui.wizard.view.UsersMappingView;

/**
 * @since v. 1.0.0
 */
@Configuration
public class WizardConfiguration {

    @Bean
    @UIScope
    public WizardFlow wizardFlow() {
        //@formatter:off
        return new WizardFlow.WizardFlowBuilder()
                    .step()
                        .title("Load CSV file")
                        .navigation(CsvLoadView.NAVIGATION)
                .and()
                    .step()
                        .title("Set up YouTrack connection info")
                        .navigation(ConnectionView.NAVIGATION)
                .and()
                    .step()
                        .title("Map custom fields")
                        .navigation(CustomFieldsMappingView.NAVIGATION)
                .and()
                    .step()
                        .title("Map users")
                        .navigation(UsersMappingView.NAVIGATION)
                .build();
        //@formatter:on
    }

    @Bean
    @UIScope
    WizardStorage storage() {
        return new WizardStorage();
    }

}
