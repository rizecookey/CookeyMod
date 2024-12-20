package net.rizecookey.cookeymod.config.option;

import com.fasterxml.jackson.databind.JsonNode;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.extension.clothconfig.entry.PickableColorEntry;

public class ColorOption extends Option<Color, PickableColorEntry> {
    public ColorOption(String id, Category category, Color defaultValue) {
        super(id, category, defaultValue);
        this.setConfigEntry(() -> new PickableColorEntry(Component.translatable(this.getTranslationKey()),
                this.get(),
                ConfigEntryBuilder.create().getResetButtonKey(),
                () -> this.getDefault().getColor(),
                value -> this.set(Color.ofTransparent(value)),
                () -> this.getTooltip(this.getTranslationKey()),
                false));
    }

    @Override
    public void load(JsonNode object) {
        this.set(Color.ofTransparent(object.asInt()));
    }

    @Override
    public Object getInConfigFormat() {
        return this.get().getColor();
    }
}
