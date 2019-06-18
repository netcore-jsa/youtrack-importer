package software.netcore.youtrack.ui.wizard.conf;

/**
 * @since v. 1.0.0
 */
public interface HasStepConfig<T> {

    T getConfig();

    void setConfig(T config);

}
