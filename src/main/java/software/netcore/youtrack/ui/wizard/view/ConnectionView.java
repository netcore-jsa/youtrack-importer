package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = ConnectionView.NAVIGATION, layout = WizardFlowView.class)
public class ConnectionView extends AbstractWizardView {

    public static final String NAVIGATION = "youtrack_connection";

    public ConnectionView() {
        buildView();
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getNextWizardStepNavigation() {
        return null;
    }

    @Override
    public String getPreviousWizardStepNavigation() {
        return null;
    }

    private void buildView() {
        add(new Label("YouTrack connection"));
    }

}
