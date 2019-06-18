package software.netcore.youtrack.ui.wizard.conf;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.ui.wizard.view.FlowStepView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
@Getter
public class WizardFlow {

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Step> stepsMapping = new LinkedHashMap<>();

    @Setter
    @Getter
    private FlowStepView flowStep;

    public Step getStep(String navigation) {
        return stepsMapping.get(navigation);
    }

    public Collection<Step> getSteps() {
        return Collections.unmodifiableCollection(stepsMapping.values());
    }

    public Step getFirstStep() {
        return stepsMapping.values().iterator().next();
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    public static class Step {

        private String title;
        private String navigation;
        private String nextStepNavigation;
        private String previousStepNavigation;

    }

    @SuppressWarnings("WeakerAccess")
    public static class WizardFlowBuilder {

        private final List<StepBuilder> stepBuilders = new ArrayList<>();

        private WizardStorage wizardStorage;

        public WizardFlowBuilder() {
        }

        public WizardFlowBuilder storage(WizardStorage storage) {
            this.wizardStorage = storage;
            return this;
        }

        public StepBuilder step() {
            StepBuilder stepBuilder = new StepBuilder(this);
            stepBuilders.add(stepBuilder);
            return stepBuilder;
        }

        public WizardFlow build() {
            List<Step> steps = stepBuilders.stream().map(StepBuilder::buildStep).collect(Collectors.toList());
            for (int i = 0; i < steps.size(); i++) {
                if (i > 0) {
                    steps.get(i).setPreviousStepNavigation(steps.get(i - 1).getNavigation());
                }
                if (i < steps.size() - 1) {
                    steps.get(i).setNextStepNavigation(steps.get(i + 1).getNavigation());
                }
            }
            WizardFlow wizardFlow = new WizardFlow();
            steps.forEach(step -> wizardFlow.getStepsMapping().put(step.getNavigation(), step));
            wizardStorage.setConfigsOrder(wizardFlow.getStepsMapping().keySet().toArray(new String[]{}));
            return wizardFlow;
        }

    }

    @SuppressWarnings("WeakerAccess")
    public static class StepBuilder {

        private String title;
        private String navigation;

        private final WizardFlowBuilder wizardFlowBuilder;

        private StepBuilder(WizardFlowBuilder wizardFlowBuilder) {
            this.wizardFlowBuilder = wizardFlowBuilder;
        }

        public StepBuilder title(String title) {
            this.title = title;
            return this;
        }

        public StepBuilder navigation(String navigation) {
            this.navigation = navigation;
            return this;
        }

        public WizardFlow build() {
            return wizardFlowBuilder.build();
        }

        public WizardFlowBuilder and() {
            return this.wizardFlowBuilder;
        }


        private Step buildStep() {
            Step step = new Step();
            step.setTitle(title);
            step.setNavigation(navigation);
            return step;
        }

    }

}
