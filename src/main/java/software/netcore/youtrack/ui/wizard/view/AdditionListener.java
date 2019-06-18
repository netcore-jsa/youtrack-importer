package software.netcore.youtrack.ui.wizard.view;

/**
 * @param <T>
 * @since v. 1.0.0
 */
@FunctionalInterface
interface AdditionListener<T> {

    void onAddition(T object);

}
