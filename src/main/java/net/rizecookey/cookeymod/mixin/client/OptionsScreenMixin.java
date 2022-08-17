package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.Option;
import net.rizecookey.cookeymod.screen.ScreenBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Component component) {
        super(component);
    }

    Option<Boolean> showModButton = CookeyMod.getInstance().getConfig().getCategory(MiscCategory.class).showModButton;

    @Inject(method = "init", at = @At("TAIL"))
    public void injectCookeyModButton(CallbackInfo ci) {
        if (this.showModButton.get()) {
            this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 24 - 6, 150, 20, new TranslatableComponent("options.cookeymod.button"), (button) -> {
                assert this.minecraft != null;
                this.minecraft.setScreen(ScreenBuilder.buildConfig(this.minecraft.screen));
            }));
        }
    }
}
