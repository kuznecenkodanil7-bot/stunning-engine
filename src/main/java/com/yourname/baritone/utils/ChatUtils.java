package com.yourname.baritone.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ChatUtils {
    private ChatUtils() {
    }

    public static void info(String message) {
        send(Formatting.GOLD, message);
    }

    public static void error(String message) {
        send(Formatting.RED, message);
    }

    public static void success(String message) {
        send(Formatting.GREEN, message);
    }

    public static void send(Formatting color, String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.inGameHud == null) {
            return;
        }
        client.inGameHud.getChatHud().addMessage(Text.literal("[Baritone] ").formatted(color).append(Text.literal(message).formatted(Formatting.WHITE)));
    }
}
