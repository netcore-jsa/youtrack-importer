package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.service.csv.CsvReader;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.ui.notification.ErrorNotification;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.WizardStorage;

import java.io.IOException;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = CsvLoadView.NAVIGATION, layout = WizardFlowView.class)
public class CsvLoadView extends AbstractFlowStepView {

    public static final String NAVIGATION = "csv_load";

    private final CsvReader csvReader;

    public CsvLoadView(CsvReader csvReader, WizardStorage storage, WizardFlow wizardFlow) {
        super(storage, wizardFlow);
        this.csvReader = csvReader;
        buildView();
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    public boolean isValid() {
        //TODO validate CSV read result content as well + show validation message
        return Objects.nonNull(getStorage().getCsvReadResult());
    }

    private void buildView() {
        add(new H3(getStep().getTitle()));
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setAcceptedFileTypes(".csv");
        upload.setWidth("300px");
        upload.addSucceededListener(event -> {
            try {
                CsvReadResult readResult = csvReader.read(memoryBuffer.getFileName(), memoryBuffer.getInputStream());
                getStorage().setCsvReadResult(readResult);
            } catch (IOException e) {
                ErrorNotification.show("Error",
                        "Ooops, failed to read CSV file, try again please");
            }
        });
        add(upload);
    }

}
