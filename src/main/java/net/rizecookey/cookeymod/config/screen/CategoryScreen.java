package net.rizecookey.cookeymod.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.config.category.Category;
import net.rizecookey.cookeymod.config.option.Option;

import java.util.ArrayList;
import java.util.List;

public class CategoryScreen extends OptionsSubScreen {
    Category category;
    OptionsList list;

    public CategoryScreen(Category category, Screen previousScreen, Options options) {
        super(previousScreen, options, new TranslatableComponent(category.getTranslationKey()));
        this.category = category;
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        List<net.minecraft.client.Option> mcOptions = new ArrayList<>();
        for (Option<?> option : this.category.getOptions().values()) {
            if (option.getMcOption() != null) {
                if (this.minecraft.font.width(new TranslatableComponent(option.getTranslationKey())) > 150) {
                    this.list.addBig(option.getMcOption());
                }
                else {
                    mcOptions.add(option.getMcOption());
                }
            }
        }
        this.list.addSmall(mcOptions.toArray(new net.minecraft.client.Option[0]));
        this.children.add(this.list);

        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.options.save();
            this.minecraft.getWindow().changeFullscreenVideoMode();
            this.minecraft.setScreen(this.lastScreen);
        }));
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, i, j, f);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(poseStack, i, j, f);
    }
}
