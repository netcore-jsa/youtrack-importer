package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.service.youtrack.entity.MandatoryFieldsMapper;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.Collection;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = MandatoryFieldsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class MandatoryFieldsMappingView extends AbstractFlowStepView<YouTrackImporterStorage, MandatoryFieldsMapper> {

    public static final String NAVIGATION = "mandatory_fields_mapping";

    private final Binder<MandatoryFieldsMapper> binder = new Binder<>(MandatoryFieldsMapper.class);
    private MandatoryFieldsMapper mapper;

    MandatoryFieldsMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow) {
        super(storage, wizardFlow);
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    public boolean isValid() {
        BinderValidationStatus<MandatoryFieldsMapper> status = binder.validate();
        setConfig(status.isOk() ? mapper : null);
        return status.isOk();
    }

    @Override
    void buildView() {
        removeAll();
        setMargin(false);
        setPadding(false);

        add(new H3("Mandatory fields mapping: YouTrack -> CSV"));

        mapper = hasStoredConfig() ? getConfig() : new MandatoryFieldsMapper();
        Collection<String> csvColumns = getStorage().getCsvReadResult().getColumns();

        ComboBox<String> summary = buildComboBox(csvColumns);
        ComboBox<String> description = buildComboBox(csvColumns);
        ComboBox<String> comments = buildComboBox(csvColumns);
        ComboBox<String> reporter = buildComboBox(csvColumns);

        add(buildHorizontalLayout(buildCaptionLabel("Summary"), summary));
        add(buildHorizontalLayout(buildCaptionLabel("Description"), description));
        add(buildHorizontalLayout(buildCaptionLabel("Comments"), comments));
        add(buildHorizontalLayout(buildCaptionLabel("Reporter"), reporter));

        binder.forField(summary)
                .asRequired("Summary mapping is required")
                .bind(MandatoryFieldsMapper::getSummary, MandatoryFieldsMapper::setSummary);
        binder.forField(description)
                .asRequired("Description mapping is required")
                .bind(MandatoryFieldsMapper::getDescription, MandatoryFieldsMapper::setDescription);
        binder.forField(comments)
                .asRequired("Comment mapping is required")
                .bind(MandatoryFieldsMapper::getComments, MandatoryFieldsMapper::setComments);
        binder.forField(reporter)
                .asRequired("Reporter mapping is required")
                .bind(MandatoryFieldsMapper::getReporter, MandatoryFieldsMapper::setReporter);
        binder.setBean(mapper);
    }

    private HorizontalLayout buildHorizontalLayout(Component... components) {
        HorizontalLayout horizontalLayout = new HorizontalLayout(components);
        horizontalLayout.setWidthFull();
        return horizontalLayout;
    }

    private ComboBox<String> buildComboBox(Collection<String> values) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setRequired(true);
        comboBox.setItems(values);
        comboBox.setWidthFull();
        return comboBox;
    }

    private Label buildCaptionLabel(String caption) {
        Label label = new Label(caption);
        label.setWidth("200px");
        return label;
    }

}
