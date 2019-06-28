package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import org.springframework.util.StringUtils;
import software.netcore.youtrack.buisness.client.exception.BadRequestException;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.entity.CsvColumnValuesMapping;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.*;

/**
 * Maps CSV unique columns values to YouTrack entity
 *
 * @param <T>
 * @since v. 1.0.0
 */
abstract class AbstractCsvUniqueValueMappingView<T extends CsvColumnValuesMapping<U>, U>
        extends AbstractFlowStepView<YouTrackImporterStorage, T> {

    @FunctionalInterface
    protected interface ValueExtractor<S, T> {

        T extractValue(S source);

    }

    private final Map<String, MappingLayout> uniqueValueToMappingLayout = new HashMap<>();
    private final ValueExtractor<U, String> valueExtractor;
    private final Div mappingFormContainer = new Div();
    private final Div csvColumnButtonsLayout = new Div();
    private final Div mappingsLayout = new Div();

    private Collection<U> youTrackEntities;
    private T mapper;

    AbstractCsvUniqueValueMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow,
                                      ValueExtractor<U, String> valueExtractor) {
        super(storage, wizardFlow);
        this.valueExtractor = valueExtractor;
    }

    abstract String getViewTitle();

    abstract String getAdditionButtonCaption();

    abstract T getEmptyMapper();

    abstract Collection<U> fetchYouTrackEntities() throws UnauthorizedException,
            HostUnreachableException, InvalidHostnameException, NotFoundException, BadRequestException;

    @Override
    public boolean isValid() {
        if (uniqueValueToMappingLayout.isEmpty()) {
            setConfig(null);
            return false;
        }
        boolean isValid = true;
        for (MappingLayout value : uniqueValueToMappingLayout.values()) {
            isValid = value.isValid() && isValid;
        }
        setConfig(isValid ? mapper : null);
        return isValid;
    }

    @Override
    void buildView() {
        removeAll();
        setMargin(false);
        setPadding(false);

        add(new H3(getViewTitle()));
        add(mappingFormContainer);
        mappingFormContainer.setWidth("500px");

        mapper = hasStoredConfig() ? getConfig() : getEmptyMapper();
        fetchEntitiesAndShowMappingForm();
    }

    private void fetchEntitiesAndShowMappingForm() {
        try {
            youTrackEntities = fetchYouTrackEntities();
            showMappingForm();
        } catch (Exception e) {
            showEntitiesFetchFailure(e);
        }
    }

    private void showMappingForm() {
        mappingFormContainer.removeAll();
        mappingFormContainer.setWidthFull();
        AddCsvColumnDialog dialog = new AddCsvColumnDialog(getStorage().getCsvReadResult().getColumns(),
                csvUser -> addCsvColumn(csvUser, true));
        Button addColumnsBtn = new Button(getAdditionButtonCaption(), VaadinIcon.PLUS.create(),
                event -> dialog.setOpened(true));
        mappingFormContainer.add(addColumnsBtn);
        mappingFormContainer.add(csvColumnButtonsLayout);
        mappingFormContainer.add(new Hr());
        mappingFormContainer.add(mappingsLayout);

        if (hasStoredConfig()) {
            mapper.getCsvColumns()
                    .forEach(column -> addCsvColumn(column, false));
            updateMappingLayouts();
        }
    }

    private void showEntitiesFetchFailure(Exception exception) {
        mappingFormContainer.removeAll();
        mappingFormContainer.add(new Label("Failed to fetch YouTrack entities"));
        mappingFormContainer.add(new Label("Reason = " + exception.getMessage()));
        mappingFormContainer.add(new Button("Retry", event -> fetchEntitiesAndShowMappingForm()));
    }

    private void addCsvColumn(String csvColumn, boolean updateMappingLayouts) {
        boolean noPresent = csvColumnButtonsLayout.getChildren().noneMatch(component -> {
            CsvColumnButton btn = (CsvColumnButton) component;
            return Objects.equals(btn.getColumn(), csvColumn);
        });
        if (noPresent) {
            mapper.getCsvColumns().add(csvColumn);
            csvColumnButtonsLayout.add(new CsvColumnButton(csvColumn, listener -> {
                mapper.getCsvColumns().remove(csvColumn);
                if (updateMappingLayouts) {
                    updateMappingLayouts();
                }
            }));
            if (updateMappingLayouts) {
                updateMappingLayouts();
            }
        }
    }

    private void updateMappingLayouts() {
        Collection<String> csvUniqueValues = getStorage().getCsvReadResult()
                .getUniqueValuesFromColumns(mapper.getCsvColumns());
        Collection<String> toRemove = new HashSet<>();
        Collection<String> toAdd = new HashSet<>();

        for (String mappedUser : uniqueValueToMappingLayout.keySet()) {
            boolean missing = true;
            for (String csvUser : csvUniqueValues) {
                if (Objects.equals(csvUser, mappedUser)) {
                    missing = false;
                    break;
                }
            }
            if (missing) {
                toRemove.add(mappedUser);
            }
        }

        for (String csvUser : csvUniqueValues) {
            if (!uniqueValueToMappingLayout.containsKey(csvUser)) {
                toAdd.add(csvUser);
            }
        }

        toRemove.forEach(csvUser -> {
            if (uniqueValueToMappingLayout.containsKey(csvUser)) {
                MappingLayout mappingLayout = uniqueValueToMappingLayout.get(csvUser);
                mappingLayout.getElement().removeFromParent();
                uniqueValueToMappingLayout.remove(csvUser);
            }
        });
        toAdd.forEach(csvUser -> {
            if (!uniqueValueToMappingLayout.containsKey(csvUser)) {
                MappingLayout userMappingLayout = new MappingLayout(csvUser, youTrackEntities);
                uniqueValueToMappingLayout.put(csvUser, userMappingLayout);
                mappingsLayout.add(userMappingLayout);
            }
        });
    }

    private class MappingLayout extends HorizontalLayout {

        private final ComboBox<U> youtrackEntityBox = new ComboBox<>();
        private final String csvUniqueValue;

        MappingLayout(String csvUniqueValue, Collection<U> youtrackEntities) {
            this.csvUniqueValue = csvUniqueValue;

            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            Label csvValueLabel = new Label(csvUniqueValue);
            csvValueLabel.setWidth("200px");

            youtrackEntityBox.setItems(youtrackEntities);
            youtrackEntityBox.setItemLabelGenerator(valueExtractor::extractValue);
            youtrackEntityBox.setErrorMessage("Mapping is required");
            youtrackEntityBox.setAllowCustomValue(false);
            youtrackEntityBox.addValueChangeListener(event -> validateSelection(event.getValue()));
            youtrackEntityBox.setWidthFull();
            if (mapper.getMapping().containsKey(csvUniqueValue)) {
                U value = mapper.getMapping().get(csvUniqueValue);
                youtrackEntityBox.setValue(value);
            }

            setWidthFull();
            add(csvValueLabel);
            add(youtrackEntityBox);
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
            mapper.getMapping().put(csvUniqueValue, isNull ? null : value);
            return !isNull;
        }
    }

    private class CsvColumnButton extends Button {

        @Getter
        private String column;

        CsvColumnButton(String column, RemovalListener<String> removalListener) {
            super(column, VaadinIcon.CLOSE.create(), event -> {
                event.getSource().getElement().removeFromParent();
                removalListener.onRemoval(column);
            });
            this.column = column;
            getElement().getStyle().set("margin", "5px");
        }
    }

    private class AddCsvColumnDialog extends Dialog {

        AddCsvColumnDialog(Collection<String> csvColumns, AdditionListener<String> additionListener) {
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

}
