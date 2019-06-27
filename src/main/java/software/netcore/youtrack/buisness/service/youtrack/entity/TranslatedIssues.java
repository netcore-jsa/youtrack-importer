package software.netcore.youtrack.buisness.service.youtrack.entity;

import lombok.Getter;
import software.netcore.youtrack.buisness.client.entity.Issue;
import software.netcore.youtrack.buisness.client.entity.IssueComment;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@Getter
public class TranslatedIssues {

    private final Collection<Issue> issues = new ArrayList<>();

    private final Map<Issue, List<IssueComment>> issueComments = new HashMap<>();

}
