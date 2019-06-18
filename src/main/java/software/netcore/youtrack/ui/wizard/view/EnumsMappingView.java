package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.client.entity.CustomField;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.EnumsConfig;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@PageTitle("YouTrack importer")
@Route(value = EnumsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class EnumsMappingView extends AbstractFlowStepView<YouTrackImporterStorage, EnumsConfig> {

    public static final String NAVIGATION = "enums_mapping";

    private final Div selectedCsvColumnsLayout = new Div();
    private final Div enumsMappingsLayout = new Div();
    private final YouTrackService youTrackService;
    private Collection<CustomField> enumFields;
    private EnumsConfig enumsConfig;

    public EnumsMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService youTrackService) {
        super(storage, wizardFlow);
        this.youTrackService = youTrackService;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    void buildView() {
        removeAll();
        enumsConfig = hasStoredConfig() ? getConfig() : new EnumsConfig();

        add(new H3("Enums mapping: CSV -> YouTrack"));

        enumFields = getStorage().getCustomFieldsConfig().getCustomFields()
                .stream().filter(customField ->
                        customField.getType().toLowerCase().contains("enum")).collect(Collectors.toList());
        AddCsvColumnsDialog dialog = new AddCsvColumnsDialog(getStorage().getCsvReadResult().getColumns(),
                csvUser -> addCsvColumn(csvUser, true));
        Button addColumnsBtn = new Button("Add column", VaadinIcon.PLUS.create(),
                event -> dialog.setOpened(true));
        add(addColumnsBtn);
        add(selectedCsvColumnsLayout);
        add(new Hr());
        add(enumsMappingsLayout);

        // load existing mappings if exist
        if (hasStoredConfig()) {
            getConfig().getSelectedCsvColumns()
                    .forEach(column -> addCsvColumn(column, false));
            updateMappingLayouts();
        }
    }

    private void addCsvColumn(String csvColumn, boolean updateMappingLayouts) {
        enumsConfig.getSelectedCsvColumns().add(csvColumn);
        selectedCsvColumnsLayout.add(new CsvColumnLabel(csvColumn, listener -> {
            enumsConfig.getSelectedCsvColumns().remove(csvColumn);
            if (updateMappingLayouts) {
                updateMappingLayouts();
            }
        }));
        if (updateMappingLayouts) {
            updateMappingLayouts();
        }
    }

    private void updateMappingLayouts() {

    }

    private static class EnumMappingLayout extends HorizontalLayout {

        private final ComboBox<CustomField> customFieldBox = new ComboBox<>();
        private final EnumsConfig enumsConfig;
        private final String csvEnum;

        public EnumMappingLayout(String csvEnum, Collection<CustomField> customFields, EnumsConfig enumsConfig) {
            this.csvEnum = csvEnum;
            this.enumsConfig = enumsConfig;

            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            Label csvColumnLabel = new Label(csvEnum);
            csvColumnLabel.setWidth("200px");

            customFieldBox.setItems(customFields);
            customFieldBox.setItemLabelGenerator(CustomField::getType);
            customFieldBox.setErrorMessage("Enum mapping is required");
            customFieldBox.setAllowCustomValue(false);
            customFieldBox.addValueChangeListener(event -> validateSelection(event.getValue()));

            add(csvColumnLabel);
            add(customFieldBox);
        }

        boolean isValid() {
            return validateSelection();
        }

        private boolean validateSelection() {
            return validateSelection(customFieldBox.getValue());
        }

        private boolean validateSelection(CustomField field) {
            boolean isNull = Objects.isNull(field);
            customFieldBox.setInvalid(isNull);
            enumsConfig.getMapping().put(csvEnum, isNull ? null : field);
            return !isNull;
        }

    }

}
