package software.netcore.youtrack.buisness.client.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomField {

    private String id;
    private boolean canBeEmpty;
    private String emptyFieldText;
    private Field field;
    @JsonProperty("$type")
    private String type;

    @Override
    public String toString() {
        return "CustomField{" +
                "id='" + id + '\'' +
                ", canBeEmpty=" + canBeEmpty +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                ", field=" + field +
                ", type=" + type +
                '}';
    }

}
