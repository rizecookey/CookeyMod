package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
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
    @Shadow
    protected abstract Button openScreenButton(Component component, Supplier<Screen> supplier);

    protected OptionsScreenMixin(Component component) {
        super(component);
    }


    BooleanOption showModButton = CookeyMod.getInstance().getConfig().misc().showModButton();

    @Inject(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    shift = At.Shift.AFTER, ordinal = 11),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void injectCookeyModButton(CallbackInfo ci, GridLayout gridLayout, GridLayout.RowHelper rowHelper) {
        if (this.showModButton.get()) {
            rowHelper.addChild(this.openScreenButton(Component.translatable("options.cookeymod.button"),
                    () -> {
                        assert this.minecraft != null;
                        return ScreenBuilder.buildConfig(this.minecraft.screen);
                    }));
        }
    }
}
