package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Maps YouTrack issue's mandatory fields to CSV columns
 *
 * @since v. 1.0.0
 */
@Getter
@Setter
public class MandatoryFieldsMapper {

    private String summary;
    private String description;
    private String comments;
    private String reporter;

}
