package software.netcore.youtrack.buisness.client.entity.field.project;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.UserGroup;

import java.util.Arrays;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class GroupProjectCustomField extends ProjectCustomField {

    private UserGroup[] defaultValues;

    @Override
    public String toString() {
        return "GroupProjectCustomField{" +
                super.toString() +
                "defaultValues=" + Arrays.toString(defaultValues) +
                '}';
    }

}
