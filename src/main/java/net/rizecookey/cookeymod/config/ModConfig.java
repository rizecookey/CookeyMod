package net.rizecookey.cookeymod.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class ModConfig {
    public static final String TRANSLATION_KEY = "options.cookeymod";
    public static final String GENERIC_KEYS = TRANSLATION_KEY + "." + "generic.options";

    CookeyMod mod;
    Logger logger;

    Path file;
    Toml toml;
    Map<String, Category> categories = new HashMap<>();
    long version;

    public ModConfig(Path file) {
        this.mod = CookeyMod.getInstance();
        this.logger = mod.getLogger();

        this.file = file;
        this.registerCategories();
        try {
            this.loadConfig();
        } catch (IOException e) {
            logger.error("Failed to load CookeyMod config file!");
            e.printStackTrace();
        }
    }

    public void registerCategories() {
        this.registerCategory(new AnimationsCategory());
        this.registerCategory(new HudRenderingCategory());
        this.registerCategory(new MiscCategory());
    }

    public void registerCategory(Category category) {
        categories.put(category.getId(), category);
    }

    public void loadCategories() {
        for (String id : categories.keySet()) {
            Toml category = toml.getTable(id);
            categories.get(id).loadOptions(category != null ? category.toMap() : new HashMap<>());
        }
    }

    public Map<String, Category> getCategories() {
        return new HashMap<>(this.categories);
    }

    public void loadConfig() throws IOException {
        if (!Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }

        InputStream resourceStream = getConfigResource();
        if (!Files.exists(file)) {
            logger.info("Config not found, creating default one...");
            Files.copy(resourceStream, file);
            logger.info("Copied default config.");
        }
        else {
            Map<String, Object> configMap = new Toml().read(file.toFile()).toMap();
            Map<String, Object> fallbackMap = new Toml().read(resourceStream).toMap();
            this.copyMissingNested(fallbackMap, configMap);
            new TomlWriter().write(configMap, file.toFile());
        }
        resourceStream.close();

        this.toml = new Toml().read(file.toFile());
        this.version = this.toml.contains("config-version") ? this.toml.getLong("config-version") : 1;
        this.loadCategories();
    }

    public Category getCategory(String id) {
        return this.categories.get(id);
    }

    public <T extends Category> T getCategory(String id, Class<T> categoryClass) {
        if (Category.class.isAssignableFrom(categoryClass)) {
            return (T) this.categories.get(id);
        }
        return null;
    }

    public <T extends Category> T getCategory(Class<T> categoryClass) {
        if (Category.class.isAssignableFrom(categoryClass)) {
            for (Category category : this.categories.values()) {
                if (category.getClass().equals(categoryClass)) {
                    return (T) category;
                }
            }
        }
        return null;
    }

    public void saveConfig() throws IOException {
        Map<String, Object> optionsMap = new HashMap<>();
        for (String id : categories.keySet()) {
            optionsMap.put(id, categories.get(id).toMap());
        }

        optionsMap.put("config-version", this.version);

        new TomlWriter().write(optionsMap, file.toFile());
    }

    @SuppressWarnings("rawtypes")
    public <T, U> void copyMissingNested(Map<T, U> from, Map<T, U> to) {
        for (T fromKey : from.keySet()) {
            U fromValue = from.get(fromKey);
            Optional<U> toValue = to.containsKey(fromKey) ? Optional.of(to.get(fromKey)) : Optional.empty();
            if (!toValue.isPresent()) {
                to.put(fromKey, fromValue);
            }
            else if (fromValue instanceof Map && toValue.get() instanceof Map) {
                this.copyMissingNested((Map) fromValue, (Map) toValue.get());
            }
        }
    }

    public InputStream getConfigResource() {
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("assets/" + mod.getModId() + "/config.toml");
        if (resourceStream == null) {
            logger.error("Failed to find config resource!");
            return null;
        }
        return resourceStream;
    }

    public String getTranslationKey() {
        return TRANSLATION_KEY;
    }
}
