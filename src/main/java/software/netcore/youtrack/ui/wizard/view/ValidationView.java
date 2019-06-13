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
@Route(value = ValidationView.NAVIGATION, layout = WizardFlowView.class)
public class ValidationView extends VerticalLayout {

    static final String NAVIGATION = "validation";

    private final WizardStorage importWizard;

    public ValidationView(WizardStorage importWizard) {
        this.importWizard = importWizard;
        buildView();
    }

    private void buildView() {
        add(new Label("Validation view"));
    }

}
