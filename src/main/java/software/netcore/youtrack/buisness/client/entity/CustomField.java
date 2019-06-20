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

    private int ordinal;

    private String name;

    @JsonProperty("$type")
    private String type;

    private FieldType fieldType;

    private String aliases;

    @Override
    public String toString() {
        return "CustomField{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", fieldType=" + fieldType +
                '}';
    }

}
