package net.rizecookey.cookeymod.screen;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.setting.Setting;

import java.util.Map;

public final class YACLScreenBuilder {
    private YACLScreenBuilder() {}

    public static Screen buildConfig(Screen previousScreen) {
        ModConfig config = CookeyMod.getInstance().getConfig();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable(ModConfig.TRANSLATION_KEY))
                .save(config::saveConfig)
                .category(createGenericScreenCategory(config.animations()))
                .category(createHudRenderingScreenCategory(config.hudRendering()))
                .category(createMiscScreenCategory(config.misc()))
                .build()
                .generateScreen(previousScreen);
    }

    private static ConfigCategory createGenericScreenCategory(Category category) {
        ConfigCategory.Builder configCategory = ConfigCategory.createBuilder()
                .name(Component.translatable(category.getTranslationKey()));

        for (Map.Entry<String, Setting<?>> entry : category.getSettings().entrySet()) {
            configCategory.option(createOption(entry.getValue()));
        }

        return configCategory.build();
    }

    private static ConfigCategory createHudRenderingScreenCategory(HudRenderingCategory category) {
        ConfigCategory.Builder configCategory = ConfigCategory.createBuilder()
                .name(Component.translatable(category.getTranslationKey()))
                .option(createOption(category.attackCooldownHandOffset()));

        String damageTintCategoryTranslationKey = category.getTranslationKey() + ".damageTint";
        OptionGroup.Builder damageTintGroup = OptionGroup.createBuilder()
                .name(Component.translatable(damageTintCategoryTranslationKey));
        for (Setting<?> setting : new Setting<?>[] {
                category.damageColor(),
                category.showDamageTintOnArmor(),
                category.showDamageTintOnHeldItems(),
                category.showDamageTintOnCape(),
                category.showDamageTintInFirstPerson() }) {
            damageTintGroup.option(createOption(setting));
        }
        configCategory.group(damageTintGroup.build());

        for (Setting<?> setting : new Setting<?>[] {
                category.onlyShowShieldWhenBlocking(),
                category.disableEffectBasedFovChange(),
                category.alternativeBobbing(),
                category.showHandWhenInvisible(),
                category.invisibilityHandOpacity() }) {
            configCategory.option(createOption(setting));
        }

        return configCategory.build();
    }

    private static ConfigCategory createMiscScreenCategory(MiscCategory category) {
        ConfigCategory.Builder configCategory = ConfigCategory.createBuilder()
                .name(Component.translatable(category.getTranslationKey()));
        configCategory.option(createOption(category.showOwnNameInThirdPerson()));
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            configCategory.option(createOption(category.showModButton()));
        }
        configCategory.option(createOption(category.fixCooldownDesync()));

        return configCategory.build();
    }

    private static Option<?> createOption(Setting<?> setting) {
        return setting.accept(YACLOptionBuilder.INSTANCE, null);
    }
}
