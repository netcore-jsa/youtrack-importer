package software.netcore.youtrack.buisness.client.entity.field.issue.base;

import lombok.Getter;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public abstract class BaseIssueCustomField<T extends BundleElement> extends IssueCustomField<T> {

}
