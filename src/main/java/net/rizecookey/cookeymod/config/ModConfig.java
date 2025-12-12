package net.rizecookey.cookeymod.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
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

public class ModConfig {
    public static final String TRANSLATION_KEY = "options.cookeymod";
    public static final String GENERIC_KEYS = TRANSLATION_KEY + "." + "generic.options";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String CONFIG_VERSION_KEY = "config-version";

    private final CookeyMod mod;
    private final Logger logger;
    private final TomlMapper tomlMapper;

    private final Path file;
    private ObjectNode defaults;
    private ObjectNode config;
    private final Map<String, Category> categories = new HashMap<>();
    private long version;

    private final AnimationsCategory animations;
    private final HudRenderingCategory hudRendering;
    private final MiscCategory misc;

    public ModConfig(CookeyMod mod, Path file) {
        this.mod = mod;
        this.logger = mod.getLogger();
        this.tomlMapper = new TomlMapper();

        this.file = file;

        animations = this.registerCategory(new AnimationsCategory(this));
        hudRendering = this.registerCategory(new HudRenderingCategory(this));
        misc = this.registerCategory(new MiscCategory(this));

        try {
            this.loadConfig();
        } catch (IOException e) {
            logger.error("Failed to load CookeyMod config file", e);
        }
    }

    public CookeyMod getMod() {
        return mod;
    }

    public <T extends Category> T registerCategory(T category) {
        categories.put(category.getId(), category);
        return category;
    }

    public void loadCategories() {
        boolean updated = ConfigUpdater.update(config, this.version);

        if (updated) logger.info("Updated config.");

        for (String id : categories.keySet()) {
            ObjectNode category = ((ObjectNode) config.get(id));
            categories.get(id).loadOptions(category != null ? category : MAPPER.createObjectNode());
        }

        this.version = this.defaults.has(CONFIG_VERSION_KEY) ? this.defaults.get(CONFIG_VERSION_KEY).asLong() : 1;

        if (updated) {
            try {
                this.saveConfig();
            } catch (IOException e) {
                CookeyMod.getInstance().getLogger().error("Failed to save CookeyMod config file", e);
            }
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
        this.defaults = (ObjectNode) tomlMapper.readTree(resourceStream);
        if (!Files.exists(file)) {
            logger.info("Config not found, creating default one...");
            tomlMapper.writeValue(file.toFile(), this.defaults);
            logger.info("Copied default config.");
        } else {
            ObjectNode config = tomlMapper.readValue(file.toFile(), ObjectNode.class);
            if (!config.has(CONFIG_VERSION_KEY)) {
                config.set(CONFIG_VERSION_KEY, LongNode.valueOf(1));
            }

            this.copyMissingNested(defaults, config);
            tomlMapper.writeValue(file.toFile(), config);
        }
        resourceStream.close();

        this.config = (ObjectNode) tomlMapper.readTree(file.toFile());
        this.version = this.config.has(CONFIG_VERSION_KEY) ? this.config.get(CONFIG_VERSION_KEY).asLong() : 1;
        this.loadCategories();
    }

    public Category getCategory(String id) {
        return this.categories.get(id);
    }

    public void saveConfig() throws IOException {
        ObjectNode node = tomlMapper.createObjectNode();
        for (String id : categories.keySet()) {
            node.set(id, categories.get(id).toNode());
        }

        node.set(CONFIG_VERSION_KEY, LongNode.valueOf(this.version));

        tomlMapper.writeValue(file.toFile(), node);
    }

    public void copyMissingNested(ObjectNode from, ObjectNode to) {
        for (var child : from.properties()) {
            var key = child.getKey();
            var value = child.getValue();
            if (!to.has(key)) {
                to.set(key, value);
                continue;
            }
            var toValue = to.get(key);
            if (value.isObject() && toValue.isObject()) {
                copyMissingNested(((ObjectNode) value), ((ObjectNode) toValue));
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

    public long getVersion() {
        return version;
    }

    public String getTranslationKey() {
        return TRANSLATION_KEY;
    }

    public AnimationsCategory animations() {
        return animations;
    }

    public HudRenderingCategory hudRendering() {
        return hudRendering;
    }

    public MiscCategory misc() {
        return misc;
    }
}
