package net.rizecookey.cookeymod.config.persistence;

import me.shedaniel.math.Color;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.setting.BooleanSetting;
import net.rizecookey.cookeymod.config.setting.ColorSetting;
import net.rizecookey.cookeymod.config.setting.DoubleSliderSetting;
import net.rizecookey.cookeymod.config.setting.EnumSetting;
import net.rizecookey.cookeymod.config.setting.Named;
import net.rizecookey.cookeymod.config.setting.Setting;
import net.rizecookey.cookeymod.config.setting.SettingVisitor;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.toml.TomlMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class JacksonTomlConfigSerialization implements ConfigSerialization {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long CONFIG_FORMAT_VERSION = 3;
    private static final String CONFIG_VERSION_KEY = "config-version";

    private final TomlMapper tomlMapper = new TomlMapper();
    private final SettingParser settingParser = new SettingParser();
    private final SettingSerializer settingSerializer = new SettingSerializer();

    @Override
    public boolean parseInto(ModConfig config, Path file) throws IOException {
        if (!Files.exists(file)) {
            return false;
        }

        ObjectNode root;
        try {
            root = tomlMapper.readTree(file).asObject();
        } catch (JacksonIOException e) {
            throw e.getCause();
        }

        long configVersion = root.get(CONFIG_VERSION_KEY).asLong(1);
        boolean changed = JacksonTomlConfigUpdater.update(root, configVersion);

        for (String categoryId : config.getCategories().keySet()) {
            Category category = config.getCategory(categoryId);
            ObjectNode categoryNode = root.get(categoryId).asObject();
            parse(category, categoryNode);
        }

        return changed;
    }

    private void parse(Category category, ObjectNode configCategory) {
        for (var field : configCategory.properties()) {
            String key = field.getKey();
            Setting<?> setting = category.getSettings().get(key);
            if (setting != null) parse(setting, field.getValue());
        }
    }

    private void parse(Setting<?> setting, JsonNode configValue) {
        setting.accept(settingParser, configValue);
    }

    @Override
    public void serializeTo(ModConfig config, Path file) throws IOException {
        ObjectNode node = tomlMapper.createObjectNode();
        for (String id : config.getCategories().keySet()) {
            node.set(id, serialize(config.getCategory(id)));
        }

        node.set(CONFIG_VERSION_KEY, LongNode.valueOf(CONFIG_FORMAT_VERSION));

        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
        }

        try {
            tomlMapper.writeValue(file, node);
        } catch (JacksonIOException e) {
            throw e.getCause();
        }
    }

    private ObjectNode serialize(Category category) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (Setting<?> setting : category.getSettings().values()) {
            Object valueInConfigFormat = serialize(setting);
            node.set(setting.getId(), MAPPER.convertValue(valueInConfigFormat, JsonNode.class));
        }

        return node;
    }

    private Object serialize(Setting<?> setting) {
        return setting.accept(settingSerializer, null);
    }

    private static class SettingSerializer implements SettingVisitor<@Nullable Void, Object> {

        @Override
        public Object visitBooleanSetting(BooleanSetting booleanSetting, @Nullable Void input) {
            return booleanSetting.get();
        }

        @Override
        public Object visitColorSetting(ColorSetting colorSetting, @Nullable Void input) {
            return colorSetting.get().getColor();
        }

        @Override
        public Object visitDoubleSliderSetting(DoubleSliderSetting doubleSliderSetting, @Nullable Void input) {
            return doubleSliderSetting.get();
        }

        @Override
        public <E extends Enum<E> & Named> Object visitEnumSetting(EnumSetting<E> enumSetting, @Nullable Void input) {
            return enumSetting.get().getInternalName();
        }
    }

    private static class SettingParser implements SettingVisitor<JsonNode, @Nullable Void> {

        @Override
        public @Nullable Void visitBooleanSetting(BooleanSetting booleanSetting, JsonNode input) {
            booleanSetting.set(input.booleanValue());
            return null;
        }

        @Override
        public @Nullable Void visitColorSetting(ColorSetting colorSetting, JsonNode input) {
            colorSetting.set(Color.ofTransparent(input.asInt()));
            return null;
        }

        @Override
        public @Nullable Void visitDoubleSliderSetting(DoubleSliderSetting doubleSliderSetting, JsonNode input) {
            doubleSliderSetting.set(input.asDouble());
            return null;
        }

        @Override
        public <E extends Enum<E> & Named> Void visitEnumSetting(EnumSetting<E> enumSetting, JsonNode input) {
            Class<E> enumClass = enumSetting.getEnumClass();
            enumSetting.set(Arrays.stream(enumClass.getEnumConstants())
                    .filter(value -> value.getInternalName().equals(input.asString()))
                    .findFirst().orElseThrow());
            return null;
        }
    }
}
