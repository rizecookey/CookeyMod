package net.rizecookey.cookeymod.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.Category;

public class ModConfigScreen extends Screen {
    Screen previousScreen;
    ModConfig modConfig;

    public ModConfigScreen(Screen previousScreen, ModConfig modConfig) {
        super(new TranslatableComponent(modConfig.getTranslationKey()));
        this.previousScreen = previousScreen;
        this.modConfig = modConfig;
    }

    public void addButton(Component label, Button.OnPress onPress) {
        int i = this.buttons.size();
        int x = i % 2 == 0 ? this.width / 2 - 155 : this.width / 2 + 5;
        int y = this.height / 6 + 12 - 6 + (int) (24 * Math.floor(i / 2.0));

        this.addButton(new Button(x, y, 150, 20, label, onPress));
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        for (Category category : this.modConfig.getCategories().values()) {
            this.addButton(new TranslatableComponent(category.getTranslationKey()), button -> this.minecraft.setScreen(new CategoryScreen(category, this, this.minecraft.options)));
        }

        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 12 + (int) (24 * Math.floor(this.buttons.size() / 2.0 + 2)), 200, 20, CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.setScreen(this.previousScreen);
        }));
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(poseStack, i, j, f);
    }
}
