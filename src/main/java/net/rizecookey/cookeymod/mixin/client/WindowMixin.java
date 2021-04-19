package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.*;

@Mixin(Window.class)
public abstract class WindowMixin implements AutoCloseable {
    @Shadow @Final private long window;

    @Shadow public abstract boolean isFullscreen();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 4))
    public void disableAutoIconify(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci) {
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE);
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    public void iconifyIfBlocking(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci) {
        glfwSetWindowFocusCallback(this.window, (window, focussed) -> {
            if (this.isFullscreen() && !focussed && glfwGetWindowAttrib(window, GLFW_HOVERED) == 1) {
                glfwIconifyWindow(window);
            }
        });
    }
}
