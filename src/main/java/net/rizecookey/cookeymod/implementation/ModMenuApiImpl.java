package net.rizecookey.cookeymod.implementation;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.rizecookey.cookeymod.screen.ScreenBuilder;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ScreenBuilder::buildConfig;
    }
}
