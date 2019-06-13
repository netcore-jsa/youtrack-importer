package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = CustomFieldsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class CustomFieldsMappingView extends VerticalLayout {

    public static final String NAVIGATION = "custom_fields_mapping";

    public CustomFieldsMappingView() {
        buildView();
    }

    private void buildView() {
        add(new Label("Custom fields mapping"));
    }

}
