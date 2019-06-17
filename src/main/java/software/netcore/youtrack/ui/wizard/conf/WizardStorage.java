package software.netcore.youtrack.ui.wizard.conf;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.service.csv.pojo.CsvReadResult;
import software.netcore.youtrack.buisness.service.youtrack.entity.*;

/**
 * @since v. 1.0.0
 */
@Getter
public class WizardStorage {

    @Setter
    private CsvReadResult csvReadResult;
    @Setter
    private ConnectionInfo connectionInfo;
    @Setter
    private CustomFieldsMapping customFieldsMapping;
    @Setter
    private UsersMapping usersMapping;
    @Setter
    private EnumsMapping enumsMapping;

    public ImportConfig getImportConfig() {
        return new ImportConfig(connectionInfo, customFieldsMapping, usersMapping);
    }

}
