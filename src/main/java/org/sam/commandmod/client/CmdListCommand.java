package org.sam.commandmod.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class CmdListCommand {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(CmdListCommand::register);
    }

    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("cmdlist")
                .executes(CmdListCommand::executeList));
    }

    private static int executeList(CommandContext<FabricClientCommandSource> context) {
        // Retrieve the list of authorized users
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("Authorized Users:"), false);
            for (String username : ConfigClass.getAuthorizedUsers()) {
                client.player.sendMessage(Text.literal("- " + username), false);
            }
        }
        return 1;
    }
}
