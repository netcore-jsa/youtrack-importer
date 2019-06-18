package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * @since v 1.0.0
 */
class CsvColumnLabel extends HorizontalLayout {

    CsvColumnLabel(String column, RemovalListener<String> removalListener) {
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        add(new Label(column));
        add(new Button(VaadinIcon.CLOSE.create(), event -> {
            getElement().removeFromParent();
            removalListener.onRemoval(column);
        }));
    }

}
