package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.category.Category;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String id, Category category, Boolean defaultValue, boolean forceRestart) {
        super(id, category, defaultValue, forceRestart);
        this.entry = () -> {
            BooleanToggleBuilder builder = ConfigEntryBuilder.create()
                    .startBooleanToggle(new TranslatableComponent(this.getTranslationKey()), this.get())
                    .setDefaultValue(this.defaultValue)
                    .setSaveConsumer(this::set);
            builder.requireRestart(forceRestart);
            builder.setTooltip(this.getTooltip(this.getTranslationKey()));
            return builder.build();
        };
        this.mcOption = new net.minecraft.client.BooleanOption(this.getTranslationKey(), (options -> this.get()), (options, value) -> this.set(value));
    }
    public BooleanOption(String id, Category category, Boolean defaultValue) {
        this(id, category, defaultValue, false);
    }
}
