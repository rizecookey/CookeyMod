package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.GLFW_AUTO_ICONIFY;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_HOVERED;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

@Mixin(Window.class)
public abstract class WindowMixin implements AutoCloseable {
    @Shadow
    @Final
    private long window;

    @Shadow
    public abstract boolean isFullscreen();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 4, remap = false))
    public void disableAutoIconify(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci) {
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE);
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    public void iconifyIfBlocking(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, String string, String string2, CallbackInfo ci) {
        GLFWWindowFocusCallback callback = glfwSetWindowFocusCallback(this.window, (windowId, focussed) -> {
            if (this.isFullscreen() && !focussed && glfwGetWindowAttrib(windowId, GLFW_HOVERED) == 1) {
                glfwIconifyWindow(windowId);
            }
        });

        if (callback != null) {
            callback.close();
        }
    }
}
