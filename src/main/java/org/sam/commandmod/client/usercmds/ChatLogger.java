package org.sam.commandmod.client.usercmds;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatLogger {

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    public void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
        logChatMessage(packet.unsignedContent(), packet.body().content());
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        logChatMessage(packet.content(), null);
    }

    private void logChatMessage(Text unsignedContent, String bodyContent) {
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
    }
}
