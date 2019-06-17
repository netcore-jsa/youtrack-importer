package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

import java.util.Objects;

@Slf4j
@PageTitle("YouTrack importer")
@Route(value = EnumsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class EnumsMappingView extends AbstractFlowStepView {

    public static final String NAVIGATION = "enums_mapping";

    public EnumsMappingView(WizardStorage storage, WizardFlow wizardFlow) {
        super(storage, wizardFlow);
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean hasStoredConfiguration() {
        return Objects.nonNull(getStorage().getEnumsMapping());
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

}
