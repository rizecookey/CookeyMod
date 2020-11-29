package net.rizecookey.cookeymod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.rizecookey.cookeymod.screen.ScreenBuilder;

public final class CookeyModCommand implements Command<CommandSourceStack> {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cookeymod").executes(new CookeyModCommand()));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(ScreenBuilder.buildConfig(client.screen));
        return 0;
    }
}
