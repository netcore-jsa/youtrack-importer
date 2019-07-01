package software.netcore.youtrack.buisness.client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.netcore.youtrack.buisness.client.entity.user.User;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class IssueComment {

    private String text;

    private Boolean usesMarkDown;

    private String textPreview;

    private Long created;

    private Long updated;

    private User author;

    private Issue issue;

    private Boolean deleted;

    @Override
    public String toString() {
        return "IssueComment{" +
                "author=" + author +
                ", issue=" + issue +
                ", created=" + created +
                ", text='" + text + '\'' +
                '}';
    }

}
