package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneClient;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class ResumeCommand implements Command {
    private final BaritoneClient baritone;
    public ResumeCommand(BaritoneClient baritone) { this.baritone = baritone; }
    public String name() { return "resume"; }
    public String description() { return "Resume paused actions"; }
    public String usage() { return "#resume"; }
    public void execute(MinecraftClient client, String[] args) {
        baritone.resume();
        ChatUtils.info("Resumed");
    }
}
