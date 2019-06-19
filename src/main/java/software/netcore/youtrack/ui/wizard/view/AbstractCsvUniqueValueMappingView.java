package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.util.StringUtils;
import software.netcore.youtrack.buisness.service.youtrack.entity.UniqueValuesMapper;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.*;

/**
 * @param <T>
 * @since v. 1.0.0
 */
public abstract class AbstractCsvUniqueValueMappingView<T extends UniqueValuesMapper, U>
        extends AbstractFlowStepView<YouTrackImporterStorage, T> {

    @FunctionalInterface
    protected interface ValueExtractor<S, T> {

        T extractValue(S source);

    }

    private final ValueExtractor<U, String> valueExtractor;
    private final Div csvColumnsLayout = new Div();
    private final Div mappingsLayout = new Div();

    private T mapper;

    AbstractCsvUniqueValueMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow,
                                      ValueExtractor<U, String> valueExtractor) {
        super(storage, wizardFlow);
        this.valueExtractor = valueExtractor;
    }

    abstract String getViewTitle();

    abstract String getAdditionButtonCaption();

    abstract T getEmptyMapper();

    @Override
    void buildView() {
        removeAll();
        add(new H3(getViewTitle()));

        mapper = hasStoredConfig() ? getConfig() : getEmptyMapper();
        AddCsvColumnsDialog dialog = new AddCsvColumnsDialog(getStorage().getCsvReadResult().getColumns(),
                csvUser -> addCsvColumn(csvUser, true));
        Button addColumnsBtn = new Button(getAdditionButtonCaption(), VaadinIcon.PLUS.create(),
                event -> dialog.setOpened(true));
        add(addColumnsBtn);
        add(csvColumnsLayout);
        add(new Hr());
        add(mappingsLayout);
    }

    private void addCsvColumn(String csvColumn, boolean updateMappingLayouts) {
        mapper.getCsvColumns().add(csvColumn);
        csvColumnsLayout.add(new CsvColumnLabel(csvColumn, listener -> {
            mapper.getCsvColumns().remove(csvColumn);
            if (updateMappingLayouts) {
                updateMappingLayouts();
            }
        }));
        if (updateMappingLayouts) {
            updateMappingLayouts();
        }
    }

    Collection<String> getValuesFromColumns(Collection<String> selectedColumns) {
        Collection<String> values = new HashSet<>();
        List<Integer> columnsIndexes = new ArrayList<>(selectedColumns.size());
        // determine columns indexes
        List<String> csvColumns = getStorage().getCsvReadResult().getColumns();
        for (int i = 0; i < csvColumns.size(); i++) {
            for (String selectedColumn : selectedColumns) {
                if (Objects.equals(csvColumns.get(i), selectedColumn)) {
                    columnsIndexes.add(i);
                    break;
                }
            }
        }

        // read values from columns
        List<List<String>> rows = getStorage().getCsvReadResult().getRows();
        rows.forEach(row -> columnsIndexes.forEach(index -> {
            String value = row.get(index);
            if (!StringUtils.isEmpty(value)) {
                values.add(value);
            }
        }));
        return values;
    }

    private class MappingLayout extends HorizontalLayout {

        private final ComboBox<U> youtrackEntityBox = new ComboBox<>();
        private final String csvUniqueValue;

        public MappingLayout(String csvUniqueValue, Collection<U> youtrackEntities) {
            this.csvUniqueValue = csvUniqueValue;

            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            Label csvValueLabel = new Label(csvUniqueValue);
            csvValueLabel.setWidth("200px");

            youtrackEntityBox.setItems(youtrackEntities);
            youtrackEntityBox.setItemLabelGenerator(valueExtractor::extractValue);
            youtrackEntityBox.setErrorMessage("User mapping is required");
            youtrackEntityBox.setAllowCustomValue(false);
            youtrackEntityBox.addValueChangeListener(event -> validateSelection(event.getValue()));
        }


        boolean isValid() {
            return validateSelection();
        }

        private boolean validateSelection() {
            return validateSelection(youtrackEntityBox.getValue());
        }

        private boolean validateSelection(U value) {
            boolean isNull = Objects.isNull(value);
            youtrackEntityBox.setInvalid(isNull);
            usersConfig.getMapping().put(csvUniqueValue, isNull ? null : value);
            return !isNull;
        }
    }


}
