package net.rizecookey.cookeymod.config.category;

import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.Setting;

import java.util.HashMap;
import java.util.Map;

public abstract class Category {
    private final Map<String, Setting<?>> settings = new HashMap<>();

    private final ModConfig modConfig;

    protected Category(ModConfig modConfig) {
        this.modConfig = modConfig;
    }

    public ModConfig getModConfig() {
        return this.modConfig;
    }

    public abstract String getId();

    public String getTranslationKey() {
        return ModConfig.TRANSLATION_KEY + "." + this.getId();
    }

    public Map<String, Setting<?>> getSettings() {
        return new HashMap<>(settings);
    }

    public <T extends Setting<?>> T register(T setting) {
        settings.put(setting.getId(), setting);
        return setting;
    }
}
