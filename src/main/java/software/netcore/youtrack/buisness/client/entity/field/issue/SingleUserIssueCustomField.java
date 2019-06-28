package software.netcore.youtrack.buisness.client.entity.field.issue;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.user.User;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class SingleUserIssueCustomField extends IssueCustomField<User> {
}
