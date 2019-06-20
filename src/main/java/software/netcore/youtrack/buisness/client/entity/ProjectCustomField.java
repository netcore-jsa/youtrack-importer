package software.netcore.youtrack.buisness.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.bundle.Bundle;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ProjectCustomField {

    private String id;

    @JsonProperty("field")
    private CustomField customField;

    @JsonProperty("$type")
    private String type;

    private boolean canBeEmpty;

    private String emptyFieldText;

    private Bundle bundle;

    @Override
    public String toString() {
        return "ProjectCustomField{" +
                "id='" + id + '\'' +
                ", canBeEmpty=" + canBeEmpty +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                ", field=" + customField +
                ", type=" + type +
                '}';
    }

}
