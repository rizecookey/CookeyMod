package net.rizecookey.cookeymod.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscOptions implements Category {
    public static final String TRANSLATION_KEY = ModConfig.TRANSLATION_KEY + ".misc";

    boolean showOwnNameInThirdPerson = false;

    public boolean isShowOwnNameInThirdPerson() {
        return showOwnNameInThirdPerson;
    }

    public void setShowOwnNameInThirdPerson(boolean showOwnNameInThirdPerson) {
        this.showOwnNameInThirdPerson = showOwnNameInThirdPerson;
    }

    @Override
    public void loadOptions(Map<String, Object> options) {
        this.showOwnNameInThirdPerson = (boolean) options.get("showOwnNameInThirdPerson");
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("showOwnNameInThirdPerson", this.showOwnNameInThirdPerson);
        return map;
    }

    @Override
    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();

        entries.add(
                ConfigEntryBuilder.create().
                        startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".showOwnNameInThirdPerson"), this.showOwnNameInThirdPerson)
                        .setSaveConsumer(this::setShowOwnNameInThirdPerson)
                .build()
        );

        return entries;
    }

    @Override
    public String getTranslationId() {
        return TRANSLATION_KEY;
    }
}
