package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.client.entity.CustomField;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.EnumsMapper;
import software.netcore.youtrack.buisness.service.youtrack.exception.NotFoundException;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.Collection;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = EnumsMappingView.NAVIGATION, layout = WizardFlowView.class)
public class EnumsMappingView extends AbstractCsvUniqueValueMappingView<EnumsMapper, CustomField> {

    public static final String NAVIGATION = "enums_mapping";

    private final Div selectedCsvColumnsLayout = new Div();
    private final Div enumsMappingsLayout = new Div();
    private final YouTrackService youTrackService;
    private Collection<CustomField> enumFields;
    private EnumsMapper enumsMapper;

    public EnumsMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService youTrackService) {
        super(storage, wizardFlow, CustomField::getType);
        this.youTrackService = youTrackService;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    String getViewTitle() {
        return "Enums mapping: CSV -> YouTrack";
    }

    @Override
    String getAdditionButtonCaption() {
        return "Add column";
    }

    @Override
    EnumsMapper getEmptyMapper() {
        return new EnumsMapper();
    }

    @Override
    Collection<CustomField> fetchYouTrackEntities() throws UnauthorizedException,
            HostUnreachableException, InvalidHostnameException, NotFoundException {
        return youTrackService.getCustomFields(getStorage().getConnectionConfig());
    }

}
