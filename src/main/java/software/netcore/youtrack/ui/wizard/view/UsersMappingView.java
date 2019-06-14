package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = UsersMappingView.NAVIGATION, layout = WizardFlowView.class)
public class UsersMappingView extends AbstractFlowStepView {

    public static final String NAVIGATION = "users_mapping";

    public UsersMappingView(WizardStorage storage, WizardFlow wizardFlow) {
        super(storage, wizardFlow);
        buildView();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    private void buildView() {
        add(new Label("Users mapping"));
    }

}
