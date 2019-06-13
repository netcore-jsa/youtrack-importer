package software.netcore.youtrack.ui.wizard.conf;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;

/**
 * @since v. 1.0.0
 */
@Getter
public class WizardFlow {

    @Singular
    private List<Step> steps;

    public Step getStep(String navigation) {
        return null;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    public static class Step {

        private String title;
        private String navigation;
        private String nextStepNavigation;
        private String previousStepNavigation;

    }

    public static class WizrdFlowBuilder {

    }

    public static class StepBuilder {

        private String title;
        private String navigation;

        public StepBuilder title(String title) {
            this.title = title;
            return this;
        }

        public StepBuilder navigation(String navigation) {
            this.navigation = navigation;
            return this;
        }

        private Step build() {
            Step step = new Step();
            step.setTitle(title);
            step.setNavigation(navigation);
            return step;
        }

    }


}
