package software.netcore.youtrack.ui.wizard.conf;

/**
 * @since v. 1.0.0
 */
public interface WizardStorage {

    <T> T getConfig(String navigation);

    <T> void setConfig(String navigation, T config);

    void invalidateFollowingConfigs(String navigation);

    String getFirstInvalidConfigNavigation();

    void setConfigsOrder(String... navigations);

}
