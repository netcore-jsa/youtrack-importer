package software.netcore.youtrack.buisness.service.youtrack.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @since v. 1.0.0
 */
@Getter
@AllArgsConstructor
public class ImportConfig {

    private ConnectionInfo connectionInfo;
    private CustomFieldsMapping customFieldsMapping;
    private UsersMapping usersMapping;

}
