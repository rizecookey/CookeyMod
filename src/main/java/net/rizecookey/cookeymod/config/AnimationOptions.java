package net.rizecookey.cookeymod.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.events.OverlayReloadListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Function;

public class AnimationOptions implements Category {
    public static final String TRANSLATION_KEY = ModConfig.TRANSLATION_KEY + ".animations";
    public static final String OPTIONS_KEY = ModConfig.TRANSLATION_KEY + ".generic.options";
    public static final Function<Long, Component> DOUBLE_SLIDER_TEXT = value -> { {
            if (value == 0) {
                return new TranslatableComponent(OPTIONS_KEY + ".off");
            }
            return new TextComponent(new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US))
                    .format(value / 100.0) );
        }
    };
    public static final AnimationOptions DEFAULTS = new AnimationOptions();

    boolean swingAndUseItem = true;
    double attackCooldownHandOffset = 0.0D;
    //boolean enableOldSwing = false;
    Color damageColor = Color.ofRGBA(255, 0, 0, 77);
    boolean showDamageTintOnArmor = false;
    double sneakAnimationSpeed = 1.0F;

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

    /*public boolean isEnableOldSwing() {
        return enableOldSwing;
    }

    public void setEnableOldSwing(boolean enableOldSwing) {
        this.enableOldSwing = enableOldSwing;
    }*/

    public Color getDamageColor() {
        return damageColor;
    }

    public void setDamageColor(Color damageColor) {
        this.damageColor = damageColor;
        OverlayReloadListener.callEvent();
    }

    public boolean isShowDamageTintOnArmor() {
        return showDamageTintOnArmor;
    }

    public void setShowDamageTintOnArmor(boolean showDamageTintOnArmor) {
        this.showDamageTintOnArmor = showDamageTintOnArmor;
    }

    public double getSneakAnimationSpeed() {
        return sneakAnimationSpeed;
    }

    public void setSneakAnimationSpeed(double sneakAnimationSpeed) {
        this.sneakAnimationSpeed = sneakAnimationSpeed;
    }



    public void loadOptions(Map<String, Object> options) {
        if (options != null) {
            this.swingAndUseItem = (Boolean) options.getOrDefault("swingAndUseItem", this.swingAndUseItem);
            this.attackCooldownHandOffset = (double) options.getOrDefault("attackCooldownHandOffset", this.attackCooldownHandOffset);
            //this.enableOldSwing = (Boolean) options.getOrDefault("enableOldSwing", this.enableOldSwing);
            this.damageColor = Color.ofTransparent((int) (long) options.getOrDefault("damageColor", (long) this.damageColor.getColor()));
            this.showDamageTintOnArmor = (Boolean) options.getOrDefault("showDamageTintOnArmor", this.showDamageTintOnArmor);
            this.sneakAnimationSpeed = (double) options.getOrDefault("sneakAnimationSpeed", this.sneakAnimationSpeed);
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("swingAndUseItem", this.swingAndUseItem);
        map.put("attackCooldownHandOffset", this.attackCooldownHandOffset);
        //map.put("enableOldSwing", this.enableOldSwing);
        map.put("damageColor", this.damageColor.getColor());
        map.put("showDamageTintOnArmor", this.showDamageTintOnArmor);
        map.put("sneakAnimationSpeed", this.sneakAnimationSpeed);
        return map;
    }



    public List<AbstractConfigListEntry<?>> getConfigEntries() {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();

        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".swingAndUseItem"), this.swingAndUseItem)
                        .setSaveConsumer(this::setSwingAndUseItem)
                        .setDefaultValue(DEFAULTS.isSwingAndUseItem())
                .build()
        );

        entries.add(
                ConfigEntryBuilder.create()
                        .startLongSlider(new TranslatableComponent(TRANSLATION_KEY + ".attackCooldownHandOffset"), (long) (this.attackCooldownHandOffset * 100.0F), -100, 100)
                        .setTextGetter(DOUBLE_SLIDER_TEXT)
                        .setSaveConsumer(value -> this.setAttackCooldownHandOffset(value / 100.0F))
                        .setDefaultValue((long) (DEFAULTS.getAttackCooldownHandOffset() * 100.0F))
                .build()
        );

        /*
        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".enableOldSwing"), this.enableOldSwing)
                        .setSaveConsumer(this::setEnableOldSwing)
                        .setDefaultValue(DEFAULTS.isEnableOldSwing())
                .build()
        );
         */

        entries.add(
                ConfigEntryBuilder.create()
                        .startAlphaColorField(new TranslatableComponent(TRANSLATION_KEY + ".damageColor"), this.damageColor)
                        .setSaveConsumer2(this::setDamageColor)
                        .setAlphaMode(true)
                        .setDefaultValue(DEFAULTS.getDamageColor().getColor())
                .build()
        );

        entries.add(
                ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableComponent(TRANSLATION_KEY + ".showDamageTintOnArmor"), this.showDamageTintOnArmor)
                        .setSaveConsumer(this::setShowDamageTintOnArmor)
                        .setDefaultValue(DEFAULTS.isShowDamageTintOnArmor())
                .build()
        );

        entries.add(
                ConfigEntryBuilder.create()
                        .startLongSlider(new TranslatableComponent(TRANSLATION_KEY + ".sneakAnimationSpeed"), (long) (this.sneakAnimationSpeed * 100.0F), 0, 200)
                        .setTextGetter(DOUBLE_SLIDER_TEXT)
                        .setSaveConsumer((value) -> this.setSneakAnimationSpeed(value / 100.0F))
                        .setDefaultValue((long) (DEFAULTS.getSneakAnimationSpeed() * 100.0F))
                .build()
        );

        return entries;
    }

    public String getTranslationId() {
        return TRANSLATION_KEY;
    }
}
