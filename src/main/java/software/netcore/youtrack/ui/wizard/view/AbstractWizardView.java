package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

/**
 * @since v. 1.0.0
 */
public abstract class AbstractWizardView extends VerticalLayout {

    @Getter
    private WizardStorage storage;

    @Autowired
    private void setStorage(WizardStorage storage) {
        this.storage = storage;
    }

    @Getter
    private WizardFlow wizardFlow;

    @Autowired
    private void setWizardFlow(WizardFlow wizardFlow) {
        this.wizardFlow = wizardFlow;
    }

    public WizardFlow.Step getStep(){
        return wizardFlow.getStep(getNavigation());
    }

    public abstract String getNavigation();

    public abstract boolean isValid();

    public abstract String getNextWizardStepNavigation();

    public abstract String getPreviousWizardStepNavigation();

    public void navigateToNext() {

    }

    public void navigateToPrevios() {

    }

}
