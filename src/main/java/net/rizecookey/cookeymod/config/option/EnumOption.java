package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.config.category.Category;

import java.util.Arrays;

public class EnumOption<T extends Enum<T> & Named> extends Option<T, EnumListEntry<T>> {
    private final Class<T> enumClass;

    public EnumOption(String id, Category category, Class<T> enumClass, T defaultValue, boolean forceRestart) {
        super(id, category, defaultValue);
        this.enumClass = enumClass;
        this.setConfigEntry(() -> {
            EnumSelectorBuilder<T> builder = ConfigEntryBuilder.create()
                    .startEnumSelector(Component.translatable(this.getTranslationKey()), enumClass, this.get())
                    .setEnumNameProvider(value -> ((Named) value).getDisplayName())
                    .setDefaultValue(this.getDefault())
                    .setSaveConsumer(this::set);
            builder.requireRestart(forceRestart);
            builder.setTooltip(this.getTooltip(this.getTranslationKey()));
            return builder.build();
        });
    }

    public EnumOption(String id, Category category, Class<T> enumClass, T defaultValue) {
        this(id, category, enumClass, defaultValue, false);
    }

    @Override
    public void load(Object object) {
        this.set(Arrays.stream(enumClass.getEnumConstants())
                .filter(value -> value.getInternalName().equals(object))
                .findFirst().orElseThrow());
    }

    @Override
    public Object getInConfigFormat() {
        return this.get().getInternalName();
    }
}
