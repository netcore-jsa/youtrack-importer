package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.netcore.youtrack.buisness.client.entity.CustomField;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.CustomFieldsMapping;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = CustomFieldsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class CustomFieldsMappingView extends AbstractFlowStepView {

    public static final String NAVIGATION = "custom_fields_mapping";

    private CustomFieldsMapping customFieldsMapping;
    private final Collection<CustomFieldMappingLayout> mappingLayouts = new ArrayList<>();
    private final YouTrackService service;

    public CustomFieldsMappingView(WizardStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow);
        this.service = service;
        buildView();
    }

    @Override
    public boolean hasStoredConfiguration() {
        return !Objects.isNull(getStorage().getCustomFieldsMapping());
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        for (CustomFieldMappingLayout mappingLayout : mappingLayouts) {
            valid = mappingLayout.isValid() && valid;
        }
        getStorage().setCustomFieldsMapping(valid ? customFieldsMapping : null);
        return valid;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    private void buildView() {
        add(new H3("Custom fields mapping: YouTrack -> CSV"));

        Div requiredFieldsLayout = new Div();
        Div optionalFieldsLayout = new Div();

        Label requiredLabel = new Label("Required");
        requiredLabel.getElement().getStyle().set("font-weight", "bold");
        requiredFieldsLayout.add(requiredLabel);
        requiredFieldsLayout.add(new Hr());
        Label optionalLabel = new Label("Optional");
        optionalLabel.getElement().getStyle().set("font-weight", "bold");
        optionalFieldsLayout.add(optionalLabel);
        optionalFieldsLayout.add(new Hr());

        add(requiredFieldsLayout);
        add(optionalFieldsLayout);

        customFieldsMapping = getCustomFieldsMapping();
        Collection<CustomField> customFields = getCustomFields();
        Collection<String> columns = getStorage().getCsvReadResult().getColumns();

        customFields.forEach(customField -> {
            CustomFieldMappingLayout layout = new CustomFieldMappingLayout(customField,
                    columns, customFieldsMapping);
            mappingLayouts.add(layout);
            if (customField.isCanBeEmpty()) {
                optionalFieldsLayout.add(layout);
            } else {
                requiredFieldsLayout.add(layout);
            }
        });
    }

    private CustomFieldsMapping getCustomFieldsMapping() {
        if (hasStoredConfiguration()) {
            return getStorage().getCustomFieldsMapping();
        } else {
            return new CustomFieldsMapping();
        }
    }

    private Collection<CustomField> getCustomFields() {
        if (hasStoredConfiguration()) {
            return getStorage().getCustomFieldsMapping().keySet();
        } else {
            try {
                return service.getCustomFields(getStorage().getConnectionInfo());
            } catch (InvalidHostnameException e) {
                e.printStackTrace();
            } catch (HostUnreachableException e) {
                e.printStackTrace();
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("'asdasdasd");
    }

    private static class CustomFieldMappingLayout extends HorizontalLayout {

        @Getter
        private final CustomField customField;

        private final ComboBox<String> columnsBox;

        CustomFieldMappingLayout(CustomField customField, Collection<String> columns, CustomFieldsMapping mapping) {
            this.customField = customField;
            mapping.put(customField, mapping.get(customField));

            Label customFieldLabel = new Label(customField.getField().getName());
            customFieldLabel.setWidth("200px");
            columnsBox = new ComboBox<>();
            columnsBox.setErrorMessage("The field mapping is required");
            columnsBox.setAllowCustomValue(false);
            columnsBox.setItems(columns);
            if (mapping.containsKey(customField)) {
                columnsBox.setValue(mapping.get(customField));
            }
            columnsBox.addValueChangeListener(event -> {
                String column = event.getValue();
                mapping.put(customField, column);
                validateSelection(event.getValue());
            });

            add(customFieldLabel);
            add(columnsBox);
        }

        boolean isValid() {
            if (customField.isCanBeEmpty()) {
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
