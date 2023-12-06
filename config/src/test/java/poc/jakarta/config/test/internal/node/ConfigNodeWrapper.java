package poc.jakarta.config.test.internal.node;

import poc.jakarta.config.internal.node.ConfigNode;
import poc.jakarta.config.value.ConfigNodeValue;

import java.util.Optional;

public class ConfigNodeWrapper {
    private final ConfigNode configNode;

    public ConfigNodeWrapper(ConfigNode configNode) {
        this.configNode = configNode;
    }

    public Optional<ConfigNodeValue> getValue() {
        return configNode.value();
    }

    public String getKey() {
        return configNode.key();
    }

}
