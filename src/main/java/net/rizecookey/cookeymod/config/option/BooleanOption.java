package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.rizecookey.cookeymod.config.category.Category;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String id, Category category, Boolean defaultValue, boolean forceRestart) {
        super(id, category, defaultValue, forceRestart);
        this.entry = () -> {
            BooleanToggleBuilder builder = ConfigEntryBuilder.create()
                    .startBooleanToggle(MutableComponent.create(new TranslatableContents(this.getTranslationKey())), this.get())
                    .setDefaultValue(this.defaultValue)
                    .setSaveConsumer(this::set);
            builder.requireRestart(forceRestart);
            builder.setTooltip(this.getTooltip(this.getTranslationKey()));
            return builder.build();
        };
    }
    public BooleanOption(String id, Category category, Boolean defaultValue) {
        this(id, category, defaultValue, false);
    }
}
