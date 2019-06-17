package software.netcore.youtrack.ui.wizard.view;

/**
 * @since v. 1.0.0
 */
public interface FlowStepView {

    boolean isValid();

    boolean hasStoredConfiguration();

    String getNavigation();

    boolean hasPreviousStep();

    boolean hasNextStep();

    String getPreviousStepNavigation();

    String getNextStepNavigation();

}
