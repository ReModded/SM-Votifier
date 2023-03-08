package pl.ibcgames.smvotifier.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import pl.ibcgames.smvotifier.SMVotifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader<T> {
    private final TypeToken<T> configToken;
    private final ExceptionSupplier<T> provider;

    public ConfigLoader(Class<T> configType) {
        configToken = TypeToken.of(configType);
        provider = configType::newInstance;
    }

    public T loadConfig(String path) throws InstantiationException, IllegalAccessException {
        SMVotifier.getInstance().logger.info("Loading configuration!");
        Path configPath = Paths.get("./config", path);
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(configPath).build();

        try {
            if (!Files.exists(configPath)) {
                SMVotifier.getInstance().logger.info("Config not found, creating new one!");
                loader.save(loader.createEmptyNode().setValue(configToken, provider.get()));
            }

            ConfigurationNode node = loader.load(ConfigurationOptions.defaults());
            ConfigurationNode ref = loader.createEmptyNode().setValue(configToken, provider.get());

            node.mergeValuesFrom(ref);
            loader.save(node);
            return postProcess(node.getValue(configToken, provider.get()));
        } catch (IOException | ObjectMappingException e) {
            SMVotifier.getInstance().logger.error("An error occured during config loading!");
            e.printStackTrace();
            return postProcess(provider.get());
        }
    }

    protected T postProcess(T config) {
        return config;
    }

    private interface ExceptionSupplier<T> {
        T get() throws InstantiationException, IllegalAccessException;
    }
}