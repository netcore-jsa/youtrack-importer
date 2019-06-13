package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

/**
 * @since v. 1.0.0
 */
@Slf4j
@Route(value = WizardFlowView.NAVIGATION)
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public final class WizardFlowView extends VerticalLayout implements RouterLayout {

    static final String NAVIGATION = "";

    private final Div right = new Div();
    private final WizardStorage storage;
    private final WizardFlow wizardFlow;

    public WizardFlowView(WizardStorage storage, WizardFlow wizardFlow) {
        this.storage = storage;
        this.wizardFlow = wizardFlow;
        buildView();
    }

    private void buildView() {
        HorizontalLayout layout = new HorizontalLayout();
        VerticalLayout left = new VerticalLayout();

        int i = 1;
        for (WizardFlow.Step step : wizardFlow.getSteps()) {
            left.add(new Label(i + ".) " + step.getTitle()));
            i++;
        }

        wizardFlow.getSteps().forEach(step -> {

        });

        layout.add(left, right);
        add(layout);

        UI.getCurrent().navigate(CsvLoadView.NAVIGATION);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        right.removeAll();
        right.getElement().appendChild(content.getElement());
    }

}
