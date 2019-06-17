package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.netcore.youtrack.buisness.client.entity.User;
import software.netcore.youtrack.buisness.client.exception.HostUnreachableException;
import software.netcore.youtrack.buisness.client.exception.InvalidHostnameException;
import software.netcore.youtrack.buisness.client.exception.UnauthorizedException;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = UsersMappingView.NAVIGATION, layout = WizardFlowView.class)
public class UsersMappingView extends AbstractFlowStepView {

    public static final String NAVIGATION = "users_mapping";

    @FunctionalInterface
    private interface AdditionListener<T> {

        void onAddition(T object);

    }

    @FunctionalInterface
    public interface RemovalListener<T> {

        void onRemoval(T object);

    }

    private final Map<String, UserMappingLayout> usersMapping = new HashMap<>();
    private final Set<String> selectedCsvColumns = new HashSet<>();
    private final Div selectedCsvColumnsLayout = new Div();
    private final Div usersMappingsLayout = new Div();
    private final YouTrackService service;

    public UsersMappingView(WizardStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow);
        this.service = service;
        buildView();
    }

    @Override
    public boolean isValid() {
        boolean isValid = true;
        for (UserMappingLayout value : usersMapping.values()) {
            isValid = value.isValid() && isValid;
        }
        return false;
    }

    @Override
    public boolean hasStoredConfiguration() {
        return Objects.nonNull(getStorage().getUsersMapping());
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    private void buildView() {
        add(new H3("Users mapping: CSV -> YouTrack"));

        try {
            Collection<User> users = service.getUsers(getStorage().getConnectionInfo());
            System.out.println(Arrays.toString(users.toArray()));
        } catch (InvalidHostnameException e) {
            e.printStackTrace();
        } catch (HostUnreachableException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }

        AddCsvColumnsDialog dialog = new AddCsvColumnsDialog(getStorage().getCsvReadResult().getColumns(),
                csvUser -> {
                    selectedCsvColumns.add(csvUser);
                    selectedCsvColumnsLayout.add(new CsvColumnLabel(csvUser, listener -> {
                        selectedCsvColumns.remove(csvUser);
                        updateMappingLayouts();
                    }));
                    updateMappingLayouts();
                });
        Button addColumnsBtn = new Button("Add column", VaadinIcon.PLUS.create(),
                event -> dialog.setOpened(true));
        add(addColumnsBtn);
        add(selectedCsvColumnsLayout);
        add(new Hr());
        add(usersMappingsLayout);
    }

    private void updateMappingLayouts() {
        Collection<String> csvUsers = extractUsersFromColumns(selectedCsvColumns);
        Collection<String> toRemove = new HashSet<>();
        Collection<String> toAdd = new HashSet<>();

        for (String mappedUser : usersMapping.keySet()) {
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
            if (!usersMapping.containsKey(csvUser)) {
                toAdd.add(csvUser);
            }
        }

        toRemove.forEach(csvUser -> {
            if (usersMapping.containsKey(csvUser)) {
                UserMappingLayout userMappingLayout = usersMapping.get(csvUser);
                userMappingLayout.getElement().removeFromParent();
                usersMapping.remove(csvUser);
            }
        });
        toAdd.forEach(csvUser -> {
            if (!usersMapping.containsKey(csvUser)) {
                UserMappingLayout userMappingLayout = new UserMappingLayout(csvUser);
                usersMapping.put(csvUser, userMappingLayout);
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

        private final TextField valueField = new TextField();

        UserMappingLayout(String csvUser) {
            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            Label csvColumnLabel = new Label(csvUser);
            csvColumnLabel.setWidth("200px");
            add(csvColumnLabel);
            add(valueField);
        }

        boolean isValid() {
            if (valueField.getValue().isEmpty()) {
                valueField.setErrorMessage("User mapping is required");
                valueField.setInvalid(true);
                return false;
            }
            return true;
        }
    }

    private static class CsvColumnLabel extends HorizontalLayout {

        CsvColumnLabel(String user, RemovalListener<String> removalListener) {
            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            add(new Label(user));
            add(new Button(VaadinIcon.CLOSE.create(), event -> {
                getElement().removeFromParent();
                removalListener.onRemoval(user);
            }));
        }
    }

    private static class AddCsvColumnsDialog extends Dialog {

        AddCsvColumnsDialog(Collection<String> csvColumns, AdditionListener<String> additionListener) {
            ComboBox<String> columnsBox = new ComboBox<>("CSV column");
            columnsBox.setItems(csvColumns);
            columnsBox.setWidthFull();
            add(columnsBox);

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
            controlsLayout.add(new Button("Add", event -> {
                String value = columnsBox.getValue();
                if (StringUtils.isEmpty(value)) {
                    columnsBox.setErrorMessage("CSV column is required");
                    columnsBox.setInvalid(true);
                    return;
                }
                additionListener.onAddition(value);
                setOpened(false);
            }));
            controlsLayout.setWidthFull();
            add(controlsLayout);
        }
    }

}
