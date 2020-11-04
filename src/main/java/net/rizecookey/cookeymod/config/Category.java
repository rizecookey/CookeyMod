package net.rizecookey.cookeymod.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.util.List;
import java.util.Map;

public interface Category {
    void loadOptions(Map<String, Object> options);
    Map<String, Object> toMap();
    List<AbstractConfigListEntry<?>> getConfigEntries();
    String getTranslationId();
}
