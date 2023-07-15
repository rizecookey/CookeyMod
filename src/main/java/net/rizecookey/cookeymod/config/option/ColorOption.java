package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.impl.builders.ColorFieldBuilder;
import me.shedaniel.math.Color;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.config.category.Category;

public class ColorOption extends Option<Color, ColorEntry> {
    public ColorOption(String id, Category category, Color defaultValue, boolean alphaMode) {
        super(id, category, defaultValue);
        this.setConfigEntry(() -> {
            ColorFieldBuilder builder = ConfigEntryBuilder.create()
                    .startAlphaColorField(Component.translatable(this.getTranslationKey()), this.get())
                    .setSaveConsumer2(this::set)
                    .setAlphaMode(alphaMode)
                    .setDefaultValue(this.getDefault().getColor());
            builder.setTooltip(this.getTooltip(this.getTranslationKey()));
            return builder.build();
        });
    }

    @Override
    public void load(Object object) {
        this.set(Color.ofTransparent((int) (long) object));
    }

    @Override
    public Object getInConfigFormat() {
        return this.get().getColor();
    }
}
