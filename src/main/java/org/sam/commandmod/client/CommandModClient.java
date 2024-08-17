package org.sam.commandmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.sam.commandmod.client.usercmds.KillCmd;

public class CommandModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UserCommandAdder.registerCommands();

        // Register the command in the client initialization phase
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            SetSpawnCommand.register(dispatcher);
        });

        // Register the /cmdlist command
        CmdListCommand.registerCommands();

    }
}
