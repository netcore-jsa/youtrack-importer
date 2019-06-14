package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import lombok.Getter;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
abstract class AbstractFlowStepView extends VerticalLayout implements FlowStepView,
        BeforeEnterObserver, BeforeLeaveObserver {

    @Getter
    private final WizardStorage storage;

    @Getter
    private final WizardFlow wizardFlow;

    AbstractFlowStepView(WizardStorage storage, WizardFlow wizardFlow) {
        this.storage = storage;
        this.wizardFlow = wizardFlow;
    }

    WizardFlow.Step getStep() {
        return wizardFlow.getStep(getNavigation());
    }

    @Override
    public boolean hasNextStep() {
        return Objects.nonNull(getNextStepNavigation());
    }

    @Override
    public boolean hasPreviousStep() {
        return Objects.nonNull(getPreviousStepNavigation());
    }

    @Override
    public String getNextStepNavigation() {
        return getStep().getNextStepNavigation();
    }

    @Override
    public String getPreviousStepNavigation() {
        return getStep().getPreviousStepNavigation();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        wizardFlow.setFlowStep(this);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        wizardFlow.setFlowStep(null);
    }

}
