package software.netcore.youtrack.buisness.client.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Me.class, name = "Me"),
        @JsonSubTypes.Type(value = VcsUnresolvedName.class, name = "VcsUnresolvedName"),
})
public class User {

    @JsonProperty("$type")
    private String type;

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
                "login='" + login + '\'' +
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
