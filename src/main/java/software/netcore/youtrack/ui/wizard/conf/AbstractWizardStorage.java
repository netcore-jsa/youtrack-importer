package software.netcore.youtrack.ui.wizard.conf;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
public abstract class AbstractWizardStorage implements WizardStorage {

    private final Map<String, Object> stepConfigs = new LinkedHashMap<>();

    @Override
    public void setConfigsOrder(String... navigations) {
        for (String navigation : navigations) {
            stepConfigs.put(navigation, null);
        }
    }

    @Override
    public <T> T getConfig(String navigation) {
        //noinspection unchecked
        return (T) stepConfigs.get(navigation);
    }

    @Override
    public <T> void setConfig(String navigation, T config) {
        stepConfigs.put(navigation, config);
    }

    @Override
    public void invalidateFollowingConfigs(String navigation) {
        Iterator<String> keysIterator = stepConfigs.keySet().iterator();
        boolean invalidate = false;
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (invalidate) {
                stepConfigs.put(key, null);
                continue;
            }
            if (Objects.equals(key, navigation)) {
                invalidate = true;
            }
        }
    }

    @Override
    public String getFirstInvalidConfigNavigation() {
        for (String navigation : stepConfigs.keySet()) {
            if (Objects.isNull(stepConfigs.get(navigation))) {
                return navigation;
            }
        }
        return stepConfigs.keySet().iterator().next();
    }

}
