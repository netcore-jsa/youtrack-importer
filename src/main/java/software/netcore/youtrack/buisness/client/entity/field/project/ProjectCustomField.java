package software.netcore.youtrack.buisness.client.entity.field.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.Project;
import software.netcore.youtrack.buisness.client.entity.field.CustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.UserProjectCustomField;
import software.netcore.youtrack.buisness.client.entity.field.project.bundle.base.*;
import software.netcore.youtrack.buisness.client.entity.field.project.simple.TextProjectCustomField;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildProjectCustomField.class, name = "BuildProjectCustomField"),
        @JsonSubTypes.Type(value = EnumProjectCustomField.class, name = "EnumProjectCustomField"),
        @JsonSubTypes.Type(value = OwnedProjectCustomField.class, name = "OwnedProjectCustomField"),
        @JsonSubTypes.Type(value = VersionProjectCustomField.class, name = "VersionProjectCustomField"),
        @JsonSubTypes.Type(value = StateProjectCustomField.class, name = "StateProjectCustomField"),
        @JsonSubTypes.Type(value = UserProjectCustomField.class, name = "UserProjectCustomField"),
        @JsonSubTypes.Type(value = TextProjectCustomField.class, name = "TextProjectCustomField"),
        @JsonSubTypes.Type(value = GroupProjectCustomField.class, name = "GroupProjectCustomField"),
        @JsonSubTypes.Type(value = PeriodProjectCustomField.class, name = "PeriodProjectCustomField"),
})
public abstract class ProjectCustomField {

    private String id;

    @JsonProperty("field")
    private CustomField customField;

    private Project project;

    private String emptyFieldText;

    private Boolean canBeEmpty;

    private Integer ordinal;

    private Boolean isPublic;

    private Boolean hasRunningJob;

    @Override
    public String toString() {
        return "ProjectCustomField{" +
                "id='" + id + '\'' +
                ", customField=" + customField +
                ", project=" + project +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                ", canBeEmpty=" + canBeEmpty +
                ", ordinal=" + ordinal +
                ", isPublic=" + isPublic +
                ", hasRunningJob=" + hasRunningJob +
                '}';
    }
}
