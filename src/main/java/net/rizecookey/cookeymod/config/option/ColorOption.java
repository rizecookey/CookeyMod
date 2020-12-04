package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.category.Category;

public class ColorOption extends Option<Color> {
    public ColorOption(String id, Category category, Color defaultValue, boolean alphaMode) {
        super(id, category, defaultValue);
        this.entry = () -> ConfigEntryBuilder.create()
                .startAlphaColorField(new TranslatableComponent(this.getTranslationKey()), this.get())
                .setSaveConsumer2(this::set)
                .setAlphaMode(alphaMode)
                .setDefaultValue(this.getDefault().getColor())
                .build();
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
