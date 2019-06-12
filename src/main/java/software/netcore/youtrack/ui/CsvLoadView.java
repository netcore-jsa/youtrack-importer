package software.netcore.youtrack.ui;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.service.csv.CsvReader;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.ui.notification.ErrorNotification;
import software.netcore.youtrack.ui.wizard.ImportWizard;

import java.io.IOException;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = CsvLoadView.NAVIGATION, layout = WizardView.class)
public class CsvLoadView extends WizardStepView {

    public static final String NAVIGATION = "csv_load";

    private final ImportWizard importWizard;
    private final CsvReader csvReader;

    public CsvLoadView(ImportWizard importWizard, CsvReader csvReader) {
        this.importWizard = importWizard;
        this.csvReader = csvReader;
        buildView();
    }

    private void buildView() {
        add(new H3("Load CSV file"));
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setAcceptedFileTypes(".csv");
        upload.setWidth("300px");
        upload.addSucceededListener(event -> {
            try {
                CsvReadResult readResult = csvReader.read(memoryBuffer.getFileName(), memoryBuffer.getInputStream());
            } catch (IOException e) {
                ErrorNotification.show("Error",
                        "Ooops, failed to read CSV file, try again please");
            }
        });
        add(upload);
    }

    @Override
    String getNavigation() {
        return NAVIGATION;
    }

}
