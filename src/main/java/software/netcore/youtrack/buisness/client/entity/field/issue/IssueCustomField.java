package software.netcore.youtrack.buisness.client.entity.field.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleBuildIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleEnumIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.SingleVersionIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.StateIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.ProjectCustomField;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleBuildIssueCustomField.class, name = "SingleBuildIssueCustomField"),
        @JsonSubTypes.Type(value = SingleEnumIssueCustomField.class, name = "SingleEnumIssueCustomField"),
        @JsonSubTypes.Type(value = SingleUserIssueCustomField.class, name = "SingleUserIssueCustomField"),
        @JsonSubTypes.Type(value = SingleVersionIssueCustomField.class, name = "SingleVersionIssueCustomField"),
        @JsonSubTypes.Type(value = StateIssueCustomField.class, name = "StateIssueCustomField"),
})
public abstract class IssueCustomField<T> {

    private String id;

    @JsonProperty("$type")
    private String $type;

    private ProjectCustomField projectCustomField;

    private T value;

}
