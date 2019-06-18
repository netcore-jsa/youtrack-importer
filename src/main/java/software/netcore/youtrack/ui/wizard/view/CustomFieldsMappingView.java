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
import software.netcore.youtrack.buisness.service.youtrack.entity.CustomFieldsConfig;
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
public class CustomFieldsMappingView extends AbstractFlowStepView<YouTrackImporterStorage, CustomFieldsConfig> {

    public static final String NAVIGATION = "custom_fields_mapping";

    private CustomFieldsConfig customFieldsConfig;
    private final Collection<CustomFieldMappingLayout> mappingLayouts = new ArrayList<>();
    private final YouTrackService service;

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
        setConfig(valid ? customFieldsConfig : null);
        return valid;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    void buildView() {
        removeAll();
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

        customFieldsConfig = getCustomFieldsConfig();
        Collection<CustomField> customFields = getCustomFields();
        Collection<String> columns = getStorage().getCsvReadResult().getColumns();

        customFields.forEach(customField -> {
            CustomFieldMappingLayout layout = new CustomFieldMappingLayout(customField,
                    columns, customFieldsConfig);
            mappingLayouts.add(layout);
            if (customField.isCanBeEmpty()) {
                optionalFieldsLayout.add(layout);
            } else {
                requiredFieldsLayout.add(layout);
            }
        });
    }

    private CustomFieldsConfig getCustomFieldsConfig() {
        return hasStoredConfig() ? getConfig() : new CustomFieldsConfig();
    }

    private Collection<CustomField> getCustomFields() {
        if (hasStoredConfig()) {
            return getCustomFieldsConfig().getMapping().keySet();
        } else {
            try {
                return service.getCustomFields(getStorage().getConnectionConfig());
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

        private final ComboBox<String> columnsBox = new ComboBox<>();

        @Getter
        private final CustomField customField;

        CustomFieldMappingLayout(CustomField customField, Collection<String> columns, CustomFieldsConfig cfc) {
            this.customField = customField;
            cfc.getMapping().put(customField, cfc.getMapping().get(customField));

            Label customFieldLabel = new Label(customField.getField().getName());
            customFieldLabel.setWidth("200px");
            columnsBox.setErrorMessage("The field mapping is required");
            columnsBox.setAllowCustomValue(false);
            columnsBox.setItems(columns);
            if (cfc.getMapping().containsKey(customField)) {
                columnsBox.setValue(cfc.getMapping().get(customField));
            }
            columnsBox.addValueChangeListener(event -> {
                String column = event.getValue();
                cfc.getMapping().put(customField, column);
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
