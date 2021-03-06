package net.rizecookey.cookeymod.config.option;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.category.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class Option<T> {
    String id;
    Category category;
    T defaultValue;
    T value;
    Supplier<AbstractConfigListEntry<?>> entry;
    net.minecraft.client.Option mcOption;

    public Option(String id, Category category, T defaultValue, boolean forceRestart) {
        this.id = id;
        this.category = category;
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public Option(String id, Category category, T defaultValue) {
        this(id, category, defaultValue, false);
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return this.category.getTranslationKey() + "." + id;
    }

    public Category getCategory() {
        return category;
    }

    public T get() {
        return this.value;
    }

    public Object getInConfigFormat() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T getDefault() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public void load(Object object) {
        this.set((T) object);
    }

    public AbstractConfigListEntry<?> getConfigEntry() {
        return entry.get();
    }

    public void setConfigEntry(Supplier<AbstractConfigListEntry<?>> entry) {
        this.entry = entry;
    }

    public net.minecraft.client.Option getMcOption() {
        return mcOption;
    }

    public Optional<Component[]> getTooltip(String translationId) {
        List<Component> components = new ArrayList<>();
        String tooltipKey = translationId + ".tooltip.";

        int i = 0;
        while (i != -1) {
            if (Language.getInstance().has(tooltipKey + i)) {
                components.add(new TranslatableComponent(tooltipKey + i));
                i++;
            }
            else {
                i = -1;
            }
        }
        Component[] array = components.toArray(new Component[0]);

        return Optional.ofNullable(array.length != 0 ? array : null);
    }
}
