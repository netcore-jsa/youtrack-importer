package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.client.entity.user.User;
import software.netcore.youtrack.buisness.client.exception.BadRequestException;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.UsersMapping;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.Collection;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = UsersMappingView.NAVIGATION, layout = WizardFlowView.class)
public class UsersMappingView extends AbstractCsvUniqueValueMappingView<UsersMapping, User> {

    public static final String NAVIGATION = "users_mapping";

    private final YouTrackService service;

    public UsersMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow, User::getLogin);
        this.service = service;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    String getViewTitle() {
        return "Users mapping: CSV -> YouTrack";
    }

    @Override
    String getAdditionButtonCaption() {
        return "Add column";
    }

    @Override
    UsersMapping getEmptyMapper() {
        return new UsersMapping();
    }

    @Override
    Collection<User> fetchYouTrackEntities() throws UnauthorizedException,
            HostUnreachableException, InvalidHostnameException, BadRequestException {
        return service.getUsers(getStorage().getConnectionConfig());
    }

}
