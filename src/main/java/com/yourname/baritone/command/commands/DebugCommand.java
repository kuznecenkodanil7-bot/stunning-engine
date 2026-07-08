package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class DebugCommand implements Command {
    private final BaritoneClient baritone;
    public DebugCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "debug"; }
    public String description() { return "Toggle debug mode"; }
    public String usage() { return "#debug"; }
    public void execute(MinecraftClient client, String[] args) {
        baritone.toggleDebug();
        ChatUtils.info("Debug: " + baritone.isDebug());
    }
}
