package org.sam.commandmod.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class UserCommandAdder {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(UserCommandAdder::register);
    }

    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("cmduser")
                .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("username", StringArgumentType.string())
                                .executes(ctx -> executeAdd(ctx, getString(ctx, "username")))))
                .then(ClientCommandManager.literal("remove")
                        .then(ClientCommandManager.argument("username", StringArgumentType.string())
                                .executes(ctx -> executeRemove(ctx, getString(ctx, "username"))))));
    }

    private static int executeAdd(CommandContext<FabricClientCommandSource> context, String username) {
        ConfigClass.addUsername(username);
        sendFeedback(context, "Added username: " + username);
        return 1;
    }

    private static int executeRemove(CommandContext<FabricClientCommandSource> context, String username) {
        ConfigClass.removeUsername(username);
        sendFeedback(context, "Removed username: " + username);
        return 1;
    }

    private static void sendFeedback(CommandContext<FabricClientCommandSource> context, String message) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal(message), false);
    }
}
