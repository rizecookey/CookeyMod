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
    public static final MiscOptions DEFAULTS = new MiscOptions();

    boolean showOwnNameInThirdPerson = false;
    boolean force100PercentRecharge = false;

    public boolean isShowOwnNameInThirdPerson() {
        return showOwnNameInThirdPerson;
    }

    public void setShowOwnNameInThirdPerson(boolean showOwnNameInThirdPerson) {
        this.showOwnNameInThirdPerson = showOwnNameInThirdPerson;
    }

    public boolean isForce100PercentRecharge() {
        return force100PercentRecharge;
    }

    public void setForce100PercentRecharge(boolean force100PercentRecharge) {
        this.force100PercentRecharge = force100PercentRecharge;
    }

    @Override
    public void loadOptions(Map<String, Object> options) {
        this.showOwnNameInThirdPerson = (boolean) options.getOrDefault("showOwnNameInThirdPerson", this.showOwnNameInThirdPerson);
        this.force100PercentRecharge = (boolean) options.getOrDefault("force100PercentRecharge", this.force100PercentRecharge);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("showOwnNameInThirdPerson", this.showOwnNameInThirdPerson);
        map.put("force100PercentRecharge", this.force100PercentRecharge);
        return map;
    }

    @Override
    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();

        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".showOwnNameInThirdPerson"), this.showOwnNameInThirdPerson)
                        .setSaveConsumer(this::setShowOwnNameInThirdPerson)
                        .setDefaultValue(DEFAULTS.isShowOwnNameInThirdPerson())
                .build()
        );

        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".force100PercentRecharge"), this.force100PercentRecharge)
                        .setSaveConsumer(this::setForce100PercentRecharge)
                        .setDefaultValue(DEFAULTS.isForce100PercentRecharge())
                .build()
        );

        return entries;
    }

    @Override
    public String getTranslationId() {
        return TRANSLATION_KEY;
    }
}
