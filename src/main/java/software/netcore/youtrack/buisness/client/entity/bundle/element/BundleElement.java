package software.netcore.youtrack.buisness.client.entity.bundle.element;

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
        @JsonSubTypes.Type(value = EnumBundleElement.class, name = "EnumBundleElement"),
        @JsonSubTypes.Type(value = OwnedBundleElement.class, name = "OwnedBundleElement"),
        @JsonSubTypes.Type(value = StateBundleElement.class, name = "StateBundleElement"),
        @JsonSubTypes.Type(value = VersionBundleElement.class, name = "VersionBundleElement"),
        @JsonSubTypes.Type(value = BuildBundleElement.class, name = "BuildBundleElement")
})
public abstract class BundleElement {

    private String id;

    private String name;

    @Override
    public String toString() {
        return "BundleElement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
