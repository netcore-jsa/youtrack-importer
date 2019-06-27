package software.netcore.youtrack.ui.wizard.conf;

import lombok.extern.slf4j.Slf4j;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.*;
import software.netcore.youtrack.ui.wizard.view.*;

/**
 * @since v. 1.0.0
 */
@Slf4j
public class YouTrackImporterStorage extends AbstractWizardStorage {

    public CsvReadResult getCsvReadResult() {
        return getConfig(CsvLoadView.NAVIGATION);
    }

    public ConnectionConfig getConnectionConfig() {
        return getConfig(ConnectionView.NAVIGATION);
    }

    public CustomFieldsMapping getCustomFieldsConfig() {
        return getConfig(CustomFieldsMappingView.NAVIGATION);
    }

    public UsersMapping getUsersMapping() {
        return getConfig(UsersMappingView.NAVIGATION);
    }

    public MandatoryFieldsMapping getMandatoryFieldsMapping() {
        return getConfig(MandatoryFieldsMappingView.NAVIGATION);
    }

    public EnumsMapping getEnumsConfig() {
        return getConfig(EnumsMappingView.NAVIGATION);
    }

}
