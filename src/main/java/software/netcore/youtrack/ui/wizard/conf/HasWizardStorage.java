package software.netcore.youtrack.ui.wizard.conf;

/**
 * @param <T>
 * @since v. 1.0.0
 */
public interface HasWizardStorage<T extends WizardStorage> {

    T getStorage();

}
