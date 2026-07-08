package com.yourname.baritone.command;

import net.minecraft.client.MinecraftClient;

public interface Command {
    String name();
    String description();
    String usage();
    void execute(MinecraftClient client, String[] args);
}
