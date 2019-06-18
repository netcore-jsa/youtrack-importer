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
import org.springframework.util.StringUtils;
import software.netcore.youtrack.buisness.client.entity.User;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.UsersConfig;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = UsersMappingView.NAVIGATION, layout = WizardFlowView.class)
public class UsersMappingView extends AbstractFlowStepView<YouTrackImporterStorage, UsersConfig> {

    public static final String NAVIGATION = "users_mapping";

    private final Map<String, UserMappingLayout> userToMappingLayout = new HashMap<>();
    private final Div selectedCsvColumnsLayout = new Div();
    private final Div usersMappingsLayout = new Div();
    private final YouTrackService service;
    private Collection<User> youTrackUsers;
    private UsersConfig usersConfig;

    public UsersMappingView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow);
        this.service = service;
    }

    @Override
    public boolean isValid() {
        boolean isValid = true;
        for (UserMappingLayout value : userToMappingLayout.values()) {
            isValid = value.isValid() && isValid;
        }
        setConfig(isValid ? usersConfig : null);
        return isValid;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    void buildView() {
        removeAll();
        usersConfig = hasStoredConfig() ? getConfig() : new UsersConfig();

        add(new H3("Users mapping: CSV -> YouTrack"));
        youTrackUsers = getYouTrackUsers();
        AddCsvColumnsDialog dialog = new AddCsvColumnsDialog(getStorage().getCsvReadResult().getColumns(),
                csvUser -> addCsvColumn(csvUser, true));
        Button addColumnsBtn = new Button("Add column", VaadinIcon.PLUS.create(),
                event -> dialog.setOpened(true));
        add(addColumnsBtn);
        add(selectedCsvColumnsLayout);
        add(new Hr());
        add(usersMappingsLayout);

        // load existing mappings if exist
        if (hasStoredConfig()) {
            getConfig().getSelectedCsvColumns()
                    .forEach(column -> addCsvColumn(column, false));
            updateMappingLayouts();
        }
    }

    private void addCsvColumn(String csvColumn, boolean updateMappingLayouts) {
        usersConfig.getSelectedCsvColumns().add(csvColumn);
        selectedCsvColumnsLayout.add(new CsvColumnLabel(csvColumn, listener -> {
            usersConfig.getSelectedCsvColumns().remove(csvColumn);
            if (updateMappingLayouts) {
                updateMappingLayouts();
            }
        }));
        if (updateMappingLayouts) {
            updateMappingLayouts();
        }
    }

    private Collection<User> getYouTrackUsers() {
        try {
            return service.getUsers(getStorage().getConnectionConfig());
        } catch (InvalidHostnameException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (HostUnreachableException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (UnauthorizedException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void updateMappingLayouts() {
        Collection<String> csvUsers = extractUsersFromColumns(usersConfig.getSelectedCsvColumns());
        Collection<String> toRemove = new HashSet<>();
        Collection<String> toAdd = new HashSet<>();

        for (String mappedUser : userToMappingLayout.keySet()) {
            boolean missing = true;
            for (String csvUser : csvUsers) {
                if (Objects.equals(csvUser, mappedUser)) {
                    missing = false;
                    break;
                }
            }
            if (missing) {
                toRemove.add(mappedUser);
            }
        }

        for (String csvUser : csvUsers) {
            if (!userToMappingLayout.containsKey(csvUser)) {
                toAdd.add(csvUser);
            }
        }

        toRemove.forEach(csvUser -> {
            if (userToMappingLayout.containsKey(csvUser)) {
                UserMappingLayout userMappingLayout = userToMappingLayout.get(csvUser);
                userMappingLayout.getElement().removeFromParent();
                userToMappingLayout.remove(csvUser);
            }
        });
        toAdd.forEach(csvUser -> {
            if (!userToMappingLayout.containsKey(csvUser)) {
                UserMappingLayout userMappingLayout = new UserMappingLayout(csvUser, youTrackUsers, usersConfig);
                userToMappingLayout.put(csvUser, userMappingLayout);
                usersMappingsLayout.add(userMappingLayout);
            }
        });
    }

    private Collection<String> extractUsersFromColumns(Collection<String> selectedColumns) {
        Collection<String> users = new HashSet<>();
        List<Integer> columnsIndexes = new ArrayList<>(selectedColumns.size());
        // determine columns indexes
        List<String> csvColumns = getStorage().getCsvReadResult().getColumns();
        for (int i = 0; i < csvColumns.size(); i++) {
            for (String selectedColumn : selectedColumns) {
                if (Objects.equals(csvColumns.get(i), selectedColumn)) {
                    columnsIndexes.add(i);
                }
            }
        }

        // read users from columns
        List<List<String>> rows = getStorage().getCsvReadResult().getRows();
        rows.forEach(row -> columnsIndexes.forEach(index -> {
            String value = row.get(index);
            if (!StringUtils.isEmpty(value)) {
                users.add(value);
            }
        }));
        return users;
    }

    private static class UserMappingLayout extends HorizontalLayout {

        private final ComboBox<User> usersBox = new ComboBox<>();
        private final UsersConfig usersConfig;
        private final String csvUser;

        UserMappingLayout(String csvUser, Collection<User> youTrackUsers, UsersConfig usersConfig) {
            this.csvUser = csvUser;
            this.usersConfig = usersConfig;

            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            Label csvColumnLabel = new Label(csvUser);
            csvColumnLabel.setWidth("200px");

            usersBox.setItems(youTrackUsers);
            usersBox.setItemLabelGenerator(User::getLogin);
            usersBox.setErrorMessage("User mapping is required");
            usersBox.setAllowCustomValue(false);
            usersBox.addValueChangeListener(event -> validateSelection(event.getValue()));

            if (usersConfig.getMapping().containsKey(csvUser)) {
                usersBox.setValue(usersConfig.getMapping().get(csvUser));
            }

            add(csvColumnLabel);
            add(usersBox);
        }

        boolean isValid() {
            return validateSelection();
        }

        private boolean validateSelection() {
            return validateSelection(usersBox.getValue());
        }

        private boolean validateSelection(User user) {
            boolean isNull = Objects.isNull(user);
            usersBox.setInvalid(isNull);
            usersConfig.getMapping().put(csvUser, isNull ? null : user);
            return !isNull;
        }
    }

}
