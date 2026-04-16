package net.rizecookey.cookeymod.screen;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.Option;

import java.io.IOException;
import java.util.Map;

public final class ScreenBuilder {
    private ScreenBuilder() {
    }

    public static Screen buildConfig(Screen prevScreen) {
        ModConfig config = CookeyMod.getInstance().getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.translatable(ModConfig.TRANSLATION_KEY))
                .setSavingRunnable(() -> {
                    try {
                        config.saveConfig();
                    } catch (IOException e) {
                        CookeyMod.getInstance().getLogger().error("Failed to save CookeyMod config file", e);
                    }
                });
        if (prevScreen != null) builder.setParentScreen(prevScreen);
        createGenericScreenCategory(builder, config.animations());
        createHudRenderingScreenCategory(builder, config.hudRendering());
        createMiscScreenCategory(builder, config.misc());

        return builder.build();
    }

    private static void createGenericScreenCategory(ConfigBuilder builder, Category category) {
        ConfigCategory configCategory = builder.getOrCreateCategory(Component.translatable(category.getTranslationKey()));

        for (Map.Entry<String, Option<?>> entry : category.getOptions().entrySet()) {
            createCategoryEntry(configCategory, entry.getValue());
        }
    }

    private static void createHudRenderingScreenCategory(ConfigBuilder builder, HudRenderingCategory category) {
        ConfigCategory configCategory = builder.getOrCreateCategory(Component.translatable(category.getTranslationKey()));
        createCategoryEntry(configCategory, category.attackCooldownHandOffset());

        String damageTintCategoryTranslationKey = category.getTranslationKey() + ".damageTint";
        SubCategoryBuilder damageTintCategoryBuilder = ConfigEntryBuilder.create()
                .startSubCategory(Component.translatable(damageTintCategoryTranslationKey))
                .setTooltip(Option.getTooltip(damageTintCategoryTranslationKey));
        for (Option<?> option : new Option<?>[] {
                category.damageColor(),
                category.showDamageTintOnArmor(),
                category.showDamageTintOnHeldItems(),
                category.showDamageTintOnCape(),
                category.showDamageTintInFirstPerson() }) {
            damageTintCategoryBuilder.add(createEntry(option));
        }
        configCategory.addEntry(damageTintCategoryBuilder.build());

        for (Option<?> option : new Option<?>[] {
                category.onlyShowShieldWhenBlocking(),
                category.disableEffectBasedFovChange(),
                category.alternativeBobbing(),
                category.showHandWhenInvisible(),
                category.invisibilityHandOpacity() }) {
            createCategoryEntry(configCategory, option);
        }
    }

    private static void createMiscScreenCategory(ConfigBuilder builder, MiscCategory category) {
        ConfigCategory configCategory = builder.getOrCreateCategory(Component.translatable(category.getTranslationKey()));
        createCategoryEntry(configCategory, category.showOwnNameInThirdPerson());
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            createCategoryEntry(configCategory, category.showModButton());
        }
        createCategoryEntry(configCategory, category.fixCooldownDesync());
    }

    private static void createCategoryEntry(ConfigCategory configCategory, Option<?> option) {
        configCategory.addEntry(createEntry(option));
    }

    private static AbstractConfigListEntry<?> createEntry(Option<?> option) {
        return option.accept(OptionEntryBuilder.INSTANCE, ConfigEntryBuilder.create());
    }
}
