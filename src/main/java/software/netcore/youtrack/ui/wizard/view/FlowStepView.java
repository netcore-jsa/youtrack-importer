package software.netcore.youtrack.ui.wizard.view;

/**
 * @since v. 1.0.0
 */
public interface FlowStepView {

    boolean isValid();

    boolean hasStoredConfig();

    String getNavigation();

    boolean hasPreviousStep();

    boolean hasNextStep();

    String getPreviousStepNavigation();

    String getNextStepNavigation();

}
