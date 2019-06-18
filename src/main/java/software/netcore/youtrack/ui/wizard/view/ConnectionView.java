package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.YouTrackConnectionConfig;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = ConnectionView.NAVIGATION, layout = WizardFlowView.class)
public class ConnectionView extends AbstractFlowStepView<YouTrackImporterStorage, YouTrackConnectionConfig> {

    public static final String NAVIGATION = "youtrack_connection";
    private final YouTrackService service;

    private Binder<YouTrackConnectionConfig> binder;
    private TextField url;
    private TextArea token;
    private TextField project;

    public ConnectionView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow);
        this.service = service;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    public boolean isValid() {
        BinderValidationStatus<YouTrackConnectionConfig> status = binder.validate();
        if (status.isOk()) {
            YouTrackConnectionConfig youTrackConnectionConfig = new YouTrackConnectionConfig();
            binder.writeBeanIfValid(youTrackConnectionConfig);
            boolean connectionInfoValid = false;
            try {
                connectionInfoValid = service.checkProjectAvailability(youTrackConnectionConfig);
                if (connectionInfoValid) {
                    Notification.show("YouTrack connection info is valid",
                            3000, Notification.Position.TOP_END);
                } else {
                    project.setErrorMessage("Project not found");
                    project.setInvalid(true);
                }
            } catch (UnauthorizedException e) {
                token.setErrorMessage("Unauthorized request. The token is most likely invalid");
                token.setInvalid(true);
            } catch (HostUnreachableException e) {
                url.setErrorMessage("Host is unreachable");
                url.setInvalid(true);
            } catch (InvalidHostnameException e) {
                url.setErrorMessage("Invalid hostname");
                url.setInvalid(true);
            }
            setConfig(connectionInfoValid ? youTrackConnectionConfig : null);
            return connectionInfoValid;
        }
        return false;
    }

    void buildView() {
        removeAll();
        setWidth("500px");
        add(new H3(getStep().getTitle()));

        url = new TextField("YouTrack API endpoint");
        url.setValueChangeMode(ValueChangeMode.EAGER);
        url.setWidthFull();
        token = new TextArea("Service token");
        token.setValueChangeMode(ValueChangeMode.EAGER);
        token.setWidthFull();
        project = new TextField("Project name");
        project.setValueChangeMode(ValueChangeMode.EAGER);
        project.setWidthFull();

        binder = new Binder<>(YouTrackConnectionConfig.class);
        binder.forField(url)
                .withValidator(new StringLengthValidator("API endpoint is required",
                        1, Integer.MAX_VALUE))
                .bind(YouTrackConnectionConfig::getApiEndpoint, YouTrackConnectionConfig::setApiEndpoint);
        binder.forField(token)
                .withValidator(new StringLengthValidator("Service token is required",
                        1, Integer.MAX_VALUE))
                .bind(YouTrackConnectionConfig::getServiceToken, YouTrackConnectionConfig::setServiceToken);
        binder.forField(project)
                .withValidator(new StringLengthValidator("Project name is required",
                        1, Integer.MAX_VALUE))
                .bind(YouTrackConnectionConfig::getProjectName, YouTrackConnectionConfig::setProjectName);
        YouTrackConnectionConfig connectionInfo = getStorage().getConnectionConfig();
        binder.readBean(connectionInfo == null ? new YouTrackConnectionConfig() : connectionInfo);

        add(url);
        add(token);
        add(project);
        add(new Button("Validate", event -> isValid()));
    }

}
