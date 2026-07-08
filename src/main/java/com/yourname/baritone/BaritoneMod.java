package com.yourname.baritone;

import com.yourname.baritone.api.BaritoneAPI;
import com.yourname.baritone.command.CommandManager;
import com.yourname.baritone.event.GameEventHandler;
import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.settings.SettingsLoader;
import com.yourname.baritone.utils.ChatUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;

public final class BaritoneMod implements ClientModInitializer {
    public static final String MOD_ID = "baritone";
    public static final String VERSION = "0.1.0";

    private static BaritoneMod instance;

    private Settings settings;
    private BaritoneClient baritone;
    private CommandManager commandManager;
    private GameEventHandler eventHandler;

    @Override
    public void onInitializeClient() {
        instance = this;
        settings = SettingsLoader.load();
        baritone = new BaritoneClient(settings);
        commandManager = new CommandManager(baritone);
        eventHandler = new GameEventHandler(baritone);

        BaritoneAPI.setProvider(player -> baritone);
        eventHandler.register();

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (!message.startsWith(settings.commandPrefix)) {
                return true;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            commandManager.dispatch(client, message.substring(settings.commandPrefix.length()).trim());
            return false;
        });

        ChatUtils.info("Baritone Fabric MVP loaded. Type #help");
    }

    public static BaritoneMod getInstance() {
        return instance;
    }

    public Settings getSettings() {
        return settings;
    }

    public BaritoneClient getBaritone() {
        return baritone;
    }
}
