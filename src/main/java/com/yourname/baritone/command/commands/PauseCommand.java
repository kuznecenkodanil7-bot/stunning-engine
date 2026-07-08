package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class PauseCommand implements Command {
    private final BaritoneClient baritone;
    public PauseCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "pause"; }
    public String description() { return "Pause current actions"; }
    public String usage() { return "#pause"; }
    public void execute(MinecraftClient client, String[] args) {
        baritone.pause();
        ChatUtils.info("Paused");
    }
}
