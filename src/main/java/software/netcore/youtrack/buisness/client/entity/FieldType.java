package software.netcore.youtrack.buisness.client.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class FieldType {

    private String id;

    @Override
    public String toString() {
        return "FieldType{" +
                "id='" + id + '\'' +
                '}';
    }

}
