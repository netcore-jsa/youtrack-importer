package software.netcore.youtrack.buisness.service.youtrack.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class ImportConfig {

    private ConnectionInfo connectionInfo = new ConnectionInfo();
    private CustomFieldsMapping customFieldsMapping = new CustomFieldsMapping();
    private UsersMapping usersMapping = new UsersMapping();

}
