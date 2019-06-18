package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.util.StringUtils;

import java.util.Collection;

class AddCsvColumnsDialog extends Dialog {

    AddCsvColumnsDialog(Collection<String> csvColumns, AdditionListener<String> additionListener) {
        ComboBox<String> columnsBox = new ComboBox<>("CSV column");
        columnsBox.setItems(csvColumns);
        columnsBox.setWidthFull();
        add(columnsBox);

        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
        controlsLayout.add(new Button("Add", event -> {
            String value = columnsBox.getValue();
            if (StringUtils.isEmpty(value)) {
                columnsBox.setErrorMessage("CSV column is required");
                columnsBox.setInvalid(true);
                return;
            }
            additionListener.onAddition(value);
            setOpened(false);
        }));
        controlsLayout.setWidthFull();
        add(controlsLayout);
    }
}
