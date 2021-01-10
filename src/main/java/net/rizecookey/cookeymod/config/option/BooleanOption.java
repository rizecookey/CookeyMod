package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.category.Category;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String id, Category category, Boolean defaultValue) {
        super(id, category, defaultValue);
        this.entry = () -> ConfigEntryBuilder.create()
                .startBooleanToggle(new TranslatableComponent(this.getTranslationKey()), this.value)
                .setDefaultValue(this.defaultValue)
                .setSaveConsumer(this::set)
                .build();
        this.mcOption = new net.minecraft.client.BooleanOption(this.getTranslationKey(), (options -> this.get()), (options, value) -> this.set(value));
    }
}
