package org.sam.commandmod.client.usercmds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.sam.commandmod.client.ConfigClass;

@Mixin(ClientPlayNetworkHandler.class)
public class GmdCmd {

    @Unique
    private static long lastProcessedTime = 0;

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {

        long currentTime = System.currentTimeMillis();

        // Logging the time difference for debugging
        System.out.println("Time since last processed: " + (currentTime - lastProcessedTime) + " ms");

        // Preventing duplicate processing within 100 ms
        if (currentTime - lastProcessedTime < 100) {
            System.out.println("Duplicate message detected, skipping processing.");
            return;
        }

        lastProcessedTime = currentTime;

        processChatMessage(packet.content(), null);
    }

    @Unique
    private void processChatMessage(Text unsignedContent, String bodyContent) {
        // Extract the message content
        Text messageText = unsignedContent != null ? unsignedContent : Text.of(bodyContent);
        String messageContent = messageText.getString();
        System.out.println("Received message: " + messageContent); // Debug log

        // Extract sender's username and actual message
        String[] messageParts = messageContent.split(": ", 2);
        String senderUsername = messageParts.length > 1 ? messageParts[0].trim() : "Unknown";
        String actualMessage = messageParts.length > 1 ? messageParts[1].trim() : messageContent;

        System.out.println("chat content: " + actualMessage); // Log the chat content
        System.out.println("chat sender: " + senderUsername); // Log the chat sender

        if (actualMessage.startsWith("-gm ")) {
            String[] parts = actualMessage.split(" ");
            if (parts.length < 2 || parts.length > 3) {
                sendFeedback("Invalid command syntax");
                return;
            }

            String gamemode = mapGamemode(parts[1]);
            if (gamemode == null) {
                sendFeedback("Invalid gamemode specified");
                return;
            }

            String targetUsername = parts.length == 3 ? parts[2] : senderUsername;

            // Check if the target is the client player
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && targetUsername.equalsIgnoreCase(client.player.getGameProfile().getName())) {
                // Allow the command only if the sender is the client player themselves
                if (!senderUsername.equalsIgnoreCase(client.player.getGameProfile().getName())) {
                    executeCommand("tellraw " + senderUsername + " [\"\",{\"text\":\"[\",\"color\":\"red\"},{\"text\":\"PBuild\",\"color\":\"dark_red\"},{\"text\":\"]\",\"color\":\"red\"},\" \",{\"text\":\"Du kannst das Gamemode dieser Person nicht ändern!\",\"color\":\"yellow\"}]");
                    return;
                }
            }

            // Check if the sender is authorized
            if (ConfigClass.isAuthorizedUser(senderUsername)) {
                System.out.println("Executing gamemode command for: " + targetUsername); // Debug log
                executeCommand("gamemode " + gamemode + " " + targetUsername);
                executeCommand("tellraw " + targetUsername + " [\"\",{\"text\":\"[\",\"color\":\"red\"},{\"text\":\"PBuild\",\"color\":\"dark_red\"},{\"text\":\"]\",\"color\":\"red\"},\" \",{\"text\":\"Dein Gamemode wurde zu \",\"color\":\"yellow\"},{\"text\":\"" + gamemode + "\",\"color\":\"aqua\"},{\"text\":\" gesetzt.\",\"color\":\"yellow\"}]");
            } else {
                executeCommand("tellraw " + senderUsername + " [\"\",{\"text\":\"[\",\"color\":\"red\"},{\"text\":\"PBuild\",\"color\":\"dark_red\"},{\"text\":\"]\",\"color\":\"red\"},\" \",{\"text\":\"Du hast dafür keine Berechtigung!\",\"color\":\"yellow\"}]");
            }
        }
    }

    @Unique
    private String mapGamemode(String gamemodeCode) {
        switch (gamemodeCode) {
            case "0":
                return "survival";
            case "1":
                return "creative";
            case "2":
                return "adventure";
            case "3":
                return "spectator";
            default:
                return null;
        }
    }

    @Unique
    public void executeCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            System.out.println("Executing command: " + command); // Debug log

            // Use the command dispatcher to execute commands
            client.player.networkHandler.sendCommand(command);
        } else {
            System.out.println("Player is null when executing command"); // Debug log
        }
    }

    @Unique
    public void sendFeedback(String feedbackMessage) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.networkHandler != null) {
            System.out.println("Sending feedback: " + feedbackMessage); // Debug log
            // Send the feedback in regular chat
            client.player.networkHandler.sendChatMessage(feedbackMessage);
        } else {
            System.out.println("Player is null when sending feedback"); // Debug log
        }
    }
}
