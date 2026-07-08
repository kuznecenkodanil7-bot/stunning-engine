package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class StopCommand implements Command {
    private final BaritoneClient baritone;
    public StopCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "stop"; }
    public String description() { return "Stop all actions"; }
    public String usage() { return "#stop"; }
    public void execute(MinecraftClient client, String[] args) {
        baritone.stopAll();
        ChatUtils.success("Stopped");
    }
}
