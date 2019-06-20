package software.netcore.youtrack.buisness.client.entity.bundle;

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
        @JsonSubTypes.Type(value = EnumBundle.class, name = "EnumBundle"),
        @JsonSubTypes.Type(value = OwnedBundle.class, name = "OwnedBundle"),
        @JsonSubTypes.Type(value = StateBundle.class, name = "StateBundle"),
        @JsonSubTypes.Type(value = VersionBundle.class, name = "VersionBundle"),
        @JsonSubTypes.Type(value = BuildBundle.class, name = "BuildBundle"),
        @JsonSubTypes.Type(value = UserBundle.class, name = "UserBundle")
})
public abstract class Bundle {

    private String id;

    private boolean isUpdateable;

    @JsonProperty("$type")
    private String type;

    @Override
    public String toString() {
        return "Bundle{" +
                "id='" + id + '\'' +
                ", isUpdateable=" + isUpdateable +
                ", type='" + type + '\'' +
                '}';
    }

}
