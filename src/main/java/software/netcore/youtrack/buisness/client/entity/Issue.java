package software.netcore.youtrack.buisness.client.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;
import software.netcore.youtrack.buisness.client.entity.user.User;

import java.util.Arrays;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class Issue {

    private String idReadable;

    private Long created;

    private Long updated;

    private Long resolved;

    private Long numberInProject;

    private String summary;

    private String description;

    private User reporter;

    private User updater;

    private User draftOwner;

    private Boolean isDraft;

    private Project project;

    private IssueCustomField[] customFields;

    @Override
    public String toString() {
        return "Issue{" +
                "idReadable='" + idReadable + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", resolved=" + resolved +
                ", numberInProject=" + numberInProject +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", reporter=" + reporter +
                ", updater=" + updater +
                ", draftOwner=" + draftOwner +
                ", isDraft=" + isDraft +
                ", project=" + project +
                ", customFields=" + Arrays.toString(customFields) +
                '}';
    }

}
