package com.yourname.baritone.command.commands;

import com.yourname.baritone.BaritoneMod;
import com.yourname.baritone.command.Command;
import com.yourname.baritone.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;

public final class VersionCommand implements Command {
    public String name() { return "version"; }
    public String description() { return "Show mod version"; }
    public String usage() { return "#version"; }
    public void execute(MinecraftClient client, String[] args) {
        ChatUtils.info("Baritone Fabric MVP " + BaritoneMod.VERSION + " for Minecraft 1.21.1");
    }
}
