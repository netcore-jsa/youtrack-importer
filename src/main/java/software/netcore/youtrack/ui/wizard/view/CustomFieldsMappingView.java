package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;
import software.netcore.youtrack.buisness.client.exception.BadRequestException;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.CustomFieldsMapping;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = CustomFieldsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class CustomFieldsMappingView extends AbstractFlowStepView<YouTrackImporterStorage, CustomFieldsMapping> {

    public static final String NAVIGATION = "custom_fields_mapping";

    private final Collection<CustomFieldMappingLayout> mappingLayouts = new ArrayList<>();
    private final Div mappingFormContainer = new Div();
    private final YouTrackService service;

    private Collection<ProjectCustomField> customFields;
    private CustomFieldsMapping mapping;

    public CustomFieldsMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow);
        this.service = service;
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        for (CustomFieldMappingLayout mappingLayout : mappingLayouts) {
            valid = mappingLayout.isValid() && valid;
        }
        setConfig(valid ? mapping : null);
        return valid;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    void buildView() {
        removeAll();
        setMargin(false);
        setPadding(false);

        add(new H3("Custom fields mapping: YouTrack -> CSV"));
        add(mappingFormContainer);
        mappingFormContainer.setWidth("500px");

        mapping = hasStoredConfig() ? getConfig() : new CustomFieldsMapping();
        fetchCustomFieldsAndBuildMappingForm();
    }

    private void fetchCustomFieldsAndBuildMappingForm() {
        if (hasStoredConfig()) {
            customFields = mapping.getMapping().keySet();
            showMappingForm();
        } else {
            try {
                customFields = fetchCustomFields();
                showMappingForm();
            } catch (Exception e) {
                showEntitiesFetchFailure(e);
            }
        }
    }

    private void showMappingForm() {
        mappingFormContainer.removeAll();
        Div requiredFieldsLayout = new Div();
        Div optionalFieldsLayout = new Div();
        mappingFormContainer.add(requiredFieldsLayout);
        mappingFormContainer.add(optionalFieldsLayout);

        Label requiredLabel = new Label("Required");
        requiredLabel.getElement().getStyle().set("font-weight", "bold");
        requiredFieldsLayout.add(requiredLabel);
        requiredFieldsLayout.add(new Hr());
        Label optionalLabel = new Label("Optional");
        optionalLabel.getElement().getStyle().set("font-weight", "bold");
        optionalFieldsLayout.add(optionalLabel);
        optionalFieldsLayout.add(new Hr());

        Collection<String> columns = getStorage().getCsvReadResult().getUniqueColumns();
        customFields.forEach(customField -> {
            CustomFieldMappingLayout layout = new CustomFieldMappingLayout(customField, columns);
            mappingLayouts.add(layout);
            if (customField.getCanBeEmpty()) {
                optionalFieldsLayout.add(layout);
            } else {
                requiredFieldsLayout.add(layout);
            }
        });
    }

    private void showEntitiesFetchFailure(Exception exception) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setPadding(false);
        layout.add(new Label("Failed to fetch YouTrack custom fields"));
        layout.add(new Label("Reason = " + exception.getMessage()));
        layout.add(new Button("Retry", event -> fetchCustomFieldsAndBuildMappingForm()));
        mappingFormContainer.removeAll();
        mappingFormContainer.add(layout);
    }

    private Collection<ProjectCustomField> fetchCustomFields() throws HostUnreachableException, NotFoundException,
            UnauthorizedException, InvalidHostnameException, BadRequestException {
        return service.getCustomFields(getStorage().getConnectionConfig());
    }

    private class CustomFieldMappingLayout extends HorizontalLayout {

        private final ComboBox<String> columnsBox = new ComboBox<>();

        @Getter
        private final ProjectCustomField customField;

        CustomFieldMappingLayout(ProjectCustomField projectCustomField, Collection<String> columns) {
            this.customField = projectCustomField;
            mapping.getMapping().put(projectCustomField, mapping.getMapping().get(projectCustomField));

            Label customFieldLabel = new Label(projectCustomField.getCustomField().getName());
            customFieldLabel.setWidth("200px");
            columnsBox.setErrorMessage("The field mapping is required");
            columnsBox.setAllowCustomValue(false);
            columnsBox.setItems(columns);
            if (mapping.getMapping().containsKey(projectCustomField)) {
                columnsBox.setValue(mapping.getMapping().get(projectCustomField));
            }
            columnsBox.addValueChangeListener(event -> {
                String column = event.getValue();
                mapping.getMapping().put(projectCustomField, column);
                validateSelection(event.getValue());
            });

            add(customFieldLabel);
            add(columnsBox);
        }

        private boolean isValid() {
            if (customField.getCanBeEmpty()) {
                return true;
            }
            return validateSelection();
        }

        private boolean validateSelection() {
            return validateSelection(null);
        }

        private boolean validateSelection(String value) {
            boolean empty = StringUtils.isEmpty(value == null ? columnsBox.getValue() : value);
            columnsBox.setInvalid(empty);
            return !empty;
        }

    }

}
