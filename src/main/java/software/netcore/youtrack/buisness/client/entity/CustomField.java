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
public class CustomField {

    private String id;
    private boolean canBeEmpty;
    private String emptyFieldText;
    private Field field;

}
