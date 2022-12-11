package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.Option;
import net.rizecookey.cookeymod.screen.ScreenBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Shadow protected abstract Button openScreenButton(Component component, Supplier<Screen> supplier);

    protected OptionsScreenMixin(Component component) {
        super(component);
    }

    Option<Boolean> showModButton = CookeyMod.getInstance().getConfig().getCategory(MiscCategory.class).showModButton;

    @Inject(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/GridWidget$RowHelper;addChild(Lnet/minecraft/client/gui/components/AbstractWidget;)Lnet/minecraft/client/gui/components/AbstractWidget;",
                    shift = At.Shift.AFTER, ordinal = 10),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void injectCookeyModButton(CallbackInfo ci, GridWidget gridWidget, GridWidget.RowHelper rowHelper) {
        if (this.showModButton.get()) {
            rowHelper.addChild(this.openScreenButton(MutableComponent.create(new TranslatableContents("options.cookeymod.button")),
                    () -> {
                        assert this.minecraft != null;
                        return ScreenBuilder.buildConfig(this.minecraft.screen);
                    }));
        }
    }
}
