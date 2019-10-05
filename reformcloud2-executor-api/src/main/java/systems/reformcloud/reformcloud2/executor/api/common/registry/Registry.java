package systems.reformcloud.reformcloud2.executor.api.common.registry;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.util.Collection;
import java.util.function.Function;

public interface Registry {

    <T> T createKey(String keyName, T t);

    <T> T getKey(String keyName);

    void deleteKey(String key);

    <T> T updateKey(String key, T newValue);

    <T> Collection<T> readKeys(Function<JsonConfiguration, T> function);
}
