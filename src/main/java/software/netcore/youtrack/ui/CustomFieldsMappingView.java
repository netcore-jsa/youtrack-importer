package software.netcore.youtrack.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.ui.wizard.ImportWizard;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = CustomFieldsMappingView.NAVIGATION, layout = WizardView.class)
public class CustomFieldsMappingView extends VerticalLayout {

    static final String NAVIGATION = "custom_fields_mapping";

    private final ImportWizard importWizard;

    public CustomFieldsMappingView(ImportWizard importWizard) {
        this.importWizard = importWizard;
        buildView();
    }

    private void buildView() {
        add(new Label("Custom fields mapping"));
    }

}
