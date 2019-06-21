package software.netcore.youtrack.ui.wizard.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.startup.UserConfig;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.CustomFieldsMapping;
import software.netcore.youtrack.buisness.service.youtrack.entity.EnumsMapping;
import software.netcore.youtrack.buisness.service.youtrack.entity.YouTrackConnectionConfig;
import software.netcore.youtrack.ui.wizard.view.*;

/**
 * @since v. 1.0.0
 */
@Slf4j
public class YouTrackImporterStorage extends AbstractWizardStorage {

    public CsvReadResult getCsvReadResult() {
        return getConfig(CsvLoadView.NAVIGATION);
    }

    public YouTrackConnectionConfig getConnectionConfig() {
        return getConfig(ConnectionView.NAVIGATION);
    }

    public CustomFieldsMapping getCustomFieldsConfig() {
        return getConfig(CustomFieldsMappingView.NAVIGATION);
    }

    public UserConfig userConfig() {
        return getConfig(UsersMappingView.NAVIGATION);
    }

    public EnumsMapping getEnumsConfig() {
        return getConfig(EnumsMappingView.NAVIGATION);
    }

}
