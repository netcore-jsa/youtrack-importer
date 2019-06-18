package software.netcore.youtrack.ui.wizard.view;

/**
 * @param <T>
 * @since v. 1.0.0
 */
@FunctionalInterface
public interface RemovalListener<T> {

    void onRemoval(T object);

}
