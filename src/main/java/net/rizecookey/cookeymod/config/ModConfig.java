package net.rizecookey.cookeymod.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unchecked")
public class ModConfig {
    public static final String TRANSLATION_KEY = "options.cookeymod";
    public static final String GENERIC_KEYS = TRANSLATION_KEY + "." + "generic.options";

    CookeyMod mod;
    Logger logger;

    File file;
    Toml toml;
    Map<String, Category> categories = new HashMap<>();

    public ModConfig(File file) {
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
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("assets/cookeymod/config.toml");
        if (resourceStream == null) {
            logger.error("Failed to find config resource!");
            return;
        }
        if (!file.exists()) {
            logger.info("Config not found, creating default one...");
            Files.copy(resourceStream, Paths.get(file.getPath()));
            logger.info("Copied default config.");
        }
        else {
            Map<String, Object> configMap = new Toml().read(file).toMap();
            Map<String, Object> fallbackMap = new Toml().read(resourceStream).toMap();
            this.copyMissingNested(fallbackMap, configMap);
            new TomlWriter().write(configMap, file);
        }
        resourceStream.close();
        this.toml = new Toml().read(file);
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

        new TomlWriter().write(optionsMap, file);
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
}
