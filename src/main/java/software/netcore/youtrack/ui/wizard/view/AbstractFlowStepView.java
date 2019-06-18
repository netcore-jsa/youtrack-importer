package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import software.netcore.youtrack.ui.wizard.conf.HasStepConfig;
import software.netcore.youtrack.ui.wizard.conf.HasWizardStorage;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
abstract class AbstractFlowStepView<U extends WizardStorage, T> extends VerticalLayout implements FlowStepView,
        BeforeEnterObserver, BeforeLeaveObserver, HasWizardStorage<U>, HasStepConfig<T> {

    private final U storage;
    private final WizardFlow wizardFlow;

    AbstractFlowStepView(U storage, WizardFlow wizardFlow) {
        this.storage = storage;
        this.wizardFlow = wizardFlow;
    }

    abstract void buildView();

    public U getStorage() {
        return storage;
    }

    @Override
    public T getConfig() {
        return getStorage().getConfig(getNavigation());
    }

    @Override
    public void setConfig(T config) {
        getStorage().setConfig(getNavigation(), config);
    }

    @Override
    public boolean hasStoredConfig() {
        return Objects.nonNull(getConfig());
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
        if (hasStoredConfig()) {
            buildView();
        } else {
            String navigation = getStorage().getFirstInvalidConfigNavigation();
            if (Objects.equals(navigation, getNavigation())) {
                buildView();
            } else {
                event.forwardTo(navigation);
            }
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        wizardFlow.setFlowStep(null);
    }

    void invalidateFollowingSteps() {
        storage.invalidateFollowingConfigs(getNavigation());
    }

    WizardFlow.Step getStep() {
        return wizardFlow.getStep(getNavigation());
    }

}
