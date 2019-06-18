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
public class Field {

    private String id;
    private String name;
    @JsonProperty("$type")
    private String type;

    @Override
    public String toString() {
        return "Field{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", name='" + type + '\'' +
                '}';
    }

}
