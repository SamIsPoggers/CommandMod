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
public class SpawnCmd {

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

        if (actualMessage.equals("-spawn")) {
            // Directly teleport the player to the spawn coordinates
            ConfigClass.readCoordinates().ifPresentOrElse(coordinates -> {
                System.out.println("Teleporting to coordinates: " + coordinates[0] + " " + coordinates[1] + " " + coordinates[2]); // Debug log
                executeCommand("tp " + senderUsername + " " + coordinates[0] + " " + coordinates[1] + " " + coordinates[2]);
            }, () -> {
                sendFeedback("Failed to read coordinates from file");
            });
        }
    }

    @Unique
    public void executeCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            System.out.println("Executing command: " + command); // Debug log
            client.player.networkHandler.sendCommand(command);
        } else {
            System.out.println("Player is null when executing command"); // Debug log
        }
    }

    @Unique
    public void sendFeedback(String feedbackMessage) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            System.out.println("Sending feedback: " + feedbackMessage); // Debug log
            // Send the feedback in regular chat
            client.player.networkHandler.sendChatMessage(feedbackMessage);
        } else {
            System.out.println("Player is null when sending feedback"); // Debug log
        }
    }
}
