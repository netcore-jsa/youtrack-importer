package software.netcore.youtrack.buisness.client.entity.field.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class IssueCustomField {

    private String id;

    @JsonProperty("$type")
    private String $type;

    private ProjectCustomField projectCustomField;

    private BundleElement value;

}
