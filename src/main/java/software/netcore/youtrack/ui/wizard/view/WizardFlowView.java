package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@Route(value = WizardFlowView.NAVIGATION)
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public final class WizardFlowView extends VerticalLayout implements RouterLayout, BeforeEnterObserver,
        AfterNavigationObserver {

    static final String NAVIGATION = "";

    private final WizardFlow wizardFlow;

    private final Div contentContainer = new Div();
    private Button next;
    private Button previous;

    public WizardFlowView(WizardFlow wizardFlow) {
        this.wizardFlow = wizardFlow;
        buildView();
    }

    private void buildView() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        VerticalLayout left = new VerticalLayout();
        left.setWidth(null);
        left.add(new H3("YouTrack importer"));

        int i = 1;
        for (WizardFlow.Step step : wizardFlow.getSteps()) {
            left.add(new Label(i + ".) " + step.getTitle()));
            i++;
        }

        next = new Button("Next", event -> navigateToNext());
        previous = new Button("Previous", event -> navigateToPrevious());
        HorizontalLayout navigationLayout = new HorizontalLayout(previous, next);
        VerticalLayout right = new VerticalLayout(contentContainer);
        right.setWidth(null);
        right.add(new Hr());
        right.add(navigationLayout);
        right.setHorizontalComponentAlignment(Alignment.END, navigationLayout);
        layout.add(left, right);
        add(layout);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contentContainer.getElement().removeAllChildren();
        contentContainer.getElement().appendChild(content.getElement());
    }

    private void navigateToNext() {
        FlowStepView flowStep = wizardFlow.getFlowStep();
        if (Objects.nonNull(flowStep) && flowStep.isValid()) {
            UI.getCurrent().navigate(flowStep.getNextStepNavigation());
        }
    }

    private void navigateToPrevious() {
        FlowStepView flowStep = wizardFlow.getFlowStep();
         if (Objects.nonNull(flowStep)) {
            UI.getCurrent().navigate(flowStep.getPreviousStepNavigation());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getPath().equals(NAVIGATION) && Objects.isNull(wizardFlow.getFlowStep())) {
            String navigation = wizardFlow.getFirstStep().getNavigation();
            event.forwardTo(navigation);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        next.setEnabled(wizardFlow.getFlowStep().hasNextStep());
        previous.setEnabled(wizardFlow.getFlowStep().hasPreviousStep());
    }

}
