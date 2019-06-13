package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = UsersMappingView.NAVIGATION, layout = WizardFlowView.class)
public class UsersMappingView extends VerticalLayout {

    public static final String NAVIGATION = "users_mapping";

    private final WizardStorage importWizard;

    public UsersMappingView(WizardStorage importWizard) {
        this.importWizard = importWizard;
        buildView();
    }

    private void buildView() {
        add(new Label("Users mapping"));
    }

}
