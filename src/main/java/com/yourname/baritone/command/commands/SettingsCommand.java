package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.gui.SettingsScreen;
import net.minecraft.client.MinecraftClient;

public final class SettingsCommand implements Command {
    private final BaritoneClient baritone;
    public SettingsCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "settings"; }
    public String description() { return "Open settings GUI"; }
    public String usage() { return "#settings"; }
    public void execute(MinecraftClient client, String[] args) {
        client.setScreen(new SettingsScreen(null, baritone.getSettings()));
    }
}
