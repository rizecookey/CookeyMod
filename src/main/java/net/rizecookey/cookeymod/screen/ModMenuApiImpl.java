package net.rizecookey.cookeymod.screen;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.Category;
import net.rizecookey.cookeymod.config.ModConfig;

import java.io.IOException;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) screen -> {
            ModConfig config = CookeyMod.getInstance().getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setTitle(new TranslatableComponent(ModConfig.TRANSLATION_KEY))
                    .setSavingRunnable(() -> {
                        try {
                            config.saveConfig();
                        } catch (IOException e) {
                            CookeyMod.getInstance().getLogger().error("Failed to save CookeyMod config file!");
                            e.printStackTrace();
                        }
                    })
                    .setParentScreen(screen);

            for (String id : config.getCategories().keySet()) {
                Category category = config.getCategory(id);
                ConfigCategory configCategory = builder.getOrCreateCategory(new TranslatableComponent(ModConfig.TRANSLATION_KEY + "." + id));

                for (AbstractConfigListEntry<?> entry : category.getConfigEntries()) {
                    configCategory.addEntry(entry);
                }
            }

            return builder.build();
        };
    }
}
