package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.client.entity.bundle.BaseBundle;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.base.BaseBundleProjectCustomField;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.CustomFieldsMapping;
import software.netcore.youtrack.buisness.service.youtrack.entity.EnumsMapping;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = EnumsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class EnumsMappingView extends AbstractFlowStepView<YouTrackImporterStorage, EnumsMapping> {

    public static final String NAVIGATION = "enums_mapping";

    private final List<MappingLayout> mappingLayouts = new ArrayList<>();
    private EnumsMapping mapping;

    EnumsMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow) {
        super(storage, wizardFlow);
    }

    @Override
    public boolean isValid() {
        boolean isValid = true;
        for (MappingLayout mappingLayout : mappingLayouts) {
            isValid = mappingLayout.isValid() && isValid;
        }
        setConfig(isValid ? mapping : null);
        return isValid;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    void buildView() {
        removeAll();
        setMargin(false);
        setPadding(false);

        add(new H3("Enums mapping: CSV -> YouTrack"));
        mapping = hasStoredConfig() ? getConfig() : new EnumsMapping();
        prepareMappingAndShowMappingForm();
    }

    private void prepareMappingAndShowMappingForm() {
        if (!hasStoredConfig()) {

            CustomFieldsMapping customFieldsMapping = getStorage().getCustomFieldsConfig();
            CsvReadResult csvReadResult = getStorage().getCsvReadResult();

            for (ProjectCustomField projectCustomField : customFieldsMapping.getMapping().keySet()) {
                if (projectCustomField instanceof BaseBundleProjectCustomField) {

                    String csvColumn = customFieldsMapping.getMapping().get(projectCustomField);
                    Collection<String> uniqueColumnValues = csvReadResult.getUniqueValuesFromColumn(csvColumn);
                    mapping.getUniqueColumnValuesMapping().put(csvColumn, uniqueColumnValues);

                    BaseBundleProjectCustomField<? extends BaseBundle, ? extends BundleElement>
                            baseBundleProjectCustomField = (BaseBundleProjectCustomField) projectCustomField;

                    Map<String, BundleElement> bundleElementsMapping = new LinkedHashMap<>();
                    for (String csvColumnValue : mapping.getUniqueColumnValuesMapping().get(csvColumn)) {
                        bundleElementsMapping.put(csvColumnValue, null);
                    }

                    mapping.getEnumsMapping().put(baseBundleProjectCustomField, bundleElementsMapping);
                }
            }
        }

        showMappingForm();
    }

    private void showMappingForm() {
        for (BaseBundleProjectCustomField<? extends BaseBundle, ? extends BundleElement> baseBundleProjectCustomField
                : mapping.getEnumsMapping().keySet()) {
            String csvColumn = getStorage().getCustomFieldsConfig().getMapping().get(baseBundleProjectCustomField);
            if (Objects.nonNull(csvColumn)) {
                MappingLayout mappingLayout = new MappingLayout(baseBundleProjectCustomField, csvColumn);
                mappingLayouts.add(mappingLayout);
                add(mappingLayout);
            }
        }
    }

    private class MappingLayout extends VerticalLayout {

        private final List<ComboBox<BundleElement>> bundleBoxes = new ArrayList<>();

        MappingLayout(BaseBundleProjectCustomField<? extends BaseBundle, ? extends BundleElement> customField,
                      String csvColumn) {
            add(new H3(csvColumn + " -> " + customField.getCustomField().getName()));

            if (mapping.getUniqueColumnValuesMapping().get(csvColumn).size() == 0) {
                add(new Label("Nothing to map"));
            } else {
                for (String csvColumnValue : mapping.getUniqueColumnValuesMapping().get(csvColumn)) {

                    Label csvColumnValueLabel = new Label(csvColumnValue);
                    csvColumnValueLabel.setWidth("200px");
                    ComboBox<BundleElement> bundleBox = buildComboBox(csvColumnValue, customField);

                    Map<String, BundleElement> csvColumnValueToBundleElementMap = mapping.getEnumsMapping().get(customField);
                    if (Objects.nonNull(csvColumnValueToBundleElementMap.get(csvColumnValue))) {
                        bundleBox.setValue(csvColumnValueToBundleElementMap.get(csvColumnValue));
                    } else {
                        // try to pre-map matching enums
                        for (BundleElement bundleElement : customField.getBundle().getValues()) {
                            if (Objects.equals(bundleElement.getName(), csvColumnValue)) {
                                csvColumnValueToBundleElementMap.put(csvColumnValue, bundleElement);
                                bundleBox.setValue(bundleElement);
                                break;
                            }
                        }
                    }

                    HorizontalLayout row = new HorizontalLayout();
                    row.add(csvColumnValueLabel, bundleBox);
                    add(row);
                }
            }
        }

        private ComboBox<BundleElement> buildComboBox(String csvColumnValue,
                                                      BaseBundleProjectCustomField<? extends BaseBundle, ? extends BundleElement> customField) {
            ComboBox<BundleElement> bundleBox = new ComboBox<>();
            bundleBox.setItems(Arrays.asList(customField.getBundle().getValues()));
            bundleBox.setItemLabelGenerator(BundleElement::getName);
            bundleBox.setErrorMessage("Mapping is required");
            bundleBox.setAllowCustomValue(false);
            bundleBox.addValueChangeListener(event -> {
                bundleBox.setInvalid(Objects.isNull(event.getValue()));
                mapping.getEnumsMapping().get(customField).put(csvColumnValue, event.getValue());
            });
            bundleBoxes.add(bundleBox);
            return bundleBox;
        }

        boolean isValid() {
            boolean isValid = true;
            for (ComboBox<BundleElement> bundleBox : bundleBoxes) {
                if (Objects.isNull(bundleBox.getValue())) {
                    bundleBox.setInvalid(true);
                    isValid = false;
                }
            }
            return isValid;
        }

    }

}
