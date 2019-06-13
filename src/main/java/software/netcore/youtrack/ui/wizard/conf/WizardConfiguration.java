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
        return WizardFlow.builder()
                .step(WizardFlow.Step.builder()
                        .title("Load CSV file")
                        .navigation(CsvLoadView.NAVIGATION)
                        .build())
                .step(WizardFlow.Step.builder()
                        .title("Set up YouTrack connection info")
                        .navigation(ConnectionView.NAVIGATION)
                        .build())
                .step(WizardFlow.Step.builder()
                        .title("Map custom fields")
                        .navigation(CustomFieldsMappingView.NAVIGATION)
                        .build())
                .step(WizardFlow.Step.builder()
                        .title("Map users")
                        .navigation(UsersMappingView.NAVIGATION)
                        .build())
                .build();
    }

    @Bean
    @UIScope
    WizardStorage storage() {
        return new WizardStorage();
    }

}
