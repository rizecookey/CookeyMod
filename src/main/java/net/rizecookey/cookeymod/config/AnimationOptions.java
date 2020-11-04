package net.rizecookey.cookeymod.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationOptions implements Category {
    public static final String TRANSLATION_KEY = ModConfig.TRANSLATION_KEY + ".animations";

    boolean swingAndUseItem = true;
    double attackCooldownHandOffset = 0.5D;
    boolean enableOldSwing = false;

    public boolean isSwingAndUseItem() {
        return swingAndUseItem;
    }

    public void setSwingAndUseItem(boolean swingAndUseItem) {
        this.swingAndUseItem = swingAndUseItem;
    }

    public double getAttackCooldownHandOffset() {
        return attackCooldownHandOffset;
    }

    public void setAttackCooldownHandOffset(double attackCooldownHandOffset) {
        this.attackCooldownHandOffset = attackCooldownHandOffset;
    }

    public boolean isEnableOldSwing() {
        return enableOldSwing;
    }

    public void setEnableOldSwing(boolean enableOldSwing) {
        this.enableOldSwing = enableOldSwing;
    }

    public void loadOptions(Map<String, Object> options) {
        if (options != null) {
            this.swingAndUseItem = (Boolean) options.get("swingAndUseItem");
            this.attackCooldownHandOffset = (double) options.get("attackCooldownHandOffset");
            this.enableOldSwing = (Boolean) options.get("enableOldSwing");
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("swingAndUseItem", this.swingAndUseItem);
        map.put("attackCooldownHandOffset", this.attackCooldownHandOffset);
        map.put("enableOldSwing", this.enableOldSwing);
        return map;
    }

    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();

        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".swingAndUseItem"), this.swingAndUseItem)
                        .setSaveConsumer(this::setSwingAndUseItem)
                .build()
        );

        entries.add(
                ConfigEntryBuilder.create()
                        .startDoubleField(new TranslatableComponent(TRANSLATION_KEY + ".attackCooldownHandOffset"), this.attackCooldownHandOffset)
                        .setMax(1.0D).setMin(-1.0D)
                        .setSaveConsumer(this::setAttackCooldownHandOffset)
                .build()
        );

        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".enableOldSwing"), this.enableOldSwing)
                        .setSaveConsumer(this::setEnableOldSwing)
                .build()
        );

        return entries;
    }

    public String getTranslationId() {
        return TRANSLATION_KEY;
    }
}
