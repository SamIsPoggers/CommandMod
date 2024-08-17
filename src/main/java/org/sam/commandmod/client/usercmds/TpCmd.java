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
public class TpCmd {

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

        if (actualMessage.startsWith("-tp ")) {
            handleTpCommand(senderUsername, actualMessage);
        } else if (actualMessage.startsWith("-tphere ")) {
            handleTpHereCommand(senderUsername, actualMessage);
        }
    }

    @Unique
    private void handleTpCommand(String senderUsername, String actualMessage) {
        String[] parts = actualMessage.split(" ", 2);
        if (parts.length == 2) {
            String targetUsername = parts[1];

            // Check if the sender is authorized
            if (ConfigClass.isAuthorizedUser(senderUsername)) {
                System.out.println("Executing tp command: /tp " + senderUsername + " " + targetUsername); // Debug log
                executeCommand("tp " + senderUsername + " " + targetUsername);
            } else {
                executeCommand("tellraw " + senderUsername + " [\"\",{\"text\":\"[\",\"color\":\"red\"},{\"text\":\"PBuild\",\"color\":\"dark_red\"},{\"text\":\"]\",\"color\":\"red\"},\" \",{\"text\":\"Du hast dafür keine Berechtigung!\",\"color\":\"yellow\"}]");
            }
        }
    }

    @Unique
    private void handleTpHereCommand(String senderUsername, String actualMessage) {
        String[] parts = actualMessage.split(" ", 2);
        if (parts.length == 2) {
            String targetUsername = parts[1];

            // Check if the sender is authorized
            if (ConfigClass.isAuthorizedUser(senderUsername)) {
                System.out.println("Executing tphere command: /tp " + targetUsername + " " + senderUsername); // Debug log
                executeCommand("tp " + targetUsername + " " + senderUsername);
            } else {
                executeCommand("tellraw " + senderUsername + " [\"\",{\"text\":\"[\",\"color\":\"red\"},{\"text\":\"PBuild\",\"color\":\"dark_red\"},{\"text\":\"]\",\"color\":\"red\"},\" \",{\"text\":\"Du hast dafür keine Berechtigung!\",\"color\":\"yellow\"}]");
            }
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
