package net.rizecookey.cookeymod.config.category;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.rizecookey.cookeymod.config.ModConfig.MAPPER;

public abstract class Category {
    private final Map<String, Option<?, ?>> options = new HashMap<>();
    private final List<Supplier<AbstractConfigListEntry<?>>> menuEntries = new ArrayList<>();

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

    public Map<String, Option<?, ?>> getOptions() {
        return new HashMap<>(options);
    }

    public <T extends Option<?, ?>> T register(T option) {
        options.put(option.getId(), option);
        menuEntries.add(option::getConfigEntry);
        return option;
    }

    public void registerGroup(String translationKey, Option<?, ?>... options) {
        for (Option<?, ?> option : options) {
            this.options.put(option.getId(), option);
        }

        String translationId = this.getTranslationKey() + "." + translationKey;
        this.menuEntries.add(() -> {
            SubCategoryBuilder builder = ConfigEntryBuilder.create()
                    .startSubCategory(Component.translatable(translationId));
            builder.setTooltip(Option.getTooltip(translationId));

            for (Option<?, ?> option : options) {
                builder.add(option.getConfigEntry());
            }

            return builder.build();
        });
    }

    public void loadOptions(ObjectNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        for (var field : node.properties()) {
            String key = field.getKey();
            Option<?, ?> option = this.options.get(key);
            if (option != null) option.load(field.getValue());
        }
    }

    public ObjectNode toNode() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (Option<?, ?> option : this.getOptions().values()) {
            node.set(option.getId(), MAPPER.convertValue(option.getInConfigFormat(), JsonNode.class));
        }

        return node;
    }

    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        return menuEntries.stream().<AbstractConfigListEntry<?>>map(Supplier::get).toList();
    }
}
