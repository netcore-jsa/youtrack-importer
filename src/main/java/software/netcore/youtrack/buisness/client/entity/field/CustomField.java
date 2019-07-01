package software.netcore.youtrack.buisness.client.entity.field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type", visible = true)
public class CustomField {

    private String id;

    private Integer ordinal;

    private String name;

    private String aliases;

    @Override
    public String toString() {
        return "CustomField{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
