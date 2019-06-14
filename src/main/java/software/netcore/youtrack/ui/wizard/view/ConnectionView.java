package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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
import software.netcore.youtrack.buisness.service.youtrack.entity.ConnectionInfo;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = ConnectionView.NAVIGATION, layout = WizardFlowView.class)
public class ConnectionView extends AbstractFlowStepView {

    public static final String NAVIGATION = "youtrack_connection";
    private final YouTrackService youTrackService;

    private Binder<ConnectionInfo> binder;
    private Div validationLayout;
    private TextField url;
    private TextArea token;
    private TextField project;

    public ConnectionView(WizardStorage storage, WizardFlow wizardFlow, YouTrackService youTrackService) {
        super(storage, wizardFlow);
        this.youTrackService = youTrackService;
        buildView();
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    public boolean isValid() {
        BinderValidationStatus<ConnectionInfo> status = binder.validate();
        if (status.isOk()) {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            binder.writeBeanIfValid(connectionInfo);
            boolean connectionInfoValid = false;
            try {
                connectionInfoValid = youTrackService.checkProjectAvailability(connectionInfo);
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
            getStorage().setConnectionInfo(connectionInfoValid ? connectionInfo : null);
            return connectionInfoValid;
        }
        return false;
    }

    private void buildView() {
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

        binder = new Binder<>(ConnectionInfo.class);
        binder.forField(url)
                .withValidator(new StringLengthValidator("API endpoint is required",
                        1, Integer.MAX_VALUE))
                .bind(ConnectionInfo::getApiEndpoint, ConnectionInfo::setApiEndpoint);
        binder.forField(token)
                .withValidator(new StringLengthValidator("Service token is required",
                        1, Integer.MAX_VALUE))
                .bind(ConnectionInfo::getServiceToken, ConnectionInfo::setServiceToken);
        binder.forField(project)
                .withValidator(new StringLengthValidator("Project name is required",
                        1, Integer.MAX_VALUE))
                .bind(ConnectionInfo::getProjectName, ConnectionInfo::setProjectName);
        ConnectionInfo connectionInfo = getStorage().getConnectionInfo();
        binder.readBean(connectionInfo == null ? new ConnectionInfo() : connectionInfo);

        add(url);
        add(token);
        add(project);
        add(new Button("Validate", event -> isValid()));
    }

}
