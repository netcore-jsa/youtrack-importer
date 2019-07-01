package software.netcore.youtrack.buisness.client.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
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
@JsonSubTypes({
        @JsonSubTypes.Type(value = Me.class, name = "Me"),
        @JsonSubTypes.Type(value = VcsUnresolvedName.class, name = "VcsUnresolvedName"),
})
public class User {

    private String id;

    private String login;

    private String fullName;

    private String email;

    private String jabberAccountName;

    private String ringId;

    private Boolean guest;

    private Boolean online;

    private Boolean banned;

    private String avatarUrl;

    @Override
    public String toString() {
        return "User{" +
                "id'" + id + '\'' +
                ", login='" + login + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", jabberAccountName='" + jabberAccountName + '\'' +
                ", ringId='" + ringId + '\'' +
                ", guest=" + guest +
                ", online=" + online +
                ", banned=" + banned +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

}
