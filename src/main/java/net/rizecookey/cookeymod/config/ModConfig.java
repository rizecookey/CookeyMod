package net.rizecookey.cookeymod.config;

import net.rizecookey.cookeymod.config.persistence.ConfigSerialization;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    public static final String TRANSLATION_KEY = "options.cookeymod";
    public static final String GENERIC_KEYS = TRANSLATION_KEY + "." + "generic.options";

    private final CookeyMod mod;
    private final Logger logger;
    private final ConfigSerialization serialization;

    private final Path file;
    private final Map<String, Category> categories = new HashMap<>();

    private final AnimationsCategory animations;
    private final HudRenderingCategory hudRendering;
    private final MiscCategory misc;

    public ModConfig(CookeyMod mod, Path file, ConfigSerialization serialization) {
        this.mod = mod;
        this.logger = mod.getLogger();
        this.serialization = serialization;
        this.file = file;

        animations = this.registerCategory(new AnimationsCategory(this));
        hudRendering = this.registerCategory(new HudRenderingCategory(this));
        misc = this.registerCategory(new MiscCategory(this));
    }

    public CookeyMod getMod() {
        return mod;
    }

    public <T extends Category> T registerCategory(T category) {
        categories.put(category.getId(), category);
        return category;
    }

    public Map<String, Category> getCategories() {
        return new HashMap<>(this.categories);
    }

    public void loadConfig() {
        boolean updated;
        try {
            updated = serialization.parseInto(this, file);
        } catch (IOException e) {
            logger.error("Could not load config", e);
            return;
        }

        if (updated) {
            this.saveConfig();
            logger.info("Updated config.");
        }
    }

    public Category getCategory(String id) {
        return this.categories.get(id);
    }

    public void saveConfig() {
        try {
            serialization.serializeTo(this, file);
        } catch (IOException e) {
            logger.error("Could not save config", e);
        }
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
