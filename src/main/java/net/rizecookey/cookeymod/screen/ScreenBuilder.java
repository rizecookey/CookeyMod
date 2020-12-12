package net.rizecookey.cookeymod.screen;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.Category;

import java.io.IOException;

public abstract class ScreenBuilder {
    public static Screen buildConfig(Screen prevScreen) {
        ModConfig config = CookeyMod.getInstance().getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(new TranslatableComponent(ModConfig.TRANSLATION_KEY))
                .setTransparentBackground(Minecraft.getInstance().level != null)
                .setSavingRunnable(() -> {
                    try {
                        config.saveConfig();
                    } catch (IOException e) {
                        CookeyMod.getInstance().getLogger().error("Failed to save CookeyMod config file!");
                        e.printStackTrace();
                    }
                });
                if (prevScreen != null) builder.setParentScreen(prevScreen);

        for (String id : config.getCategories().keySet()) {
            Category category = config.getCategory(id);
            ConfigCategory configCategory = builder.getOrCreateCategory(new TranslatableComponent(category.getTranslationKey()));

            for (AbstractConfigListEntry<?> entry : category.getConfigEntries()) {
                configCategory.addEntry(entry);
            }
        }

        return builder.build();
    }
}
