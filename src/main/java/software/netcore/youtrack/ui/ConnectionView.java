package software.netcore.youtrack.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = ConnectionView.NAVIGATION, layout = WizardView.class)
public class ConnectionView extends WizardStepView {

    public static final String NAVIGATION = "youtrack_connection";

    public ConnectionView() {
        buildView();
    }

    @Override
    String getNavigation() {
        return NAVIGATION;
    }

    private void buildView() {
        add(new Label("YouTrack connection"));
    }

}
