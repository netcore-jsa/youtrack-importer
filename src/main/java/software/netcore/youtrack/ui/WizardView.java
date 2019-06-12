package software.netcore.youtrack.ui;

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
import software.netcore.youtrack.ui.wizard.ImportWizard;

/**
 * @since v. 1.0.0
 */
@Slf4j
@Route(value = WizardView.NAVIGATION)
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public final class WizardView extends VerticalLayout implements RouterLayout {

    static final String NAVIGATION = "";

    private final Div right = new Div();
    private final ImportWizard importWizard;

    public WizardView(ImportWizard importWizard) {
        this.importWizard = importWizard;
        buildView();
    }

    private void buildView() {
        HorizontalLayout layout = new HorizontalLayout();
        VerticalLayout left = new VerticalLayout();

        left.add(new Label("Step 1"));
        left.add(new Label("Step 2"));
        left.add(new Label("Step 3"));
        left.add(new Label("Step 4"));

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
