package net.rizecookey.cookeymod.config.category;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Category {
    final HashMap<String, Option<?>> options = new HashMap<>();

    public abstract String getId();

    public String getTranslationKey() {
        return ModConfig.TRANSLATION_KEY + "." + this.getId();
    }

    public HashMap<String, Option<?>> getOptions() {
        return new HashMap<>(options);
    }

    public <T> Option<T> register(Option<T> option) {
        options.put(option.getId(), option);
        return option;
    }

    public void loadOptions(Map<String, Object> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                Option<?> option = this.options.get(key);
                if (option != null) option.load(map.get(key));
            }
        }
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Option<?> option : this.getOptions().values()) {
            map.put(option.getId(), option.getInConfigFormat());
        }

        return map;
    }

    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        for (Option<?> option : this.getOptions().values()) {
            entries.add(option.getConfigEntry());
        }

        return entries;
    }
}
