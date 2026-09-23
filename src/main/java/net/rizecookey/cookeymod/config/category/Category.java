package net.rizecookey.cookeymod.config.category;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.setting.Setting;

import java.util.HashMap;
import java.util.Map;

import static net.rizecookey.cookeymod.config.ModConfig.MAPPER;

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

    public void loadSettings(ObjectNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        for (var field : node.properties()) {
            String key = field.getKey();
            Setting<?> setting = this.settings.get(key);
            if (setting != null) setting.load(field.getValue());
        }
    }

    public ObjectNode toNode() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (Setting<?> setting : this.getSettings().values()) {
            node.set(setting.getId(), MAPPER.convertValue(setting.getInConfigFormat(), JsonNode.class));
        }

        return node;
    }
}
